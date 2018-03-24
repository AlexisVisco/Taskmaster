package fr.aviscogl.taskmaster.command.list;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.command.Command;
import fr.aviscogl.taskmaster.command.CommandExecutor;
import fr.aviscogl.taskmaster.command.CommandRouter;
import fr.aviscogl.taskmaster.manage.ProcessHandler;

@Command(label = "start", alias = {"start", "go"})
public class Start extends CommandExecutor {

    @CommandRouter(regex = "not-launched", priority = 3)
    public void launchNotLaunched() {
        Server.processes.values().forEach(e -> e.processes.values().forEach(x -> {
            if (!x.isAlive()) {
                out.println("Launching process " + x.getCurrentName() + " !");
                x.start();
            }
        }));
        end();
    }

    @CommandRouter(regex = "(\\w+)")
    public void startProcess(String name) {
        ProcessHandler.getByName(name).ifPresentOrElse(
                (e) -> {
                    if (e.startAllProcesses()) out.println("Launching all processes for program " + e.getConfig().name + ".");
                    else out.println("Impossible to launch all process because some processes are still alive.");
                },
                () -> out.println("No programs found for " + name + ".")
        );
        end();
    }

    @CommandRouter(regex = "not-launched (\\w+)", priority = 2)
    public void startProcessNotLaunched(String name) {
        ProcessHandler.getByName(name).ifPresentOrElse(
                (e) -> e.processes.values().forEach(x -> {
                    if (!x.isAlive()) {
                        out.println("Launching process " + x.getCurrentName() + " !");
                        x.start();
                    }
                }),
                () -> out.println("No program found for " + name + ".")
        );
        end();
    }

    @CommandRouter(regex = "(\\w+) (\\d+)")
    public void startSpecifiedProcess(String name, int num) {
        ProcessHandler.getByNum(name, num).ifPresentOrElse(
                e -> {
                    if (e.isAlive()) {
                        out.println("Process " + name + "_" + num + " already launched.");
                        return;
                    }
                    out.println("Launching " + name + "_" + num + " ...");
                    e.start();
                },
                () -> out.println("The process is unknown or has never been initialized, to initialize it: 'start not-launched'.")
        );
        end();
    }

    @Override
    public void defaultMethod() {
        helpLine("Start")
                .helpCommand("start <name>", "Launch processes for the program named $name in the configuration.")
                .helpCommand("start <name> <number>", "Launch the processes number $number of the program $name.")
                .helpCommand("start not-launched", "Start all processes that are not launched yet.")
                .helpCommand("start not-launched <name>", "Start all processes that are not launched yet in program $name.")
                .end();
    }
}
