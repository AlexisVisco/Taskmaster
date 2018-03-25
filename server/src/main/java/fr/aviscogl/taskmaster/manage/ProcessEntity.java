package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.data.IProcessEntity;
import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.data.ProcessStatus;
import fr.aviscogl.taskmaster.data.RestartType;
import fr.aviscogl.taskmaster.util.ProcessUtil;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ProcessEntity implements IProcessEntity, Runnable {

    private final ProcessHandler parent;

    private final int            id;
    private       ProcessBuilder pb;
    private       Process        process;
    private       long           timeAtLaunch;
    private       ProcessStatus  status;
    private       long           startRetries      = 0;
    private       long           restart           = 0;
    private       boolean        needToBeRestarted = true;
    private       long           endAt;

    public ProcessEntity(ProcessHandler parent, ProcessBuilder pb, int id) {
        this.parent = parent;
        this.pb = pb;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            parent.out.log("Process %s started.", getCurrentName());
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
            parent.out.logErr("Error while launching %s.", getCurrentName());
        }
    }

    private void onExitProcess() {
        CompletableFuture<Process> onExit = process.onExit();
        try {
            onExit.get();
            onExit.thenAccept((p) -> {
                endAt = System.currentTimeMillis();
                parent.out.log("Process %s exited with value %d.", getCurrentName(), p.exitValue());
                this.updateStatus();
                if (needToBeRestarted && !restartOnFail() && status == ProcessStatus.LAUNCHED)
                {
                    status = ProcessStatus.TERMINATED;
                    if (parent.config.autorestart == RestartType.always)
                        this.start();
                    else if (parent.config.autorestart == RestartType.unexpected && !isNornalExitCode(p.exitValue())) {
                        this.start();
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
        if (status == ProcessStatus.LAUNCHING && parent.config.startretries >= startRetries)
        {
            parent.out.log("Trying to restart process %s, remaining %d. (cause: fail on start)", getCurrentName(),
                    parent.config.startretries - startRetries);
            status = ProcessStatus.TERMINATED;
            startRetries++;
            this.start();
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
                if (isAlive()) {
                    process.destroyForcibly();
                    parent.out.log(Level.WARNING, "Forced to kill processes %s.", getCurrentName());
                }
            }, parent.config.stoptime, TimeUnit.SECONDS);
        }
        else parent.out.logErr("Cant kill process %s because it is already stopped.", getCurrentName());
    }

    private boolean isNornalExitCode(int exitCode) {
        return parent.config.exitcodes.stream().anyMatch(e -> e == exitCode);
    }

    private void updateStatus() {
        boolean b = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.timeAtLaunch)
                >= parent.config.starttime;
        if (b && this.status == ProcessStatus.LAUNCHING)
            status = ProcessStatus.LAUNCHED;
    }

    public void start() {
        parent.executor.execute(this);
    }

    @Override
    public Optional<Long> getPid() {
        return Optional.ofNullable(getStatus().canAccessInfo() ? process.pid() : null);
    }

    @Override
    public long getDuration() {
        return TimeUnit.MILLISECONDS.toSeconds((getStatus() == ProcessStatus.TERMINATED ? endAt : System.currentTimeMillis()) - this.timeAtLaunch);
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
        return getStatus() != ProcessStatus.TERMINATED;
    }

    @Override
    public void stop() {
        this.kill(null);
    }

    @Override
    public void restart() {
        parent.out.log("Restarting process %s.", getCurrentName());
        if (status.ordinal() <= 2) {
            this.kill(this::start);
        }
        else
            this.start();
    }

    public int getId() {
        return id;
    }

    public void setProcessBuilder(ProcessBuilder processBuilder) {
        this.pb = processBuilder;
    }
}
