package fr.aviscogl.taskmaster.data;

import java.util.List;
import java.util.Optional;

public class Programs {

    public List<ProcessConfig> programs;

    public Programs() { }

    public List<ProcessConfig> getPrograms() {
        return programs;
    }

    public Optional<ProcessConfig> getConfig(String name) {
        return programs.stream().filter(e -> e.name.equals(name)).findFirst();
    }

}