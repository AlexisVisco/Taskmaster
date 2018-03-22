package fr.aviscogl.taskmaster.command.list;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.command.Command;
import fr.aviscogl.taskmaster.command.CommandExecutor;
import fr.aviscogl.taskmaster.command.CommandRouter;
import fr.aviscogl.taskmaster.util.ProcessUtil;

@Command(alias = {"stats", "st"}, label = "status")
public class Status extends CommandExecutor {

    @CommandRouter(regex = "all", priority = 3)
    public void all() {
        out.println(ProcessUtil.stringifyInfo(Server.process.values()));
        end();
    }

    @CommandRouter(regex = "([a-zA-Z0-9]+) (\\d+)", priority = 2)
    public void statusNameWithNumber(String name, int num) {
        out.println("status name " + name + "_" + num);
        end();
    }

    @CommandRouter(regex = "(\\d+)", priority = 1)
    public void statusPid(int pid) {
        out.println("status pid " + pid);
        end();
    }

    @CommandRouter(regex = "([a-zA-Z0-9]+)")
    public void statusName(String name) {
        out.println("status name " + name);
        end();
    }

    @Override
    public void defaultMethod() {
        helpLine("Status")
                .helpCommand("status <pid>", "Show the status of the process with $pid")
                .helpCommand("status <name>", "Show the status of the process that contain $name")
                .helpCommand("status <name> <num>", "Show the status of the process that contain $name and the process number $num")
                .helpCommand("status all", "Show globally the status of all process")
        .end();

    }
}
