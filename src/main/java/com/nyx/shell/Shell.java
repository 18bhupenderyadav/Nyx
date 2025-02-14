package com.nyx.shell;

import java.util.Scanner;

public class Shell {
    private CommandRegistry commandRegistry;

    public Shell(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("$ ");
            if(!scanner.hasNextLine())
                break;

            String input = scanner.nextLine().trim();
            if(input.isEmpty())
                continue;

            String[] tokens = input.split("\\s+");

            String commandName = tokens[0];

            String[] args = new String[tokens.length - 1];
            if(tokens.length > 1) {
                System.arraycopy(tokens, 1, args, 0, tokens.length - 1);
            }

            // Retrieve the command from the registry
            Command command = commandRegistry.getCommand(commandName);
            if(command != null) {
                command.execute(args);
            } else {
                System.out.println(commandName + ": command not found");
            }
        }
        scanner.close();
    }
}
