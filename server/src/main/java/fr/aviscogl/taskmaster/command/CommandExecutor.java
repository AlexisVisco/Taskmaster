package fr.aviscogl.taskmaster.command;

import fr.aviscogl.taskmaster.util.Color;

import java.io.PrintWriter;
import java.util.Arrays;

public abstract class CommandExecutor {

    public PrintWriter out;
    public String name;
    public String args[];

    public abstract void defaultMethod();

    @Override
    public String toString() {
        return "CommandExecutor{" +
                "out=" + out +
                ", name='" + name + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    protected CommandExecutor helpLine(String describe) {
        out.println("Help for command '" + Color.PURPLE + describe + Color.RESET + "' >>");
        return this;
    }

    public CommandExecutor helpCommand(String command, String description) {
        out.println("  * " + command.replaceAll("(<[a-zA-Z0-9 ]*>)", Color.GREEN + "$1" + Color.RESET) + " - " +
                description.replaceAll("(\\$\\w*)", Color.CYAN + "$1" + Color.RESET));
        return this;
    }

    public void end() {
        out.println("{END}");
    }
}
