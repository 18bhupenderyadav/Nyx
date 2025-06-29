package com.nyx.shell.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for locating and executing external commands.
 * This class handles:
 * <ul>
 *   <li>Command path resolution using PATH environment variable</li>
 *   <li>Process creation and execution</li>
 *   <li>Standard I/O redirection</li>
 *   <li>Process monitoring and termination</li>
 *   <li>Error handling and reporting</li>
 * </ul>
 */
public class ExternalCommandRunner {
    
    // Default timeout for commands in seconds
    private static final long DEFAULT_TIMEOUT = 30;
    
    // Exit codes
    private static final int SUCCESS = 0;
    private static final int COMMAND_NOT_FOUND = 127;
    private static final int EXECUTION_ERROR = 1;

    /**
     * Builds the command list for ProcessBuilder.
     * Combines the command name with its arguments while preserving the original command name in argv[0].
     *
     * @param command The command name as typed by the user
     * @param args    The command arguments
     * @return A list of strings representing the complete command
     */
    public static List<String> buildCommandList(String command, String[] args) {
        List<String> commandList = new ArrayList<>();
        commandList.add(command);
        commandList.addAll(Arrays.asList(args));
        return commandList;
    }

    /**
     * Executes an external command with optional redirection of standard output and error.
     * Supports both overwrite (>) and append (>>) modes for redirections.
     *
     * @param command         The command to execute
     * @param args           The command arguments
     * @param stdoutRedirect File path to redirect stdout (null if not specified)
     * @param stdoutAppend   Whether to append to stdout redirect file
     * @param stderrRedirect File path to redirect stderr (null if not specified)
     * @param stderrAppend   Whether to append to stderr redirect file
     * @return The exit code of the command
     */
    public static int runExternalCommand(String command, String[] args,
                                     String stdoutRedirect, boolean stdoutAppend,
                                     String stderrRedirect, boolean stderrAppend) {
        try {
            // Find the full path to the executable
            Optional<String> executablePathOpt = ExecutableFinder.findExecutable(command);
            if (executablePathOpt.isEmpty()) {
                System.err.println(command + ": command not found");
                return COMMAND_NOT_FOUND;
            }

            // Build and configure the process
            ProcessBuilder pb = configureProcess(command, args, 
                                              stdoutRedirect, stdoutAppend,
                                              stderrRedirect, stderrAppend);

            // Execute and monitor the process
            return executeProcess(pb, command);

        } catch (Exception e) {
            handleExecutionError(command, e);
            return EXECUTION_ERROR;
        }
    }

    /**
     * Configures the ProcessBuilder with the command and its I/O redirections.
     */
    private static ProcessBuilder configureProcess(String command, String[] args,
                                                 String stdoutRedirect, boolean stdoutAppend,
                                                 String stderrRedirect, boolean stderrAppend) {
        List<String> commandList = buildCommandList(command, args);
        ProcessBuilder pb = new ProcessBuilder(commandList);

        // Configure stdout redirection
        if (stdoutRedirect != null) {
            File redirectFile = new File(stdoutRedirect);
            pb.redirectOutput(stdoutAppend ? 
                ProcessBuilder.Redirect.appendTo(redirectFile) : 
                ProcessBuilder.Redirect.to(redirectFile));
        } else {
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }

        // Configure stderr redirection
        if (stderrRedirect != null) {
            File redirectFile = new File(stderrRedirect);
            pb.redirectError(stderrAppend ? 
                ProcessBuilder.Redirect.appendTo(redirectFile) : 
                ProcessBuilder.Redirect.to(redirectFile));
        } else {
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        }

        return pb;
    }

    /**
     * Executes and monitors the process, handling timeouts and interruptions.
     */
    private static int executeProcess(ProcessBuilder pb, String command) throws IOException, InterruptedException {
        Process process = pb.start();
        
        try {
            // Wait for the process with timeout
            if (!process.waitFor(DEFAULT_TIMEOUT, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                System.err.println(command + ": process timed out after " + DEFAULT_TIMEOUT + " seconds");
                return EXECUTION_ERROR;
            }
            
            return process.exitValue();
        } catch (InterruptedException e) {
            // Handle interrupt by terminating the process
            process.destroyForcibly();
            Thread.currentThread().interrupt(); // Preserve interrupt status
            throw e;
        }
    }

    /**
     * Handles and logs execution errors.
     */
    private static void handleExecutionError(String command, Exception e) {
        String errorMessage = e instanceof IOException ? 
            "I/O error occurred" : "Execution failed";
        System.err.println(command + ": " + errorMessage + " - " + e.getMessage());
    }
}
