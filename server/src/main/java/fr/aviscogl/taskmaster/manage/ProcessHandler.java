package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.data.IProcessEntity;
import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.util.Color;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ProcessHandler {

    ProcessConfig config;
    private HashMap<Integer, ProcessEntity> processes = new HashMap<>();
    private boolean started = false;
    final ExecutorService executor;

    public ProcessHandler(ProcessConfig config) {
        this.executor = Executors.newFixedThreadPool(config.numprocs * 2);
        this.config = config;
        if (config.autostart)
            startAllProcesses();
    }

    public boolean killProcesses(int i) {
        ProcessEntity selfProcess = processes.get(i);
        if (selfProcess != null)
            selfProcess.kill(null);
        Logger.log(Level.WARNING, "No processes processes %s%i.", config.name, i);
        return false;
    }

    public boolean killAllProcesses() {
        if (started) {
            processes.forEach((k, v) -> {
                Logger.log(Level.INFO, "Terminating processes %s.", v.getCurrentName());
                v.kill(null);
            });
            return true;
        }
        return false;
    }

    public boolean startAllProcesses() {
        this.started = true;
        Optional<ProcessBuilder> pb = config.constructProcessBuilder();
        if (!pb.isPresent()) {
            Logger.logErr("Cannot launch processes %s because command is invalid.", config.name);
            this.started = false;
            return false;
        }
        for (int i = 0; i < config.numprocs; i++) {
            ProcessEntity selfProcess = new ProcessEntity(this, pb.get(), i);
            processes.put(i, selfProcess);
            selfProcess.launch();
        }
        return true;
    }

    public long getAliveProcesses() {
        return processes.values().stream().filter(ProcessEntity::isAlive).count();
    }

    public String getStringState() {
        int i = (int)((getAliveProcesses() / config.numprocs) * 3);
        if (i == 1) return  "danger" ;
        if (i == 2) return  "warning" ;
        if (i == 3) return "like a charm" ;
        else return  "critical" ;
    }

    public Optional<IProcessEntity> getProcessEntity(int p) {
        return Optional.ofNullable(processes.get(p));
    }

    public ProcessConfig getConfig() {
        return config;
    }
}
