package com.nyx.shell;

import java.util.Scanner;

/**
 * The Shell class encapsulates the main command processing loop.
 * It reads user input from the console, parses the command and its arguments,
 * and dispatches the command for execution.
 */
public class Shell {
    private final CommandRegistry myCommandRegistry;

    /**
     * Constructs a new Shell with the provided command registry.
     *
     * @param commandRegistry The registry that holds built-in commands.
     */
    public Shell(CommandRegistry commandRegistry) {
        this.myCommandRegistry = commandRegistry;
    }

    /**
     * Starts the shell's main loop which continuously reads input from the user,
     * parses it, and executes the appropriate command.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            if (!scanner.hasNextLine())
                break;

            // Read and trim the input.
            String input = scanner.nextLine().trim();
            if (input.isEmpty())
                continue;

            // Tokenize the input; the first token is the command name.
            String[] tokens = input.split("\\s+");
            String commandName = tokens[0];

            // Prepare the arguments array by excluding the command name.
            String[] args = new String[tokens.length - 1];
            if (tokens.length > 1) {
                System.arraycopy(tokens, 1, args, 0, tokens.length - 1);
            }

            // Retrieve the command from the registry.
            Command command = myCommandRegistry.getCommand(commandName);
            if (command != null) {
                command.execute(args);
            } else {
                System.out.println(commandName + ": command not found");
            }
        }
        scanner.close();
    }
}
