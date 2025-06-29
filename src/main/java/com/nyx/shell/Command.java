package com.nyx.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for all shell commands, both built-in and external.
 * Provides support for:
 * <ul>
 *   <li>Standard command execution</li>
 *   <li>Pipeline input/output handling</li>
 *   <li>Error reporting and status codes</li>
 *   <li>Resource cleanup</li>
 * </ul>
 */
public interface Command {
    /**
     * Standard exit codes
     */
    int SUCCESS = 0;
    int GENERAL_ERROR = 1;
    int MISUSE_OF_SHELL_BUILTINS = 2;
    int COMMAND_NOT_EXECUTABLE = 126;
    int COMMAND_NOT_FOUND = 127;

    /**
     * Executes the command with the given arguments.
     * This is the primary execution method for simple commands.
     *
     * @param args Command arguments
     * @return Exit status code (0 for success, non-zero for failure)
     * @throws IOException If an I/O error occurs
     */
    default int execute(String[] args) throws IOException {
        return execute(args, null, System.out, System.err);
    }

    /**
     * Executes the command with support for pipeline operations.
     * This method should be implemented by commands that support pipelines.
     *
     * @param args Command arguments
     * @param input Input stream (may be null if no input is piped)
     * @param output Output stream for standard output
     * @param error Output stream for error output
     * @return Exit status code
     * @throws IOException If an I/O error occurs
     */
    default int execute(String[] args, InputStream input, 
                       OutputStream output, OutputStream error) throws IOException {
        // Default implementation for backward compatibility
        return execute(args);
    }

    /**
     * Validates command arguments before execution.
     * Commands should override this to perform argument validation.
     *
     * @param args Command arguments to validate
     * @throws IllegalArgumentException if arguments are invalid
     */
    default void validateArgs(String[] args) {
        // Default implementation accepts any arguments
    }

    /**
     * Checks if the command supports pipeline input.
     * Commands that can process input from other commands should override this.
     *
     * @return true if the command can accept pipeline input
     */
    default boolean supportsPipelineInput() {
        return false;
    }

    /**
     * Gets the command usage help text.
     * Commands should override this to provide help information.
     *
     * @return Help text describing command usage
     */
    default String getHelp() {
        return "No help available for this command.";
    }
}
