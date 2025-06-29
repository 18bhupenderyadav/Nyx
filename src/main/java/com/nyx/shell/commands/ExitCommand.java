package com.nyx.shell.commands;

import com.nyx.shell.Command;
import java.io.*;

/**
 * Implementation of the exit command.
 * Features:
 * - Status code handling
 * - Resource cleanup
 * - Custom exit handlers
 */
public class ExitCommand implements Command {
    public interface ExitHandler {
        void onExit(int status);
    }

    private ExitHandler exitHandler;
    private boolean cleanupRegistered = false;

    public ExitCommand() {
        this(System::exit);
    }

    public ExitCommand(ExitHandler handler) {
        this.exitHandler = handler;
        registerCleanupHook();
    }

    @Override
    public int execute(String[] args, InputStream input, 
                      OutputStream output, OutputStream error) throws IOException {
        try {
            validateArgs(args);
            
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            int status = 0;

            // Parse exit status if provided
            if (args.length > 0) {
                try {
                    status = Integer.parseInt(args[0]);
                    if (status < 0) {
                        err.println("exit: status should be >= 0");
                        err.flush();
                        return GENERAL_ERROR;
                    }
                } catch (NumberFormatException e) {
                    err.println("exit: numeric argument required");
                    err.flush();
                    return MISUSE_OF_SHELL_BUILTINS;
                }
            }

            // Perform exit
            exitHandler.onExit(status);
            return status;

        } catch (Exception e) {
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            err.println("exit: " + e.getMessage());
            err.flush();
            return GENERAL_ERROR;
        }
    }

    @Override
    public void validateArgs(String[] args) {
        if (args != null && args.length > 1) {
            throw new IllegalArgumentException("exit: too many arguments");
        }
    }

    @Override
    public String getHelp() {
        return "Usage: exit [status]\n" +
               "Exit the shell.\n\n" +
               "Arguments:\n" +
               "  status  The exit status (0-255). If not specified, the exit\n" +
               "          status is that of the last executed command.\n";
    }

    /**
     * Registers a JVM shutdown hook to handle cleanup operations.
     */
    private void registerCleanupHook() {
        if (!cleanupRegistered) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // Close any open resources, flush buffers, etc.
                try {
                    System.out.flush();
                    System.err.flush();
                } catch (Exception e) {
                    // Ignore exceptions during shutdown
                }
            }));
            cleanupRegistered = true;
        }
    }

    /**
     * Sets a custom exit handler.
     * Useful for testing or custom exit behavior.
     */
    public void setExitHandler(ExitHandler handler) {
        this.exitHandler = handler != null ? handler : System::exit;
    }

    @Override
    public boolean supportsPipelineInput() {
        return false; // exit doesn't process input from other commands
    }
}
