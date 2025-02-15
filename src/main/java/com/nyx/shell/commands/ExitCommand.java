package com.nyx.shell.commands;

import com.nyx.shell.Command;

/**
 * A built-in command that terminates the shell.
 */
public class ExitCommand implements Command {

    /**
     * Executes the exit command.
     * If an argument is provided, it will be used as the exit code.
     * Otherwise, the shell exits with a status code of 0.
     *
     * @param args The exit code as a string (optional).
     */
    @Override
    public void execute(String[] args) {
        int exitCode = 0; // Default exit code.
        if (args.length > 0) {
            try {
                exitCode = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid exit code. Using default exit code 1.");
                exitCode = 1;
            }
        }
        System.exit(exitCode);
    }
}
