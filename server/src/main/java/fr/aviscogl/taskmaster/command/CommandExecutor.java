package fr.aviscogl.taskmaster.command;

import java.io.PrintWriter;

public abstract class CommandExecutor {

    public PrintWriter out;
    public String name;
    public String args[];

    public abstract void defaultMethod();
}
