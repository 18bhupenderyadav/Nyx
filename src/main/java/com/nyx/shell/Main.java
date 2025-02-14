package com.nyx.shell;

/**
 * The entry point for the shell application.
 * It initializes the command registry and starts the shell loop.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        CommandRegistry registry = new CommandRegistry();

        // Register built-in commands here.
        // For example: registry.registerCommand("help", new HelpCommand());

        // Initialize and run the shell.
        Shell shell = new Shell(registry);
        shell.run();
    }
}
