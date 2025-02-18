package com.nyx.shell.commands;

import com.nyx.shell.Command;
import java.io.File;
import java.io.IOException;

/**
 * A built-in command that changes the current working directory.
 * Supports both absolute paths (e.g. /usr/local/bin) and relative paths (e.g. ./, ../, ./dir).
 */
public class CdCommand implements Command {

    /**
     * Executes the cd command.
     * If the provided path is valid, changes the current working directory.
     * If the path is invalid, prints an error message.
     *
     * @param args The directory path to change to. Only the first argument is used.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            // Future enhancement: default to the user's home directory.
            System.out.println("cd: missing operand");
            return;
        }

        String pathArg = args[0];
        File targetDir;

        // Determine if the path is absolute or relative.
        if (new File(pathArg).isAbsolute()) {
            targetDir = new File(pathArg);
        } else {
            String currentDir = System.getProperty("user.dir");
            targetDir = new File(currentDir, pathArg);
        }

        // Resolve any relative components (like ./ or ../) and normalize the path.
        try {
            targetDir = targetDir.getCanonicalFile();
        } catch (IOException e) {
            System.out.println("cd: " + pathArg + ": No such file or directory");
            return;
        }

        // Verify the target exists and is a directory.
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            System.out.println("cd: " + pathArg + ": No such file or directory");
            return;
        }

        // Change the working directory. The pwd command will read this value.
        System.setProperty("user.dir", targetDir.getAbsolutePath());
    }
}
