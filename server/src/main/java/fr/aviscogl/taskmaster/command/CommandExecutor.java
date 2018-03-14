package fr.aviscogl.taskmaster.command;

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
}
