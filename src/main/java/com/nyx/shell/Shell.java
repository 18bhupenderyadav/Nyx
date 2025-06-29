package com.nyx.shell;

import com.nyx.shell.util.AntlrTokenizer;
import com.nyx.shell.util.ExternalCommandRunner;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Shell class implements the main interactive loop.
 * Features:
 * <ul>
 *   <li>Command execution and tracking</li>
 *   <li>I/O redirection handling</li>
 *   <li>Signal handling (Ctrl+C, Ctrl+D)</li>
 *   <li>Resource cleanup</li>
 *   <li>Error recovery</li>
 * </ul>
 */
public class Shell {
    private final CommandRegistry commandRegistry;
    private final AtomicBoolean running;
    private final AtomicInteger lastExitCode;
    private boolean exitCodeShown;  // Added flag to track if exit code was shown
    private PrintStream shellOutput;
    private PrintStream shellError;
    private volatile Thread currentCommandThread;

    public Shell(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
        this.running = new AtomicBoolean(true);
        this.lastExitCode = new AtomicInteger(0);
        this.exitCodeShown = false;  // Initialize the flag
        this.shellOutput = System.out;
        this.shellError = System.err;
        
        setupSignalHandlers();
    }

    /**
     * Sets up handlers for system signals and shutdown
     */
    private void setupSignalHandlers() {
        // Handle SIGINT (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            Thread cmdThread = currentCommandThread;
            if (cmdThread != null) {
                cmdThread.interrupt();
            }
        }));

        // Handle clean shutdown
        Thread shutdownThread = new Thread(() -> {
            try {
                cleanup();
            } catch (Exception e) {
                // Ignore exceptions during shutdown
            }
        });
        shutdownThread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Runs the shell's main interactive loop
     */
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (running.get()) {
                try {
                    displayPrompt();
                    if (!scanner.hasNextLine()) {
                        // Handle EOF (Ctrl+D)
                        break;
                    }
                    
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) continue;

                    int exitCode = processCommand(input);
                    lastExitCode.set(exitCode);

                } catch (IOException e) {
                    handleCommandFailure("I/O operation", e);
                } catch (InterruptedException e) {
                    // Restore interrupt status and continue
                    Thread.currentThread().interrupt();
                    shellError.println("\nCommand interrupted");
                } catch (Exception e) {
                    handleCommandFailure("command execution", e);
                }
            }
        } finally {
            cleanup();
        }
    }

    /**
     * Displays the shell prompt with additional context
     */
    private void displayPrompt() {
        int lastExit = lastExitCode.get();
        if (lastExit != 0 && !exitCodeShown) {  // Only show if not shown before
            shellOutput.print("[" + lastExit + "]");
            exitCodeShown = true;  // Mark as shown
        }
        shellOutput.print("$ ");
        shellOutput.flush();
    }

    /**
     * Processes a command line input
     * @return The exit code from the command
     */
    private int processCommand(String input) throws IOException, InterruptedException {
        exitCodeShown = false;  // Reset the flag when processing new command
        String[] tokens = AntlrTokenizer.tokenize(input);
        if (tokens.length == 0) return 0;

        CommandLineParser.ParseResult parseResult = CommandLineParser.parse(tokens);
        if (parseResult.hasError()) {
            shellError.println("Error: " + parseResult.errorMessage);
            return Command.MISUSE_OF_SHELL_BUILTINS;
        }

        CommandLine commandLine = parseResult.commandLine;
        String commandName = commandLine.getCommandName();
        String[] args = commandLine.getArguments().toArray(new String[0]);
        
        return executeCommand(commandLine, commandName, args);
    }

    /**
     * Executes a command with proper thread management
     */
    private int executeCommand(CommandLine commandLine, String commandName, String[] args) 
            throws IOException, InterruptedException {
        currentCommandThread = Thread.currentThread();
        try {
            return executeCommandInternal(commandLine, commandName, args);
        } finally {
            currentCommandThread = null;
        }
    }

    /**
     * Internal command execution logic
     */
    private int executeCommandInternal(CommandLine commandLine, String commandName, String[] args) 
            throws IOException {
        Command command = commandRegistry.getCommand(commandName).orElse(null);
        
        if (command != null) {
            return executeBuiltinCommand(command, commandLine, args);
        } else {
            return executeExternalCommand(commandLine, commandName, args);
        }
    }

    /**
     * Executes a builtin command with redirection handling
     */
    private int executeBuiltinCommand(Command command, CommandLine commandLine, String[] args) 
            throws IOException {
        if (hasRedirection(commandLine)) {
            return executeWithRedirection(command, commandLine, args);
        } else {
            return command.execute(args);
        }
    }

    private boolean hasRedirection(CommandLine commandLine) {
        return commandLine.getStdoutRedirect() != null || 
               commandLine.getStderrRedirect() != null;
    }

    /**
     * Executes a command with output redirection
     */
    private int executeWithRedirection(Command command, CommandLine commandLine, String[] args) 
            throws IOException {
        try (ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
             ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
             PrintStream capturedOut = new PrintStream(baosOut);
             PrintStream capturedErr = new PrintStream(baosErr)) {

            // Redirect output streams
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            System.setOut(capturedOut);
            System.setErr(capturedErr);

            try {
                int exitCode = command.execute(args);
                handleRedirectedOutput(commandLine, baosOut.toString(), baosErr.toString());
                return exitCode;
            } finally {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        }
    }

    /**
     * Handles redirected output writing
     */
    private void handleRedirectedOutput(CommandLine commandLine, String outContent, String errContent) 
            throws IOException {
        String stdoutRedirect = commandLine.getStdoutRedirect();
        String stderrRedirect = commandLine.getStderrRedirect();

        if (stdoutRedirect != null) {
            writeToFile(stdoutRedirect, outContent, commandLine.isStdoutAppend());
        } else {
            shellOutput.print(outContent);
        }

        if (stderrRedirect != null) {
            writeToFile(stderrRedirect, errContent, commandLine.isStderrAppend());
        } else {
            shellError.print(errContent);
        }
    }

    /**
     * Writes content to a file with proper resource handling
     */
    private void writeToFile(String path, String content, boolean append) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs(); // Create parent directories if needed
        
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(file, append)))) {
            writer.print(content);
        }
    }

    /**
     * Executes an external command
     */
    private int executeExternalCommand(CommandLine commandLine, String commandName, String[] args) {
        return ExternalCommandRunner.runExternalCommand(
            commandName, 
            args,
            commandLine.getStdoutRedirect(), 
            commandLine.isStdoutAppend(),
            commandLine.getStderrRedirect(), 
            commandLine.isStderrAppend()
        );
    }

    /**
     * Handles command failures with detailed error reporting
     */
    private void handleCommandFailure(String command, Exception e) {
        String errorMessage = String.format("Error executing %s: %s (%s)", 
            command, e.getMessage(), e.getClass().getSimpleName());
        shellError.println(errorMessage);
        
        if (e instanceof InterruptedException) {
            lastExitCode.set(130); // Standard interrupt exit code
        } else {
            lastExitCode.set(Command.GENERAL_ERROR);
        }
    }

    /**
     * Performs cleanup of resources
     */
    private void cleanup() {
        try {
            shellOutput.flush();
            shellError.flush();
        } catch (Exception e) {
            // Ignore exceptions during cleanup
        }
    }

    // For testing and customization
    public void setShellOutput(PrintStream output) {
        this.shellOutput = output;
    }

    public void setShellError(PrintStream error) {
        this.shellError = error;
    }

    public int getLastExitCode() {
        return lastExitCode.get();
    }
}
