package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.log.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Optional;

public class ProcessManager {

    private ProcessConfig config;
    private HashMap<Integer, SelfProcess> process = new HashMap<>();

    public ProcessManager(ProcessConfig config) {
        this.config = config;
        if (config.autostart)
            startProcess();
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
            final ProcessBuilder pb = new ProcessBuilder("kill", String.format("-%s", signal, Integer.toString(pid)));
            pb.redirectErrorStream(true);
            pb.start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void startProcess() {}

    public final static class SelfProcess {

        private ProcessBuilder pb;
        private Process process;
        private int startRetries = 0;
        private long timeAtLaunch = System.currentTimeMillis();
        private boolean successful = false;
        private boolean isTerminated;

    }
}
