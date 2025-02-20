package com.nyx.shell;

import com.nyx.shell.util.ExternalCommandRunner;

import java.util.ArrayList;
import java.util.List;
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
     * Splits the input line into tokens, respecting single quotes.
     * Text enclosed in single quotes is preserved as a single token.
     *
     * @param input The raw input from the user.
     * @return An array of tokens.
     */
    private String[] tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inSingleQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\'') {
                // Toggle the inSingleQuotes flag when encountering a single quote.
                inSingleQuotes = !inSingleQuotes;
            } else if (Character.isWhitespace(c)) {
                if (inSingleQuotes) {
                    // If within quotes, keep whitespace.
                    currentToken.append(c);
                } else {
                    // Outside quotes, end the current token if it's not empty.
                    if (!currentToken.isEmpty()) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                }
            } else {
                currentToken.append(c);
            }
        }
        // Add the final token if present.
        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }
        return tokens.toArray(new String[0]);
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
            String[] tokens = tokenize(input);
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
                // Delegate execution of external commands.
                ExternalCommandRunner.runExternalCommand(commandName, args);
            }
        }
        scanner.close();
    }
}
