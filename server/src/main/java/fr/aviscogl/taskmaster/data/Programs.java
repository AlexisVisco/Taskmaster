package fr.aviscogl.taskmaster.data;

import java.util.List;

public class Programs {
    public List<ProcessConfig> programs;

    public Programs() {
    }

    public List<ProcessConfig> getPrograms() {
        return programs;
    }

    public Programs setPrograms(List<ProcessConfig> programs) {
        this.programs = programs;
        return this;
    }
}