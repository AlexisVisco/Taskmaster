package fr.aviscogl.taskmaster.data;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.manage.ProcessHandler;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Programs {

    public List<ProcessConfig> programs;

    public Programs() {
    }

    public List<ProcessConfig> getPrograms() {
        return programs;
    }

    public Optional<ProcessConfig> getConfig(String name) {
        return programs.stream().filter(e -> e.name.equals(name)).findFirst();
    }

    public void updateFromThis(String name, PrintWriter out) {
        getConfig(name).ifPresentOrElse(
                (e) -> ProcessHandler.getByName(name).ifPresentOrElse(
                        (ph) -> {
                            ph.updateConfig(e);
                            out.println("Updating config of " + name + ".");
                            Server.programs.replace(name, e);
                        },
                        () -> {
                            Server.processes.put(name, new ProcessHandler(e));
                            Server.programs.programs.add(e);
                            out.println("Created a new program named " + name + ".");
                        }
                ),
                () -> ProcessHandler.getByName(name).ifPresentOrElse(
                        (ph) -> {
                            ph.killAllProcesses();
                            Server.processes.remove(name);
                            Server.programs.remove(name);
                            out.println("Removing processes for " + name + " because it is no present in the config.");
                        },
                        () -> out.println("No configuration for " + name + " at all.")
                )
        );
    }

    private void replace(String name, ProcessConfig with) {
        programs = programs.parallelStream().filter(e -> !e.name.equals(name)).collect(Collectors.toList());
        programs.add(with);
    }

    private void remove(String name) {
        programs = programs.parallelStream().filter(e -> !e.name.equals(name)).collect(Collectors.toList());
    }


}