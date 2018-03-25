package fr.aviscogl.taskmaster.command.list;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.command.Command;
import fr.aviscogl.taskmaster.command.CommandExecutor;
import fr.aviscogl.taskmaster.command.IProcessAction;
import fr.aviscogl.taskmaster.manage.ProcessEntity;
import fr.aviscogl.taskmaster.manage.ProcessHandler;

@Command(label = "restart", alias = {"relaunch", "reexecute"})
public class Restart extends CommandExecutor implements IProcessAction {

    @Override
    public void processNameNum(String name, int num) {
        ProcessHandler.getByNum(name, num).ifPresentOrElse(
                (e) -> {
                    out.println("Restarting process " + e.getCurrentName() + ".");
                    e.restart();
                },
                () -> out.println("No process named " + name + "_" + num + ".")
        );
        end();
    }

    @Override
    public void processName(String name) {
        ProcessHandler.getByName(name).ifPresentOrElse(
                (p) -> {
                    out.println("Restarting all processes of program " + name + ".");
                    p.processes.values().forEach(ProcessEntity::restart);
                },
                () -> out.println("No program for name " + name + '.')
        );
        end();
    }

    @Override
    public void processPid(int pid) {
        ProcessHandler.getByPid(pid).ifPresentOrElse(
                (e) -> {
                    out.println("Restarting process " + e.getCurrentName() + ".");
                    e.restart();
                },
                () -> out.println("No process with pid " + pid + ".")
        );
        end();
    }

    @Override
    public void processAll() {
        out.println("Restarting all programs and their processes.");
        Server.processes.values().forEach(p -> p.processes.values().forEach(ProcessEntity::restart));
    }

    @Override
    public void defaultMethod() {
        helpLine("Restart")
                .helpCommand("restart <name> <num>", "Restart process number $num in program $name")
                .helpCommand("restart <name>", "Restart all processes in program $name")
                .helpCommand("restart <pid>", "Restart the processes with pid $pid")
                .helpCommand("restart all", "Restart all processes of each programs")
        .end();
    }
}
