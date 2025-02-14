package com.nyx.shell;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
//        System.out.print("$ ");
//
//        Scanner scanner = new Scanner(System.in);
//        String input = scanner.nextLine();
//
//        System.out.println(input + ": command not found");

        CommandRegistry registry = new CommandRegistry();

        // Register valid commands here. For now, none are registered,
        // so every command will be treated as invalid.
        // e.g., registry.registerCommand("help", new HelpCommand());

        Shell shell = new Shell(registry);
        shell.run();
    }
}
