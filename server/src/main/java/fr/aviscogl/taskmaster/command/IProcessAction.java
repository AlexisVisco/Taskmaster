package fr.aviscogl.taskmaster.command;

public interface IProcessAction {

    @CommandRouter(regex = "(\\w+) (\\d+)")
    void processNameNum(String name, int num);

    @CommandRouter(regex = "(\\w+)")
    void processName(String name);

    @CommandRouter(regex = "(\\d+)", priority = 1)
    void processPid(int pid);

    @CommandRouter(regex = "all", priority = 2)
    void processAll();

}
