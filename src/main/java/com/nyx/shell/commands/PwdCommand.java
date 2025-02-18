package com.nyx.shell.commands;

import com.nyx.shell.Command;

/**
 * A built-in command that prints the current working directory.
 * The pwd command returns the full absolute path of the current working directory.
 */
public class PwdCommand implements Command {

    /**
     * Executes the pwd command.
     * Any provided arguments are ignored.
     *
     * @param args the arguments provided by the user (ignored in this command).
     */
    @Override
    public void execute(String[] args) {
        // Print the absolute path of the current working directory.
        System.out.println(System.getProperty("user.dir"));
    }
}
