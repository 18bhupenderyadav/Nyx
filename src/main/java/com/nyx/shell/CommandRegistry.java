package com.nyx.shell;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a registry of commands mapped by their names.
 * It enables easy registration and retrieval of commands.
 */
public class CommandRegistry {
    private final Map<String, Command> myCommands = new HashMap<>();

    /**
     * Registers a new command with the given name.
     *
     * @param name    The command name to be registered.
     * @param command The implementation of the command.
     */
    public void registerCommand(String name, Command command) {
        myCommands.put(name, command);
    }

    /**
     * Retrieves the command associated with the given name.
     *
     * @param name The command name.
     * @return The command if found; otherwise, null.
     */
    public Command getCommand(String name) {
        return myCommands.get(name);
    }
}
