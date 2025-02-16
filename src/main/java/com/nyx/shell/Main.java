package com.nyx.shell;

import com.nyx.shell.commands.EchoCommand;
import com.nyx.shell.commands.ExitCommand;
import com.nyx.shell.commands.TypeCommand;

/**
 * The entry point for the shell application.
 * It initializes the command registry and starts the shell loop.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        CommandRegistry registry = new CommandRegistry();

        // Register built-in commands here.
        // For example: registry.registerCommand("help", new HelpCommand());
        registry.registerCommand("exit", new ExitCommand());
        registry.registerCommand("echo", new EchoCommand());
        registry.registerCommand("type", new TypeCommand(registry));

        // Initialize and run the shell.
        Shell shell = new Shell(registry);
        shell.run();
    }
}
