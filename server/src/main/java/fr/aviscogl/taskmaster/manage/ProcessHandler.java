package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.data.IProcessEntity;
import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.util.Color;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ProcessHandler {

    final ExecutorService executor;
    final Logger          out;
          ProcessConfig   config;

    public  HashMap<Integer, ProcessEntity> processes = new HashMap<>();
    private boolean                         started   = false;
    private long                            startedAt;

    public ProcessHandler(ProcessConfig config) {
        out = new Logger(config.name, config.name + "/log");
        this.executor = Executors.newFixedThreadPool(config.numprocs > 0 ? config.numprocs * 2 : 1);
        this.config = config;
        if (config.autostart)
            startAllProcesses();
    }

    public boolean killProcesses(int i) {
        ProcessEntity selfProcess = processes.get(i);
        if (selfProcess != null)
            selfProcess.kill(null);
        out.log(Level.WARNING, "No processes processes %s%i.", config.name, i);
        return false;
    }

    public boolean killAllProcesses() {
        if (started) {
            processes.forEach((k, v) -> {
                out.log(Level.INFO, "Terminating processes %s.", v.getCurrentName());
                v.kill(null);
            });
            return true;
        }
        return false;
    }

    public boolean startAllProcesses() {
        if (!this.started || canRestart()) {
            this.started = true;
            this.startedAt = System.currentTimeMillis();
            Optional<ProcessBuilder> pb = config.constructProcessBuilder();
            if (!pb.isPresent()) {
                out.logErr("Cannot start processes %s because command is invalid.", config.name);
                this.started = false;
                return false;
            }
            for (int i = 0; i < config.numprocs; i++) {
                ProcessEntity selfProcess = new ProcessEntity(this, pb.get(), i);
                processes.put(i, selfProcess);
                selfProcess.start();
            }
            return true;
        }
        return false;
    }

    public long getAliveProcesses() {
        return processes.values().stream().filter(ProcessEntity::isAlive).count();
    }

    public String getStringState() {

        long i = Math.round((((double)getAliveProcesses() / (double)config.numprocs) * (double)3));
        if (i == 1) return  Color.RED_BOLD + "danger :x" + Color.RESET ;
        if (i == 2) return  Color.YELLOW_BOLD + "warning :o" + Color.RESET;
        if (i == 3) return Color.GREEN_BOLD_BRIGHT + "like a charm c:" + Color.RESET;
        else return Color.WHITE_BRIGHT + Color.RED_BACKGROUND  + "critical :z" + Color.RESET;
    }

    public Optional<IProcessEntity> getProcessEntity(int p) {
        return Optional.ofNullable(processes.get(p));
    }

    public ProcessConfig getConfig() {
        return config;
    }

    private boolean canRestart() {
        for (ProcessEntity processEntity : processes.values()) {
            if (processEntity.isAlive())
                return false;
        }
        return true;
    }

    public String startedAt() {
        return new SimpleDateFormat("dd/MM HH:mm:ss").format(new Date(startedAt));
    }

    public static Optional<ProcessEntity> getByPid(long pid) {
        for (ProcessHandler processHandler : Server.processes.values()) {
            for (ProcessEntity processEntity : processHandler.processes.values()) {
                if (processEntity.getPid().isPresent() && processEntity.getPid().get() == pid)
                    return Optional.of(processEntity);
            }
        }
        return Optional.empty();
    }

    public static Optional<ProcessEntity> getByNum(String name, int num) {
        ProcessHandler processHandler = Server.processes.get(name);
        if (processHandler != null) {
            for (ProcessEntity pe : processHandler.processes.values()) {
                if (pe.getId() == num)
                    return Optional.of(pe);
            }
        }
        return Optional.empty();
    }

    public static Optional<ProcessHandler> getByName(String name) {
        return Optional.of(Server.processes.get(name));
    }
}
