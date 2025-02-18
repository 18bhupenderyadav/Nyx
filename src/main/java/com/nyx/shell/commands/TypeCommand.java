package com.nyx.shell.commands;

import com.nyx.shell.Command;
import com.nyx.shell.CommandRegistry;
import com.nyx.shell.util.ExecutableFinder;

import java.util.Optional;

/**
 * A built-in command that determines how a command would be interpreted.
 * It first checks if a command is a shell builtin by consulting the command registry.
 * If not found as a builtin, it searches for an executable file in the PATH environment variable.
 */
public class TypeCommand implements Command {
    private final CommandRegistry myRegistry;

    /**
     * Constructs a new TypeCommand with the provided command registry.
     *
     * @param registry The registry that holds the built-in commands.
     */
    public TypeCommand(CommandRegistry registry) {
        this.myRegistry = registry;
    }

    /**
     * Executes the type command.
     * For each provided argument, checks if it is a built-in command or an executable in PATH.
     * <ul>
     *   <li>If the command is a shell builtin, prints "<command> is a shell builtin".</li>
     *   <li>If the command is found in PATH, prints "<command> is <absolute_path_to_command>".</li>
     *   <li>If not found, prints "<command>: not found".</li>
     * </ul>
     *
     * @param args The command names to check.
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("type: missing operand");
            return;
        }

        for (String cmd : args) {
            // Check if the command is a built-in.
            if (myRegistry.getCommand(cmd) != null) {
                System.out.println(cmd + " is a shell builtin");
            } else {
                // Use the ExecutableFinder to search for the command in PATH.
                Optional<String> executablePath = ExecutableFinder.findExecutable(cmd);
                if (executablePath.isPresent()) {
                    System.out.println(cmd + " is " + executablePath.get());
                } else {
                    System.out.println(cmd + ": not found");
                }
            }
        }
    }
}
