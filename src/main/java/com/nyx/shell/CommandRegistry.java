package com.nyx.shell;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.Optional;

/**
 * Registry for shell commands and their aliases.
 * Provides:
 * <ul>
 *   <li>Command registration and lookup</li>
 *   <li>Alias management</li>
 *   <li>Command metadata storage</li>
 * </ul>
 */
public class CommandRegistry {
    private final Map<String, Command> commands;
    private final Map<String, String> aliases;
    private final Map<String, CommandMetadata> metadata;

    /**
     * Stores metadata about registered commands
     */
    public static class CommandMetadata {
        private final String description;
        private final boolean isBuiltin;
        private final Set<String> aliases;

        public CommandMetadata(String description, boolean isBuiltin, Set<String> aliases) {
            this.description = description;
            this.isBuiltin = isBuiltin;
            this.aliases = Collections.unmodifiableSet(aliases);
        }

        public String getDescription() { return description; }
        public boolean isBuiltin() { return isBuiltin; }
        public Set<String> getAliases() { return aliases; }
    }

    public CommandRegistry() {
        this.commands = new HashMap<>();
        this.aliases = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    /**
     * Registers a command with optional metadata.
     *
     * @param name Command name
     * @param command Command implementation
     * @param description Optional description
     * @param isBuiltin Whether this is a built-in command
     * @throws IllegalArgumentException if name is null or empty
     */
    public void registerCommand(String name, Command command, 
                              String description, boolean isBuiltin) {
        validateCommandName(name);
        commands.put(name.toLowerCase(), command);
        metadata.put(name.toLowerCase(), 
            new CommandMetadata(description, isBuiltin, Collections.emptySet()));
    }

    /**
     * Registers a command without metadata.
     */
    public void registerCommand(String name, Command command) {
        registerCommand(name, command, "", false);
    }

    /**
     * Creates an alias for an existing command.
     *
     * @param alias Alias name
     * @param commandName Original command name
     * @throws IllegalArgumentException if command doesn't exist
     */
    public void createAlias(String alias, String commandName) {
        validateCommandName(alias);
        String normalizedCommand = commandName.toLowerCase();
        
        if (!commands.containsKey(normalizedCommand)) {
            throw new IllegalArgumentException(
                "Cannot create alias: command '" + commandName + "' not found");
        }

        aliases.put(alias.toLowerCase(), normalizedCommand);
        
        // Update metadata to include the new alias
        CommandMetadata meta = metadata.get(normalizedCommand);
        Set<String> updatedAliases = new java.util.HashSet<>(meta.getAliases());
        updatedAliases.add(alias);
        
        metadata.put(normalizedCommand, 
            new CommandMetadata(meta.getDescription(), meta.isBuiltin(), updatedAliases));
    }

    /**
     * Gets a command by name or alias.
     *
     * @param name Command name or alias
     * @return Optional containing the command if found
     */
    public Optional<Command> getCommand(String name) {
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }

        String normalizedName = name.toLowerCase();
        Command command = commands.get(normalizedName);
        
        if (command == null) {
            String originalName = aliases.get(normalizedName);
            if (originalName != null) {
                command = commands.get(originalName);
            }
        }

        return Optional.ofNullable(command);
    }

    /**
     * Gets metadata for a command.
     *
     * @param name Command name or alias
     * @return Optional containing the metadata if found
     */
    public Optional<CommandMetadata> getMetadata(String name) {
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }

        String normalizedName = name.toLowerCase();
        CommandMetadata meta = metadata.get(normalizedName);
        
        if (meta == null) {
            String originalName = aliases.get(normalizedName);
            if (originalName != null) {
                meta = metadata.get(originalName);
            }
        }

        return Optional.ofNullable(meta);
    }

    /**
     * Lists all registered commands and their aliases.
     *
     * @return Set of command names
     */
    public Set<String> listCommands() {
        return Collections.unmodifiableSet(commands.keySet());
    }

    /**
     * Lists all registered aliases.
     *
     * @return Map of alias to original command name
     */
    public Map<String, String> listAliases() {
        return Collections.unmodifiableMap(aliases);
    }

    private void validateCommandName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Command name cannot be null or empty");
        }
    }
}
