package com.nyx.shell;

/**
 * Represents a command that can be executed by the shell.
 * Each command should implement this interface to define its own execution logic.
 */
public interface Command {

    /**
     * Executes the command with the specified arguments.
     *
     * @param args The arguments provided by the user.
     */
    void execute(String[] args);
}
