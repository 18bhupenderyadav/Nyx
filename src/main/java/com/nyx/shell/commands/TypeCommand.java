package com.nyx.shell.commands;

import com.nyx.shell.Command;
import com.nyx.shell.CommandRegistry;

/**
 * A built-in command that determines how a command would be interpreted.
 * It checks the command registry to see if a command is a shell builtin.
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
     * For each provided argument, the command checks if it exists in the registry.
     * If found, it prints "<command> is a shell builtin"; otherwise, it prints
     * "<command>: not found".
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
            if (myRegistry.getCommand(cmd) != null) {
                System.out.println(cmd + " is a shell builtin");
            } else {
                System.out.println(cmd + ": not found");
            }
        }
    }
}
