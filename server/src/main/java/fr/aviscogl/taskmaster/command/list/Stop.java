package fr.aviscogl.taskmaster.command.list;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.command.Command;
import fr.aviscogl.taskmaster.command.CommandExecutor;
import fr.aviscogl.taskmaster.command.CommandRouter;
import fr.aviscogl.taskmaster.manage.ProcessHandler;

@Command(label = "stop", alias = {"kill"})
public class Stop extends CommandExecutor {

    @CommandRouter(regex = "(\\w+) (\\d+)")
    public void stopProcess(String name, Integer num) {
        ProcessHandler.getByNum(name, num).ifPresentOrElse(
                (e) -> {
                    if (e.isAlive()) {
                        out.println("Stopping process " + e.getCurrentName());
                        e.stop();
                    }
                    else out.println("Process is already stopped.");
                },
                () -> out.println("No process for this name.")
        );
        end();
    }

    @CommandRouter(regex = "(\\w+)")
    public void stopProcess(String name) {
        ProcessHandler.getByName(name).ifPresentOrElse(
                (e) -> {
                    if (e.getAliveProcesses() != 0) {
                        out.println("Stopping process all processes for program " + e.getConfig().name);
                        e.killAllProcesses();
                    }
                    else out.println("Program has already all processes stopped.");
                },
                () -> out.println("No program for this name.")
        );
        end();
    }

    @CommandRouter(regex = "(\\d+)")
    public void stopPid(long pid) {
        ProcessHandler.getByPid(pid).ifPresentOrElse(
                (e) -> {
                    if (e.isAlive()) {
                        out.println("Stopping process " + e.getCurrentName());
                        e.stop();
                    }
                    else out.println("Process is already stopped.");
                },
                () -> out.println("No process for this pid.")
        );
        end();
    }

    @CommandRouter(regex = "all")
    public void stopAll() {
        out.println("Stopping all processes !");
        Server.processes.values().forEach(ProcessHandler::killAllProcesses);
        end();
    }

    @Override
    public void defaultMethod() {
        helpLine("Stop")
                .helpCommand("stop <name>", "Stop all processes in program $name.")
                .helpCommand("stop all", "Stop all processes actives.")
                .helpCommand("stop <pid>", "Stop the process with the pid $pid.")
                .helpCommand("stop <name> <id>", "Stop the process $id in the program $name.")
                .end();
    }
}
