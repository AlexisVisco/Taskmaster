package fr.aviscogl.taskmaster.util;

import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.manage.ProcessEntity;
import fr.aviscogl.taskmaster.manage.ProcessHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ProcessUtil {

    public static Optional<String> getUmask() {
        try {
            String[] commands = {"umask"};
            Process proc = Runtime.getRuntime().exec(commands);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            return Optional.ofNullable(stdInput.readLine());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static void setUmask(String mask) {
        try {
            String[] commands = {"umask", mask};
            Process exec = Runtime.getRuntime().exec(commands);
            exec.waitFor();
        } catch (Exception e) {
            Logger.logErr("Impossible to set umask for %s.", mask);
        }
    }

    public static void sendSignal(String signal, int pid) {
        try {
            final ProcessBuilder pb = new ProcessBuilder("kill", String.format("-%s", signal, Integer.toString(pid)));
            pb.redirectErrorStream(true);
            pb.start();
        } catch (IOException ignored) { }
    }

    public static String stringifyInfo(Collection<ProcessHandler> processHandlerList) {
        List<String> headers = Arrays.asList("Name", "Health", "Amount processes", "Alive processes", "Command");
        List<List<String>> rows = new ArrayList<>();
        processHandlerList.forEach(e -> {
            List<String> colums = Arrays.asList(e.getConfig().name, e.getStringState(),
                    Integer.toString(e.getConfig().numprocs), Long.toString(e.getAliveProcesses()), e.getConfig().cmd);
            rows.add(colums);
        });
        return new TableGenerator().generateTable(headers, rows);
    }

    public static String stringifyInfoOf(ProcessHandler e) {
        List<String> headers = Arrays.asList("Name", "Alive", "Status", "Uptime", "Sstarted", "Pid");
        List<List<String>> rows = new ArrayList<>();
        e.processes.forEach((i, ent) -> {
            List<String> colums = Arrays.asList(
                    ent.getCurrentName(),
                    ent.isAlive() ? Color.GREEN_BOLD + "Yes" + Color.RESET : Color.RED_BOLD + "No" + Color.RESET,
                    ent.getStatus().getState(),
                    ent.getStringDuration(),
                    Long.toString(ent.getAmountRestart()) + " time(s)",
                    ent.getPid().isPresent() ? Long.toString(ent.getPid().get()) : "No pid");
            rows.add(colums);
        });
        return new TableGenerator().generateTable(headers, rows);
    }
}
