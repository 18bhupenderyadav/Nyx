package com.nyx.shell.commands;

import com.nyx.shell.Command;

/**
 * A built-in command that echoes the provided arguments to the standard output.
 */
public class EchoCommand implements Command {

    /**
     * Executes the echo command by joining the given arguments with spaces and printing the result.
     *
     * @param args the arguments to be echoed.
     */
    @Override
    public void execute(String[] args) {
        // Join the arguments with a single space and print the result.
        System.out.println(String.join(" ", args));
    }
}
