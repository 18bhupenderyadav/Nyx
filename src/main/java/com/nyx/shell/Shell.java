package com.nyx.shell;

import com.nyx.shell.util.AntlrTokenizer;
import com.nyx.shell.util.ExternalCommandRunner;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * The Shell class implements the main interactive loop.
 * It performs the following steps:
 *   Reads a line of input from the user.
 *   Uses the ANTLR-based tokenizer to split the input into tokens (handling quotes, escapes, etc.).
 *   Passes the tokens to CommandLineParser to build a structured CommandLine object that includes:
 *       The command name.
 *       Arguments.
 *       Redirection targets for stdout and stderr.
 *   If the command is built into the shell, it executes it and, if redirection is specified, captures and writes its output.
 *   If the command is external, it delegates execution to ExternalCommandRunner which uses ProcessBuilder for redirection.
 */
public class Shell {
    private final CommandRegistry myCommandRegistry;

    public Shell(CommandRegistry commandRegistry) {
        this.myCommandRegistry = commandRegistry;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        // Main shell loop: process each line of input until end-of-file.
        while (true) {
            System.out.print("$ ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            // Tokenize the input line using our ANTLR-based tokenizer.
            String[] tokens = AntlrTokenizer.tokenize(input);
            if (tokens.length == 0) continue;

            // Parse the tokens into a structured CommandLine object.
            CommandLine commandLine = CommandLineParser.parse(tokens);
            if (commandLine == null) continue; // Skip if no valid command is found.

            // Retrieve command components from the parsed CommandLine.
            String commandName = commandLine.getCommandName();
            String[] args = commandLine.getArguments().toArray(new String[0]);
            String stdoutRedirect = commandLine.getStdoutRedirect();
            String stderrRedirect = commandLine.getStderrRedirect();

            // Check if the command is a built-in command.
            Command command = myCommandRegistry.getCommand(commandName);
            if (command != null) {
                // If redirection is specified for built-ins, capture output.
                if (stdoutRedirect != null || stderrRedirect != null) {

                    // Create separate ByteArrayOutputStreams for stdout and stderr.
                    ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
                    ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

                    // Save the original System.out and System.err.
                    PrintStream originalOut = System.out;
                    PrintStream originalErr = System.err;

                    // Redirect System.out and System.err to our capturing streams.
                    System.setOut(new PrintStream(baosOut));
                    System.setErr(new PrintStream(baosErr));

                    // Execute the built-in command.
                    command.execute(args);

                    // Flush the file handlers.
                    System.out.flush();
                    System.err.flush();

                    // Restore the original output stream.
                    System.setOut(originalOut);
                    System.setErr(originalErr);

                    // Retrieve the captured outputs.
                    String outContent = baosOut.toString();
                    String errContent = baosErr.toString();

                    // Write stdout captured content if stdout redirection is specified.
                    if (stdoutRedirect != null) {
                        try (PrintWriter writer = new PrintWriter(new FileOutputStream(stdoutRedirect, commandLine.isStdoutAppend()))) {
                            writer.print(outContent);
                        } catch (IOException e) {
                            System.err.println("Error writing to file: " + stdoutRedirect);
                        }
                    } else {
                        // Otherwise, print to the console.
                        System.out.print(outContent);
                    }


                    // Write stderr captured content if stderr redirection is specified.
                    if (stderrRedirect != null) {
                        try (PrintWriter writer = new PrintWriter(new FileOutputStream(stderrRedirect, commandLine.isStderrAppend()))) {
                            writer.print(errContent);
                        } catch (IOException e) {
                            System.err.println("Error writing to file: " + stderrRedirect);
                        }
                    } else {
                        System.err.print(errContent);
                    }
                } else {
                    // No redirection specified: simply execute the built-in.
                    command.execute(args);
                }
            } else {
                // External command: delegate execution to ExternalCommandRunner.
                ExternalCommandRunner.runExternalCommand(commandName, args,
                                                         stdoutRedirect, commandLine.isStdoutAppend(),
                                                         stderrRedirect, commandLine.isStderrAppend());
            }
        }
        scanner.close();
    }
}
