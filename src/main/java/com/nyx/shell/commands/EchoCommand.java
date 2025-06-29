package com.nyx.shell.commands;

import com.nyx.shell.Command;
import java.io.*;

/**
 * Implementation of the echo command.
 * Supports:
 * - Basic text output
 * - Escape sequences
 * - Pipeline input/output
 * - Option handling (-n to suppress newline)
 */
public class EchoCommand implements Command {
    private static final String NO_NEWLINE_FLAG = "-n";

    @Override
    public int execute(String[] args, InputStream input, 
                      OutputStream output, OutputStream error) throws IOException {
        try {
            validateArgs(args);
            
            boolean appendNewline = true;
            int startIndex = 0;

            // Handle -n flag if present
            if (args.length > 0 && NO_NEWLINE_FLAG.equals(args[0])) {
                appendNewline = false;
                startIndex = 1;
            }

            // Process and write output
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
            
            // Handle pipeline input if available
            if (input != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.print(line);
                        if (appendNewline) writer.println();
                    }
                }
            }

            // Process command arguments
            for (int i = startIndex; i < args.length; i++) {
                if (i > startIndex) writer.print(" ");
                writer.print(processEscapes(args[i]));
            }

            if (appendNewline) writer.println();
            writer.flush();

            return SUCCESS;
        } catch (Exception e) {
            PrintWriter errorWriter = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            errorWriter.println("echo: " + e.getMessage());
            return GENERAL_ERROR;
        }
    }

    @Override
    public void validateArgs(String[] args) {
        // Echo accepts any number of arguments
        if (args == null) {
            throw new IllegalArgumentException("Arguments array cannot be null");
        }
    }

    @Override
    public boolean supportsPipelineInput() {
        return true;
    }

    @Override
    public String getHelp() {
        return "Usage: echo [-n] [string ...]\n" +
               "Write arguments to standard output.\n\n" +
               "Options:\n" +
               "  -n    do not append newline\n\n" +
               "Escape sequences:\n" +
               "  \\n    newline\n" +
               "  \\t    horizontal tab\n" +
               "  \\\\    backslash\n";
    }

    /**
     * Process escape sequences in the input string.
     * Supported sequences: \n, \t, \\
     */
    private String processEscapes(String input) {
        if (input == null || !input.contains("\\")) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());
        boolean escaped = false;

        for (char c : input.toCharArray()) {
            if (escaped) {
                switch (c) {
                    case 'n': result.append('\n'); break;
                    case 't': result.append('\t'); break;
                    case '\\': result.append('\\'); break;
                    default: result.append('\\').append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }

        // Handle trailing backslash
        if (escaped) {
            result.append('\\');
        }

        return result.toString();
    }
}
