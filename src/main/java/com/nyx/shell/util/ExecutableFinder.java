package com.nyx.shell.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A utility class that locates executable files in the system PATH.
 * Supports both Windows and Unix-like systems by:
 * <ul>
 *   <li>Handling different path separators (: vs ;)</li>
 *   <li>Supporting Windows executable extensions (PATHEXT)</li>
 *   <li>Resolving both absolute and relative paths</li>
 *   <li>Validating file permissions where applicable</li>
 * </ul>
 */
public class ExecutableFinder {
    // Windows-specific file extensions for executables
    private static final String[] WINDOWS_EXTENSIONS = {
        "", ".exe", ".cmd", ".bat", ".com"
    };

    // Path separator based on OS
    private static final String PATH_SEPARATOR = System.getProperty("path.separator");
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    /**
     * Searches for an executable in the system PATH.
     *
     * @param command The command or program name to search for
     * @return Optional containing the absolute path if found
     */
    public static Optional<String> findExecutable(String command) {
        // If command contains path separators, try it as an absolute/relative path first
        if (command.contains("/") || command.contains("\\")) {
            File file = new File(command);
            if (isExecutableFile(file)) {
                return Optional.of(file.getAbsolutePath());
            }
        }

        // Get the PATH environment variable
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty()) {
            return Optional.empty();
        }

        // Split PATH into individual directories
        String[] directories = pathEnv.split(PATH_SEPARATOR);

        // Search each directory
        for (String dir : directories) {
            Optional<String> found = findInDirectory(dir, command);
            if (found.isPresent()) {
                return found;
            }
        }

        return Optional.empty();
    }

    /**
     * Searches for the executable in a specific directory.
     *
     * @param directory The directory to search in
     * @param command The command to search for
     * @return Optional containing the absolute path if found
     */
    private static Optional<String> findInDirectory(String directory, String command) {
        if (directory == null || directory.isEmpty()) {
            return Optional.empty();
        }

        File dir = new File(directory);
        if (!dir.isDirectory()) {
            return Optional.empty();
        }

        // On Windows, try all possible extensions
        if (IS_WINDOWS) {
            for (String ext : WINDOWS_EXTENSIONS) {
                File file = new File(dir, command + ext);
                if (isExecutableFile(file)) {
                    return Optional.of(file.getAbsolutePath());
                }
            }
        } else {
            // On Unix-like systems, try exact name only
            File file = new File(dir, command);
            if (isExecutableFile(file)) {
                return Optional.of(file.getAbsolutePath());
            }
        }

        return Optional.empty();
    }

    /**
     * Checks if a file exists and is executable.
     *
     * @param file The file to check
     * @return true if the file exists and is executable
     */
    private static boolean isExecutableFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        if (IS_WINDOWS) {
            // On Windows, check if the file exists and is readable
            return file.canRead();
        } else {
            // On Unix-like systems, check execute permission
            return file.canExecute();
        }
    }

    /**
     * Gets all valid extensions for executable files on Windows.
     * This checks the PATHEXT environment variable.
     *
     * @return Array of valid executable extensions
     */
    private static String[] getWindowsExtensions() {
        if (!IS_WINDOWS) {
            return new String[]{""};
        }

        String pathExt = System.getenv("PATHEXT");
        if (pathExt == null || pathExt.isEmpty()) {
            return WINDOWS_EXTENSIONS;
        }

        List<String> extensions = new ArrayList<>();
        extensions.add(""); // Always include empty extension
        
        for (String ext : pathExt.split(PATH_SEPARATOR)) {
            if (!ext.isEmpty()) {
                extensions.add(ext.toLowerCase());
            }
        }

        return extensions.toArray(new String[0]);
    }
}
