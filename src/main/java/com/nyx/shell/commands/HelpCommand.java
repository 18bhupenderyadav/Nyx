package com.nyx.shell.commands;

import com.nyx.shell.Command;
import com.nyx.shell.CommandRegistry;
import java.io.*;
import java.util.Optional;

/**
 * Implementation of the help command.
 * Features:
 * - List all available commands
 * - Show detailed help for specific commands
 * - Supports both built-in and external commands
 */
public class HelpCommand implements Command {
    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public int execute(String[] args, InputStream input, 
                      OutputStream output, OutputStream error) throws IOException {
        try {
            validateArgs(args);
            
            PrintWriter out = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);

            // If no command specified, show general help
            if (args.length == 0) {
                printGeneralHelp(out);
                out.flush();
                return SUCCESS;
            }

            // Show help for specific command
            String commandName = args[0];
            Optional<Command> command = registry.getCommand(commandName);
            Optional<CommandRegistry.CommandMetadata> metadata = registry.getMetadata(commandName);

            if (command.isPresent()) {
                // Print command help
                out.println(command.get().getHelp());
                
                // Print additional metadata if available
                metadata.ifPresent(meta -> {
                    if (!meta.getAliases().isEmpty()) {
                        out.println("\nAliases: " + String.join(", ", meta.getAliases()));
                    }
                });
                
                out.flush();
                return SUCCESS;
            } else {
                err.println("help: no help found for " + commandName);
                err.flush();
                return GENERAL_ERROR;
            }

        } catch (Exception e) {
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            err.println("help: " + e.getMessage());
            err.flush();
            return GENERAL_ERROR;
        }
    }

    @Override
    public void validateArgs(String[] args) {
        if (args != null && args.length > 1) {
            throw new IllegalArgumentException("help: too many arguments");
        }
    }

    @Override
    public String getHelp() {
        return "Usage: help [command]\n" +
               "Display information about builtin commands.\n\n" +
               "If COMMAND is specified, gives detailed help about that command.\n" +
               "Otherwise, lists available commands.\n";
    }

    /**
     * Prints general help showing all available commands
     */
    private void printGeneralHelp(PrintWriter out) {
        out.println("Nyx Shell, version 1.0");
        out.println("These shell commands are defined internally.\n");
        
        out.println("Available commands:");
        for (String name : registry.listCommands()) {
            Optional<CommandRegistry.CommandMetadata> meta = registry.getMetadata(name);
            if (meta.isPresent() && meta.get().isBuiltin()) {
                String description = meta.get().getDescription();
                out.printf("  %-15s %s%n", name, 
                    description.isEmpty() ? "(no description available)" : description);
            }
        }
        
        out.println("\nType 'help command' for more information about a command.");
    }

    @Override
    public boolean supportsPipelineInput() {
        return false; // help doesn't process input from other commands
    }
}