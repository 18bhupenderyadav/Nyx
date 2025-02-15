package com.nyx.shell.commands;

import com.nyx.shell.Command;

public class ExitCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.print("exit 0");
    }
}
