package com.nyx.shell;

import com.nyx.shell.commands.*;

/**
 * The entry point for the Nyx Shell application.
 * This class serves as the main bootstrap for the shell environment,
 * responsible for:
 * 
 * <ul>
 *   <li>Initializing the command registry</li>
 *   <li>Registering all built-in commands</li>
 *   <li>Starting the shell's interactive loop</li>
 * </ul>
 * 
 * <p>The shell supports:</p>
 * <ul>
 *   <li>Built-in commands (cd, pwd, echo, exit, type, help)</li>
 *   <li>External command execution</li>
 *   <li>Input/output redirection (>, >>, 2>, 2>>)</li>
 *   <li>Command-line parsing with proper handling of quotes and escapes</li>
 * </ul>
 * 
 * @author Nyx Shell Team
 * @version 1.0
 */
public class Main {
    
    /**
     * Initializes and starts the shell environment.
     * This method performs the following steps:
     * <ol>
     *   <li>Creates a new command registry</li>
     *   <li>Registers all built-in commands</li>
     *   <li>Initializes the shell with the registry</li>
     *   <li>Starts the shell's interactive loop</li>
     * </ol>
     *
     * @param args Command line arguments (currently unused)
     * @throws Exception If there's an error during shell initialization or execution
     */
    public static void main(String[] args) throws Exception {
        // Create and initialize the command registry
        CommandRegistry registry = initializeCommandRegistry();

        // Initialize and run the shell
        Shell shell = new Shell(registry);
        shell.run();
    }

    /**
     * Initializes the command registry with all built-in commands.
     * This method encapsulates the command registration logic for better maintainability.
     * 
     * Built-in commands:
     * <ul>
     *   <li>help - Display command help</li>
     *   <li>exit - Terminate the shell</li>
     *   <li>echo - Display text</li>
     *   <li>type - Show command information</li>
     *   <li>pwd  - Print working directory</li>
     *   <li>cd   - Change directory</li>
     * </ul>
     *
     * @return A fully initialized CommandRegistry with all built-in commands
     */
    private static CommandRegistry initializeCommandRegistry() {
        CommandRegistry registry = new CommandRegistry();

        // Register core shell commands with descriptions
        registry.registerCommand("help", new HelpCommand(registry), 
            "Display information about shell commands", true);
            
        registry.registerCommand("exit", new ExitCommand(),
            "Exit the shell with a status code", true);
            
        registry.registerCommand("echo", new EchoCommand(),
            "Display text or command output", true);
            
        registry.registerCommand("type", new TypeCommand(registry),
            "Display information about command type", true);
            
        registry.registerCommand("pwd", new PwdCommand(),
            "Print current working directory", true);
            
        registry.registerCommand("cd", new CdCommand(),
            "Change the current directory", true);

        return registry;
    }
}
