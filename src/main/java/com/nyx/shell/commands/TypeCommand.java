package com.nyx.shell.commands;

import com.nyx.shell.Command;
import com.nyx.shell.CommandRegistry;

public class TypeCommand implements Command {
    private final CommandRegistry myRegistry;

    public TypeCommand(CommandRegistry commandRegistry) {
        myRegistry = commandRegistry;
    }

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
