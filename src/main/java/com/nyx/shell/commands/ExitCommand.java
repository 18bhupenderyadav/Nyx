package com.nyx.shell.commands;

import com.nyx.shell.Command;

import static java.lang.System.exit;

public class ExitCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.print("exit 0");
        exit(0);
    }
}
