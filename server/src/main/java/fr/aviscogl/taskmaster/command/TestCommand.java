package fr.aviscogl.taskmaster.command;

@Command(label = "process")
public class TestCommand extends CommandExecutor {

    @CommandRouter(regexMatcher = "^(\\d+)$")
    public void helloPid(int pid) {
        out.println("short pid = " + pid);
        out.println("{END}");
    }

    @CommandRouter(regexMatcher = "^(?:status|state) (\\d+)$")
    public void statusPid(int pid) {
        out.println("pid = " + pid);
        out.println("{END}");
    }

    @CommandRouter(regexMatcher = "^(?:status|state) ([a-zA-Z]+[a-zA-Z0-9]*)$")
    public void statusName(String processName) {
        System.out.println("Hey ?");
        out.println("process name = " + processName);
        out.println("{END}");
    }

    @Override
    public void defaultMethod() {
        out.println("Help:");
        out.println("  * process [pid]");
        out.println("  * process status [pid or name of the process]");
        out.println("{END}");
    }
}
