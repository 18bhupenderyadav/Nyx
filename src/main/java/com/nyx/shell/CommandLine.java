package com.nyx.shell;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a parsed command line with support for:
 * <ul>
 *   <li>Command name and arguments</li>
 *   <li>I/O redirection with append mode</li>
 *   <li>Pipeline operations (future)</li>
 *   <li>Input validation and error reporting</li>
 * </ul>
 */
public class CommandLine {
    private final String commandName;
    private final List<String> arguments;
    private final String stdoutRedirect;
    private final boolean stdoutAppend;
    private final String stderrRedirect;
    private final boolean stderrAppend;

    // Future: Support for pipeline operations
    private CommandLine nextCommand;

    /**
     * Creates a new CommandLine instance with full I/O redirection support.
     *
     * @param commandName    The name of the command to execute
     * @param arguments      The command arguments
     * @param stdoutRedirect File path for stdout redirection (null if none)
     * @param stdoutAppend   Whether to append stdout (true) or overwrite (false)
     * @param stderrRedirect File path for stderr redirection (null if none)
     * @param stderrAppend   Whether to append stderr (true) or overwrite (false)
     * @throws IllegalArgumentException if command name is null or empty
     */
    public CommandLine(String commandName, List<String> arguments,
                      String stdoutRedirect, boolean stdoutAppend,
                      String stderrRedirect, boolean stderrAppend) {
        validateInputs(commandName);
        
        this.commandName = commandName;
        this.arguments = arguments != null ? 
            Collections.unmodifiableList(arguments) : 
            Collections.emptyList();
        this.stdoutRedirect = stdoutRedirect;
        this.stdoutAppend = stdoutAppend;
        this.stderrRedirect = stderrRedirect;
        this.stderrAppend = stderrAppend;
    }

    private void validateInputs(String commandName) {
        if (commandName == null || commandName.trim().isEmpty()) {
            throw new IllegalArgumentException("Command name cannot be null or empty");
        }
    }

    // Getters with validation
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

    // Pipeline operations (future implementation)
    public void setNextCommand(CommandLine next) {
        this.nextCommand = next;
    }

    public CommandLine getNextCommand() {
        return nextCommand;
    }

    public boolean hasPipeline() {
        return nextCommand != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandLine)) return false;
        
        CommandLine that = (CommandLine) o;
        return stdoutAppend == that.stdoutAppend &&
               stderrAppend == that.stderrAppend &&
               Objects.equals(commandName, that.commandName) &&
               Objects.equals(arguments, that.arguments) &&
               Objects.equals(stdoutRedirect, that.stdoutRedirect) &&
               Objects.equals(stderrRedirect, that.stderrRedirect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandName, arguments, 
                          stdoutRedirect, stdoutAppend,
                          stderrRedirect, stderrAppend);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
            .append(commandName);

        if (!arguments.isEmpty()) {
            sb.append(" ").append(String.join(" ", arguments));
        }

        if (stdoutRedirect != null) {
            sb.append(stdoutAppend ? " >> " : " > ")
              .append(stdoutRedirect);
        }

        if (stderrRedirect != null) {
            sb.append(stderrAppend ? " 2>> " : " 2> ")
              .append(stderrRedirect);
        }

        if (hasPipeline()) {
            sb.append(" | ").append(nextCommand.toString());
        }

        return sb.toString();
    }
}
