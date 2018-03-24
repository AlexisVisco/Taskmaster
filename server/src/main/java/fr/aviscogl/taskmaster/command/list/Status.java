package fr.aviscogl.taskmaster.command.list;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.command.Command;
import fr.aviscogl.taskmaster.command.CommandExecutor;
import fr.aviscogl.taskmaster.command.CommandRouter;
import fr.aviscogl.taskmaster.manage.ProcessEntity;
import fr.aviscogl.taskmaster.manage.ProcessHandler;
import fr.aviscogl.taskmaster.util.Stringify;

@Command(alias = {"stats", "st"}, label = "status")
public class Status extends CommandExecutor {

    @CommandRouter(regex = "all", priority = 3)
    public void all() {
        out.println(Stringify.of(Server.processes.values()));
        end();
    }

    @CommandRouter(regex = "([a-zA-Z0-9]+) (\\d+)", priority = 2)
    public void statusNameWithNumber(String name, int num) {
        ProcessHandler.getByNum(name, num).ifPresentOrElse(
                pe -> out.println("\n" + Stringify.of(pe)),
                () -> out.println("No processes with name name " + name + "_" + num)
        );
        end();
    }

    @CommandRouter(regex = "(\\d+)", priority = 1)
    public void statusPid(long pid) {
        ProcessHandler.getByPid(pid).ifPresentOrElse(
                e -> out.println(Stringify.of(e)),
                () -> out.println("This PID does not allow to find a processes launched by taskmaster")
        );
        end();
    }

    @CommandRouter(regex = "([a-zA-Z0-9]+)")
    public void statusName(String name) {
        ProcessHandler.getByName(name).ifPresentOrElse(
                p -> out.println(Stringify.of(p)),
                () -> out.println("No processes specified with this name")
        );
        end();
    }

    @Override
    public void defaultMethod() {
        helpLine("Status")
                .helpCommand("status <pid>", "Show the status of the processes with $pid.")
                .helpCommand("status <name>", "Show the status of the processes that contain $name.")
                .helpCommand("status <name> <num>", "Show the status of the processes that contain $name and the processes number $num.")
                .helpCommand("status all", "Show globally the status of all processes.")
        .end();

    }
}
