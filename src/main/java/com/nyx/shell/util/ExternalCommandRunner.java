package com.nyx.shell.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for locating and executing external commands.
 * This class is responsible for:
 *   Locating the executable using a helper (ExecutableFinder).
 *   Building the command list (command name + arguments).
 *   Executing the command with support for output redirection via ProcessBuilder.
 */
public class ExternalCommandRunner {

    /**
     * Builds the command list for ProcessBuilder.
     * The command list starts with the command name (as typed by the user) followed by its arguments.
     *
     * @param command The command name.
     * @param args    The command arguments.
     * @return a list of strings representing the complete command.
     */
    public static List<String> buildCommandList(String command, String[] args) {
        List<String> commandList = new ArrayList<>();
        commandList.add(command);
        commandList.addAll(Arrays.asList(args));
        return commandList;
    }

    /**
     * Executes an external command with optional redirection of standard output and error.
     *
     * @param command         The command to execute.
     * @param args            The command arguments.
     * @param stdoutRedirect  File path to redirect stdout (null if not specified).
     * @param stderrRedirect  File path to redirect stderr (null if not specified).
     */
    public static void runExternalCommand(String command, String[] args, String stdoutRedirect, String stderrRedirect) {
        // Find the full path to the executable using the helper.
        Optional<String> executablePathOpt = ExecutableFinder.findExecutable(command);
        if (executablePathOpt.isEmpty()) {
            System.out.println(command + ": not found");
            return;
        }
        // Build the command list (we're passing the relative command name to keep argv[0] unchanged).
        List<String> commandList = buildCommandList(command, args);
        ProcessBuilder pb = new ProcessBuilder(commandList);

        // Set up stdout redirection if specified.
        if (stdoutRedirect != null) {
            pb.redirectOutput(new File(stdoutRedirect));
        } else {
            // Otherwise, inherit the parent's stdout.
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }
        // Set up stderr redirection if specified.
        if (stderrRedirect != null) {
            pb.redirectError(new File(stderrRedirect));
        } else {
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        }

        // Start the process and wait for it to complete.
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing external command: " + command);
            e.printStackTrace();
        }
    }
}
