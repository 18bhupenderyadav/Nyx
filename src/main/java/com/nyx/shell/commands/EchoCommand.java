package com.nyx.shell.commands;

import com.nyx.shell.Command;

public class EchoCommand implements Command {

    @Override
    public void execute(String[] args) {
        String output = "";
        for (String i : args) {
            output = output + i + " ";
        }
        System.out.println(output);
    }
}
