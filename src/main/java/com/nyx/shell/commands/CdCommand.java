package com.nyx.shell.commands;

import com.nyx.shell.Command;
import java.io.File;
import java.io.IOException;

/**
 * A built-in command that changes the current working directory.
 * Supports absolute paths, relative paths, and the "~" shorthand
 * for the user's home directory.
 */
public class CdCommand implements Command {

    /**
     * Executes the cd command.
     * If the target directory is valid, the working directory is changed.
     * If the directory doesn't exist or isn't a directory, an error
     * message is printed.
     *
     * @param args The directory path to change to. Only the first
     *             argument is used.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("cd: missing operand");
            return;
        }

        // Map "~" to the user's home directory; otherwise,
        // use the provided path.
        String inputPath = args[0];
        String resolvedPath = "~".equals(inputPath) ? System.getenv("HOME") : inputPath;

        File targetDir;
        // If the resolved path is absolute, use it directly;
        // if not, resolve relative to current directory.
        if (new File(resolvedPath).isAbsolute()) {
            targetDir = new File(resolvedPath);
        } else {
            String currentDir = System.getProperty("user.dir");
            targetDir = new File(currentDir, resolvedPath);
        }

        // Normalize the path to handle any relative components like ./ or ../.
        try {
            targetDir = targetDir.getCanonicalFile();
        } catch (IOException e) {
            System.out.println("cd: " + args[0] + ": No such file or directory");
            return;
        }

        // Check if the target directory exists and is indeed a directory.
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            System.out.println("cd: " + args[0] + ": No such file or directory");
            return;
        }

        // Update the working directory. The pwd command will read this property.
        System.setProperty("user.dir", targetDir.getAbsolutePath());
    }
}
