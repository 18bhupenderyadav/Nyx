package com.nyx.shell.util;

import java.io.File;
import java.util.Optional;

/**
 * A utility class that provides methods for locating executable files in the directories
 * specified by the PATH environment variable.
 */
public class ExecutableFinder {

    /**
     * Searches for the specified command in the directories listed in the PATH environment variable.
     *
     * @param command The command to search for.
     * @return An Optional containing the absolute path of the executable if found; otherwise, an empty Optional.
     */
    public static Optional<String> findExecutable(String command) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty()) {
            return Optional.empty();
        }
        String[] directories = pathEnv.split(":");
        for (String dir : directories) {
            File file = new File(dir, command);
            if (file.exists() && file.isFile() && file.canExecute()) {
                return Optional.of(file.getAbsolutePath());
            }
        }
        return Optional.empty();
    }
}
