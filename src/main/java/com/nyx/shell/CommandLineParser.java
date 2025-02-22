package com.nyx.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses an array of tokens (from the ANTLR tokenizer) into a CommandLine object.
 * This class isolates the logic of identifying redirection operators (like ">", "1>", "2>")
 * and separating them from the command and its arguments.
 * Example:
 *   Input tokens: {"ls", "/tmp/baz", ">", "/tmp/foo/baz.md"}
 *   Parsed result:
 *     commandName = "ls"
 *     arguments   = ["/tmp/baz"]
 *     stdoutRedirect = "/tmp/foo/baz.md"
 */
public class CommandLineParser {

    /**
     * Parses the given tokens into a CommandLine object.
     *
     * @param tokens Array of tokens (as strings) produced by the tokenizer.
     * @return a CommandLine object or null if no command is found.
     */
    public static CommandLine parse(String[] tokens) {
        List<String> commandTokens = new ArrayList<>();
        String stdoutRedirect = null;
        String stderrRedirect = null;

        // Iterate through tokens to find redirection operators.
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            // Check for standard output redirection:
            // Either ">" or "1>" indicates redirection of stdout.
            if (token.equals(">") || token.equals("1>")) {
                // Ensure there is a following token (the file path).
                if (i + 1 < tokens.length) {
                    stdoutRedirect = tokens[++i];
                } else {
                    System.out.println("Error: redirection operator without target file");
                    // You could also throw an exception here.
                }
            }
            // Check for standard error redirection ("2>").
            else if (token.equals("2>")) {
                if (i + 1 < tokens.length) {
                    stderrRedirect = tokens[++i];
                } else {
                    System.out.println("Error: redirection operator without target file");
                }
            } else {
                // If token is not a redirection operator, add it to the command tokens.
                commandTokens.add(token);
            }
        }

        // If no command tokens are present, return null.
        if (commandTokens.isEmpty()) {
            return null;
        }

        // The first token is assumed to be the command name.
        String commandName = commandTokens.getFirst();
        // Remaining tokens are the command's arguments.
        List<String> arguments = commandTokens.subList(1, commandTokens.size());

        return new CommandLine(commandName, arguments, stdoutRedirect, stderrRedirect);
    }
}
