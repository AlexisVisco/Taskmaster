package fr.aviscogl.taskmaster.command;

import java.io.PrintWriter;

@Command(label = "hello")
public class TestCommand extends CommandExecutor {

    //hello 123
    @CommandRouter(regexPatternArguments = "(\\d+)")
    private void helloPid(int pid) {
        System.out.println(pid);
    }

    @Override
    public void defaultMethod() {

    }
}
