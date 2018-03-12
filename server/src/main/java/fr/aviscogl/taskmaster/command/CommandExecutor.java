package fr.aviscogl.taskmaster.command;

public class CommandExecutor {

    private String name;
    private String args[];

    public CommandExecutor(String name, String[] args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }
}
