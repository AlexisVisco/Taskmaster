package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.data.IProcessEntity;
import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.data.ProcessStatus;
import fr.aviscogl.taskmaster.data.RestartType;
import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.util.ProcessUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ProcessEntity implements IProcessEntity, Runnable {

    private final ProcessHandler parent;

    private final int            id;
    private final ProcessBuilder pb;
    private       Process        process;
    private       long           timeAtLaunch;
    private       ProcessStatus  status;
    private       long           startRetries      = 0;
    private       long           restart           = 0;
    private       boolean        needToBeRestarted = true;

    public ProcessEntity(ProcessHandler parent, ProcessBuilder pb, int id) {
        this.parent = parent;
        this.pb = pb;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            Logger.log("Process " + getCurrentName() + " started !");
            needToBeRestarted = true;
            restart++;
            Optional<String> umask = ProcessUtil.getUmask();
            if (!umask.isPresent()) {
                this.restartOnFail();
                return;
            }
            ProcessUtil.setUmask(parent.config.umask);
            this.timeAtLaunch = System.currentTimeMillis();
            this.status = ProcessStatus.LAUNCHING;
            this.process = pb.start();
            this.onExitProcess();
            ProcessUtil.setUmask(umask.get());
        } catch (Exception e) {
            Logger.logErr("Error while launching " + getCurrentName());
        }
    }

    private void onExitProcess() {
        CompletableFuture<Process> onExit = process.onExit();
        try {
            onExit.get();
            onExit.thenAccept((p) -> {
                Logger.log("Process " + getCurrentName() + " exited with exitvalue " + p.exitValue());
                this.updateStatus();
                if (needToBeRestarted && !restartOnFail() && status == ProcessStatus.LAUNCHED)
                {
                    status = ProcessStatus.TERMINATED;
                    if (parent.config.autorestart == RestartType.always)
                        this.launch();
                    else if (parent.config.autorestart == RestartType.unexpected && !isNornalExitCode(p.exitValue())) {
                        this.launch();
                    }
                }
                else
                    status = ProcessStatus.TERMINATED;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean restartOnFail() {
        if (status == ProcessStatus.LAUNCHING && parent.config.startretries > startRetries)
        {
            Logger.log("Re-launching process, remaining %d", parent.config.startretries - startRetries);
            status = ProcessStatus.TERMINATED;
            startRetries++;
            this.launch();
            return true;
        }
        return false;
    }

    void kill(Runnable r) {
        if (process.isAlive()) {
            status = ProcessStatus.TERMINATING;
            needToBeRestarted = false;
            if (r != null)
                process.onExit().thenAccept(e -> r.run());
            ProcessUtil.sendSignal(parent.config.stopsignal, Math.toIntExact(process.pid()));
            Executors.newScheduledThreadPool(1).schedule(() -> {
                process.destroyForcibly();
                Logger.log(Level.WARNING, "Forced to kill process %s.", getCurrentName());
            }, parent.config.stoptime, TimeUnit.SECONDS);
        }
        else {
            Logger.logErr("Cant kill process %s because is already stopped.", getCurrentName());
        }
    }

    private boolean isNornalExitCode(int exitCode) {
        return Arrays.stream(parent.config.exitcodes).anyMatch(e -> e == exitCode);
    }

    private void updateStatus() {
        boolean b = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.timeAtLaunch)
                >= parent.config.starttime;
        if (b && this.status == ProcessStatus.LAUNCHING)
            status = ProcessStatus.LAUNCHED;
    }

    void launch() {
        parent.executor.execute(this);
    }

    @Override
    public Optional<Long> getPid() {
        return Optional.ofNullable(getStatus() == ProcessStatus.LAUNCHED ? process.pid() : null);
    }

    @Override
    public long getDuration() {
        if (getStatus() == ProcessStatus.LAUNCHED) {
            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.timeAtLaunch);
        }
        return 0;
    }

    @Override
    public long getAmountRestartBecauseFail() {
        return this.startRetries;
    }

    @Override
    public long getAmountRestart() {
        return this.restart;
    }

    @Override
    public ProcessConfig getConfig() {
        return parent.config;
    }

    @Override
    public ProcessStatus getStatus() {
        updateStatus();
        return this.status;
    }

    @Override
    public String getParentName() {
        return parent.config.name;
    }

    @Override
    public String getCurrentName() {
        return getParentName() + "_" + id;
    }

    @Override
    public boolean isAlive() {
        return getStatus() == ProcessStatus.LAUNCHED;
    }

    @Override
    public void stop() {
        this.kill(null);
    }

    @Override
    public void restart() {
        if (status.ordinal() <= 2) {
            this.kill(this::launch);
        }
        else
            this.launch();
    }
}
