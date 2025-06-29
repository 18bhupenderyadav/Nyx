package com.nyx.shell.commands;

import com.nyx.shell.Command;
import com.nyx.shell.CommandRegistry;
import com.nyx.shell.util.ExecutableFinder;
import java.io.*;
import java.util.Optional;

/**
 * Implementation of the type command.
 * Shows how a command name would be interpreted if used as a command name.
 * Provides detailed information about:
 * - Built-in commands and their aliases
 * - External commands and their locations
 * - Command availability and type
 */
public class TypeCommand implements Command {
    private final CommandRegistry registry;

    public TypeCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public int execute(String[] args, InputStream input, 
                      OutputStream output, OutputStream error) throws IOException {
        try {
            validateArgs(args);
            
            PrintWriter out = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);

            int exitCode = SUCCESS;
            boolean foundAny = false;

            for (String arg : args) {
                // Check built-in commands first
                Optional<Command> cmd = registry.getCommand(arg);
                Optional<CommandRegistry.CommandMetadata> metadata = registry.getMetadata(arg);

                if (cmd.isPresent()) {
                    foundAny = true;
                    printBuiltinInfo(arg, metadata.orElse(null), out);
                }

                // Check for external commands
                Optional<String> execPath = ExecutableFinder.findExecutable(arg);
                if (execPath.isPresent()) {
                    foundAny = true;
                    printExternalInfo(arg, execPath.get(), out);
                }

                if (!foundAny) {
                    err.println(arg + " not found");
                    exitCode = COMMAND_NOT_FOUND;
                }
                
                foundAny = false; // Reset for next argument
            }

            out.flush();
            err.flush();
            return exitCode;
            
        } catch (Exception e) {
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            err.println("type: " + e.getMessage());
            err.flush();
            return GENERAL_ERROR;
        }
    }

    @Override
    public void validateArgs(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("type: missing command name");
        }
    }

    @Override
    public String getHelp() {
        return "Usage: type command ...\n" +
               "Display information about command type.\n\n" +
               "For each command, indicate how it would be interpreted if used as a\n" +
               "command name. Built-in commands will show their description and any\n" +
               "aliases. External commands will show their full path.\n";
    }

    private void printBuiltinInfo(String name, CommandRegistry.CommandMetadata metadata, 
                                PrintWriter out) {
        out.print(name + " is a shell builtin");
        
        if (metadata != null) {
            if (!metadata.getDescription().isEmpty()) {
                out.println();
                out.println("Description: " + metadata.getDescription());
            }
            
            if (!metadata.getAliases().isEmpty()) {
                out.println("Aliases: " + String.join(", ", metadata.getAliases()));
            }
        }
        
        out.println();
    }

    private void printExternalInfo(String name, String path, PrintWriter out) {
        out.println(name + " is " + path);
    }
}
