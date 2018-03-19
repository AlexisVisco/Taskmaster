package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.data.ProcessStatus;
import fr.aviscogl.taskmaster.data.RestartType;
import fr.aviscogl.taskmaster.log.Logger;

import javax.security.auth.login.LoginException;
import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ProcessManager {

    private ProcessConfig config;
    private HashMap<Integer, SelfProcess> process = new HashMap<>();
    private boolean started = false;

    public ProcessManager(ProcessConfig config) {
        this.config = config;
        if (config.autostart)
            startAllProcess();
    }

    private Optional<String> getUmask() {
        try {
            String[] commands = {"umask"};
            Process proc = Runtime.getRuntime().exec(commands);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            return Optional.ofNullable(stdInput.readLine());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void setUmask(String mask) {
        try {
            String[] commands = {"umask", mask};
            Process exec = Runtime.getRuntime().exec(commands);
            exec.waitFor();
        } catch (Exception e) {
            Logger.logErr("Impossible to set umask for %s.", mask);
        }
    }

    public boolean sendSignal(String signal, int pid) {
        final long start = System.currentTimeMillis();
        try {
            final ProcessBuilder pb = new ProcessBuilder("stop", String.format("-%s", signal, Integer.toString(pid)));
            pb.redirectErrorStream(true);
            pb.start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean killProcess(int i) {
        SelfProcess selfProcess = process.get(i);
        if (selfProcess != null)
            selfProcess.stop();
        Logger.log(Level.WARNING, "No process process %s%i.", config.name, i);
        return false;
    }

    public boolean killAllProcess() {
        if (started) {
            process.forEach((k, v) -> {
                Logger.log(Level.INFO, "Terminating process %s.", v.name);
                v.stop();
            });
            return true;
        }
        return false;
    }

    public boolean startAllProcess() {
        this.started = true;
        Optional<ProcessBuilder> pb = config.constructProcessBuilder();
        if (!pb.isPresent()) {
            Logger.logErr("Cannot launch process %s because command is invalid.", config.name);
            this.started = false;
            return false;
        }

        for (int i = 0; i < config.numprocs; i++) {
            SelfProcess selfProcess = new SelfProcess(pb.get(), i);
            process.put(i, selfProcess);
            selfProcess.start();
        }
        return true;
    }

    public final class SelfProcess {

        private String name;
        private ProcessBuilder pb;
        private Process process;
        private int startRetries = 0;
        private long timeAtLaunch;
        private ProcessStatus status;

        private SelfProcess(ProcessBuilder pb, int id) {
            this.pb = pb;
            this.name = config.name + "_" + id;
        }

        private void start() {
            try {
                Optional<String> umask = getUmask();
                if (!umask.isPresent()) {
                    restart();
                    return;
                }
                setUmask(config.umask);
                this.timeAtLaunch = System.currentTimeMillis();
                this.status = ProcessStatus.LAUNCHED;
                this.process = pb.start();
                this.onExitProcess();
                setUmask(umask.get());
            } catch (Exception e) {
                Logger.logErr("Error while launching " + this.name);
            }
        }

        private void onExitProcess() {
            CompletableFuture<Process> onExit = process.onExit();
            try {
                onExit.get();
                onExit.thenAccept((p) -> {
                    updateStatus();
                    if (!restart() && status == ProcessStatus.LAUNCHED)
                    {
                        status = ProcessStatus.TERMINATED;
                        if (config.autorestart == RestartType.always)
                            this.start();
                        else if (config.autorestart == RestartType.unexpected && !isNornalExitCode(p.exitValue()))
                            this.start();
                    }
                    else
                        status = ProcessStatus.TERMINATED;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean restart() {
            if (status == ProcessStatus.LAUNCHING && config.startretries < startRetries)
            {
                status = ProcessStatus.TERMINATED;
                startRetries++;
                this.start();
                return true;
            }
            return false;
        }

        private void stop() {
            if (process.isAlive()) {
                status = ProcessStatus.TERMINATING;
                sendSignal(config.stopsignal, Math.toIntExact(process.pid()));
                Executors.newScheduledThreadPool(1).schedule(() -> {
                    process.destroyForcibly();
                    Logger.log(Level.WARNING, "Forced to stop process %s.", this.name);
                }, config.stoptime, TimeUnit.SECONDS);
            }
            else {
                Logger.logErr("Cant stop process %s because is already stopped.", this.name);
            }
        }

        private boolean isNornalExitCode(int exitCode) {
            return Arrays.stream(config.exitcodes).anyMatch(e -> e == exitCode);
        }

        private boolean updateStatus() {
            boolean b = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.timeAtLaunch)
                    >= config.starttime;
            if (b && this.status == ProcessStatus.LAUNCHING)
                status = ProcessStatus.LAUNCHED;
            return b;
        }

        private Optional<Long> getPid() {
            return Optional.ofNullable(getStatus() == ProcessStatus.LAUNCHED ? process.pid() : null);
        }

        private ProcessStatus getStatus() {
            updateStatus();
            return status;
        }
    }
}
