package com.nyx.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses tokenized command line input into structured CommandLine objects.
 * Supports:
 * <ul>
 *   <li>Command name and arguments parsing</li>
 *   <li>I/O redirection operators (>, >>, 2>, 2>>)</li>
 *   <li>Pipeline operator (|) - TODO: Implementation pending</li>
 *   <li>Error detection and reporting</li>
 * </ul>
 */
public class CommandLineParser {

    /**
     * Error codes for parsing failures
     */
    private static final int NO_ERROR = 0;
    private static final int MISSING_REDIRECT_TARGET = 1;
    private static final int MULTIPLE_REDIRECTIONS = 2;
    private static final int NO_COMMAND = 3;

    /**
     * Parse result containing both the CommandLine and any error information
     */
    public static class ParseResult {
        public final CommandLine commandLine;
        public final int errorCode;
        public final String errorMessage;

        private ParseResult(CommandLine cmd, int error, String message) {
            this.commandLine = cmd;
            this.errorCode = error;
            this.errorMessage = message;
        }

        public static ParseResult success(CommandLine cmd) {
            return new ParseResult(cmd, NO_ERROR, null);
        }

        public static ParseResult error(int code, String message) {
            return new ParseResult(null, code, message);
        }

        public boolean hasError() {
            return errorCode != NO_ERROR;
        }
    }

    /**
     * Parses the given tokens into a CommandLine object.
     *
     * @param tokens Array of tokens produced by the tokenizer
     * @return ParseResult containing either a valid CommandLine or error information
     */
    public static ParseResult parse(String[] tokens) {
        if (tokens == null || tokens.length == 0) {
            return ParseResult.error(NO_COMMAND, "No command provided");
        }

        List<String> commandTokens = new ArrayList<>();
        String stdoutRedirect = null;
        String stderrRedirect = null;
        boolean stdoutAppend = false;
        boolean stderrAppend = false;

        // Track redirection states to detect duplicates
        boolean hasStdoutRedirect = false;
        boolean hasStderrRedirect = false;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            // Check for stdout redirection
            if (token.equals(">") || token.equals("1>") || token.equals(">>") || token.equals("1>>")) {
                if (hasStdoutRedirect) {
                    return ParseResult.error(MULTIPLE_REDIRECTIONS, 
                        "Multiple stdout redirections not allowed");
                }
                
                if (i + 1 >= tokens.length) {
                    return ParseResult.error(MISSING_REDIRECT_TARGET, 
                        "Missing target file for " + token);
                }

                stdoutRedirect = tokens[++i];
                stdoutAppend = token.endsWith(">>");
                hasStdoutRedirect = true;
            }
            // Check for stderr redirection
            else if (token.equals("2>") || token.equals("2>>")) {
                if (hasStderrRedirect) {
                    return ParseResult.error(MULTIPLE_REDIRECTIONS, 
                        "Multiple stderr redirections not allowed");
                }
                
                if (i + 1 >= tokens.length) {
                    return ParseResult.error(MISSING_REDIRECT_TARGET, 
                        "Missing target file for " + token);
                }

                stderrRedirect = tokens[++i];
                stderrAppend = token.equals("2>>");
                hasStderrRedirect = true;
            }
            // Future: Handle pipe operator here
            else if (token.equals("|")) {
                // TODO: Implement pipeline support
                // Currently just collecting as part of command
                commandTokens.add(token);
            }
            else {
                commandTokens.add(token);
            }
        }

        if (commandTokens.isEmpty()) {
            return ParseResult.error(NO_COMMAND, "No command specified");
        }

        String commandName = commandTokens.get(0);
        List<String> arguments = commandTokens.subList(1, commandTokens.size());

        CommandLine cmdLine = new CommandLine(
            commandName, arguments, 
            stdoutRedirect, stdoutAppend,
            stderrRedirect, stderrAppend
        );

        return ParseResult.success(cmdLine);
    }

    /**
     * Legacy parse method for backward compatibility
     */
    public static CommandLine parse(String[] tokens, boolean legacyMode) {
        ParseResult result = parse(tokens);
        if (result.hasError()) {
            if (legacyMode) {
                return null;
            }
            throw new IllegalArgumentException(result.errorMessage);
        }
        return result.commandLine;
    }
}
