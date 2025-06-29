package com.nyx.shell.commands;

import com.nyx.shell.Command;
import java.io.*;
import java.nio.file.*;
import java.util.Stack;

/**
 * Implementation of the cd (change directory) command.
 * Features:
 * - Home directory expansion (~)
 * - Previous directory tracking (-)
 * - Path normalization
 * - Symbolic link handling
 */
public class CdCommand implements Command {
    private static final String HOME_DIR = System.getProperty("user.home");
    private static Stack<String> directoryHistory = new Stack<>();
    private static String previousDirectory = null;

    @Override
    public int execute(String[] args, InputStream input, 
                      OutputStream output, OutputStream error) throws IOException {
        try {
            validateArgs(args);
            
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            String currentDir = System.getProperty("user.dir");
            String targetDir;

            // Determine target directory
            if (args.length == 0 || args[0].equals("~")) {
                targetDir = HOME_DIR;
            } else if (args[0].equals("-")) {
                if (previousDirectory == null) {
                    err.println("cd: no previous directory");
                    err.flush();
                    return GENERAL_ERROR;
                }
                targetDir = previousDirectory;
            } else {
                targetDir = expandPath(args[0]);
            }

            // Try to change to the target directory
            try {
                Path path = Paths.get(targetDir).toAbsolutePath().normalize();
                
                // Verify the directory exists and is accessible
                if (!Files.exists(path)) {
                    err.println("cd: " + targetDir + ": No such file or directory");
                    err.flush();
                    return GENERAL_ERROR;
                }
                
                if (!Files.isDirectory(path)) {
                    err.println("cd: " + targetDir + ": Not a directory");
                    err.flush();
                    return GENERAL_ERROR;
                }
                
                if (!Files.isReadable(path)) {
                    err.println("cd: " + targetDir + ": Permission denied");
                    err.flush();
                    return GENERAL_ERROR;
                }

                // Update directory history
                if (!currentDir.equals(path.toString())) {
                    previousDirectory = currentDir;
                    directoryHistory.push(currentDir);
                }

                // Change directory
                System.setProperty("user.dir", path.toString());
                return SUCCESS;

            } catch (SecurityException e) {
                err.println("cd: " + targetDir + ": Permission denied");
                err.flush();
                return GENERAL_ERROR;
            } catch (InvalidPathException e) {
                err.println("cd: " + targetDir + ": Invalid path");
                err.flush();
                return GENERAL_ERROR;
            }

        } catch (Exception e) {
            PrintWriter err = new PrintWriter(new OutputStreamWriter(error, "UTF-8"), true);
            err.println("cd: " + e.getMessage());
            err.flush();
            return GENERAL_ERROR;
        }
    }

    @Override
    public void validateArgs(String[] args) {
        if (args != null && args.length > 1) {
            throw new IllegalArgumentException("cd: too many arguments");
        }
    }

    @Override
    public String getHelp() {
        return "Usage: cd [dir]\n" +
               "Change the current directory.\n\n" +
               "Arguments:\n" +
               "  dir    The directory to change to. If not specified, changes to\n" +
               "         the home directory.\n\n" +
               "Special paths:\n" +
               "  ~      Home directory\n" +
               "  -      Previous directory\n" +
               "  .      Current directory\n" +
               "  ..     Parent directory\n";
    }

    /**
     * Expands special characters in paths.
     * Handles: ~, ., .., and environment variables
     */
    private String expandPath(String path) {
        if (path.startsWith("~")) {
            // Replace ~ with home directory
            return HOME_DIR + path.substring(1);
        }

        // Handle environment variable expansion
        if (path.contains("$")) {
            for (String key : System.getenv().keySet()) {
                String var = "$" + key;
                if (path.contains(var)) {
                    path = path.replace(var, System.getenv(key));
                }
            }
        }

        return path;
    }

    @Override
    public boolean supportsPipelineInput() {
        return false; // cd doesn't process input from other commands
    }
}
