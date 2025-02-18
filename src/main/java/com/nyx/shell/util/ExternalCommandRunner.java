package com.nyx.shell.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for locating and executing external commands.
 */
public class ExternalCommandRunner {

    /**
     * Checks if the specified command exists in the PATH.
     *
     * @param command The command to search for.
     * @return true if the command exists in one of the PATH directories; false otherwise.
     */
    public static boolean commandExists(String command) {
        return ExecutableFinder.findExecutable(command).isPresent();
    }

    /**
     * Builds a command list for execution.
     * The first element is kept as the original (relative) command name, ensuring that
     * argv[0] remains the command name (as required by tests).
     *
     * @param command The command name as entered by the user.
     * @param args    The arguments to be passed to the command.
     * @return A list of strings representing the command and its arguments.
     */
    public static List<String> buildCommandList(String command, String[] args) {
        List<String> commandList = new ArrayList<>();
        // Use the original command name to ensure the process receives it as argv[0].
        commandList.add(command);
        commandList.addAll(Arrays.asList(args));
        return commandList;
    }

    /**
     * Attempts to run an external command with the provided arguments.
     * It first checks that the command exists in the PATH using ExecutableFinder.
     * If the command is found, it uses ProcessBuilder to execute it while inheriting
     * the current process's I/O. The command is executed using its relative name.
     *
     * @param command The command to execute.
     * @param args    The arguments for the command.
     */
    public static void runExternalCommand(String command, String[] args) {
        // Check if the command exists in PATH.
        Optional<String> found = ExecutableFinder.findExecutable(command);
        if (!found.isPresent()) {
            System.out.println(command + ": not found");
            return;
        }

        // Build the command list with the relative command name.
        List<String> commandList = buildCommandList(command, args);

        ProcessBuilder pb = new ProcessBuilder(commandList);
        // Inherit I/O so that output/error from the external process shows in the shell.
        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + command);
            e.printStackTrace();
        }
    }
}
