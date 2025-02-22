package com.nyx.shell;

import java.util.List;

/**
 * Represents a parsed command line.
 * Contains the command name, arguments, and optional redirection targets.
 * For each redirection, a corresponding boolean flag indicates if the redirection is
 * an append (>> / 2>>) instead of an overwrite (> / 2>).
 */
public class CommandLine {
    private final String commandName;
    private final List<String> arguments;
    private final String stdoutRedirect; // null if not specified
    private final boolean stdoutAppend;  // true if using ">>" or "1>>"
    private final String stderrRedirect; // null if not specified
    private final boolean stderrAppend;  // true if using "2>>"

    public CommandLine(String commandName, List<String> arguments,
                       String stdoutRedirect, boolean stdoutAppend,
                       String stderrRedirect, boolean stderrAppend) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.stdoutRedirect = stdoutRedirect;
        this.stdoutAppend = stdoutAppend;
        this.stderrRedirect = stderrRedirect;
        this.stderrAppend = stderrAppend;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getStdoutRedirect() {
        return stdoutRedirect;
    }

    public boolean isStdoutAppend() {
        return stdoutAppend;
    }

    public String getStderrRedirect() {
        return stderrRedirect;
    }

    public boolean isStderrAppend() {
        return stderrAppend;
    }
}
