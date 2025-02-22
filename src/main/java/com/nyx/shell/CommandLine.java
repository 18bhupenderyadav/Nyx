package com.nyx.shell;

import java.util.List;

/**
 * Represents a parsed command line.
 * <p>
 * Contains:
 *   commandName: The primary command (e.g. "ls", "echo").
 *   arguments: The list of arguments for the command.
 *   stdoutRedirect: File path for standard output redirection (null if not specified)
 *   stderrRedirect: File path for standard error redirection (null if not specified).
 * This class is a simple data holder (POJO) for the command and its redirection targets.
 */
public class CommandLine {
    private final String commandName;
    private final List<String> arguments;
    private final String stdoutRedirect;
    private final String stderrRedirect;

    public CommandLine(String commandName, List<String> arguments, String stdoutRedirect, String stderrRedirect) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.stdoutRedirect = stdoutRedirect;
        this.stderrRedirect = stderrRedirect;
    }

    // Getter methods to retrieve command components.
    public String getCommandName() {
        return commandName;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getStdoutRedirect() {
        return stdoutRedirect;
    }

    public String getStderrRedirect() {
        return stderrRedirect;
    }
}
