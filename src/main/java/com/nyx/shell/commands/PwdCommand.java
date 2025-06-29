package com.nyx.shell.commands;

import com.nyx.shell.Command;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of the pwd (print working directory) command.
 * Features:
 * - Physical (-P) and logical (-L) path resolution
 * - Path normalization
 * - Pipeline output support
 */
public class PwdCommand implements Command {
    private static final String PHYSICAL_FLAG = "-P";
    private static final String LOGICAL_FLAG = "-L";

    @Override
    public int execute(String[] args, InputStream input, 
                      OutputStream output, OutputStream error) throws IOException {
        try {
            validateArgs(args);
            
            PrintWriter out = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);

            boolean usePhysical = false;

            // Parse flags
            if (args.length > 0) {
                switch (args[0]) {
                    case PHYSICAL_FLAG:
                        usePhysical = true;
                        break;
                    case LOGICAL_FLAG:
                        usePhysical = false;
                        break;
                    default:
                        err.println("pwd: invalid option: " + args[0]);
                        err.flush();
                        return MISUSE_OF_SHELL_BUILTINS;
                }
            }

            // Get and normalize the current directory path
            String currentDir = System.getProperty("user.dir");
            Path path = Paths.get(currentDir);

            if (usePhysical) {
                // Resolve symbolic links to get the physical path
                try {
                    path = path.toRealPath();
                } catch (IOException e) {
                    err.println("pwd: error resolving path: " + e.getMessage());
                    err.flush();
                    return GENERAL_ERROR;
                }
            } else {
                // Use the logical path (normalize but don't resolve links)
                path = path.normalize();
            }

            // Convert path to use system-specific separator
            String formattedPath = path.toString();

            // Print the path
            out.println(formattedPath);
            out.flush();
            return SUCCESS;

        } catch (Exception e) {
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            err.println("pwd: " + e.getMessage());
            err.flush();
            return GENERAL_ERROR;
        }
    }

    @Override
    public void validateArgs(String[] args) {
        if (args != null && args.length > 1) {
            throw new IllegalArgumentException("pwd: too many arguments");
        }
    }

    @Override
    public String getHelp() {
        return "Usage: pwd [-P|-L]\n" +
               "Print the current working directory.\n\n" +
               "Options:\n" +
               "  -L    print the value of PWD if it names the current working\n" +
               "        directory (default)\n" +
               "  -P    print the physical directory, with all symbolic links\n" +
               "        resolved\n";
    }

    @Override
    public boolean supportsPipelineInput() {
        return false; // pwd doesn't process input from other commands
    }
}
