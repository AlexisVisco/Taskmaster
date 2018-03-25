package fr.aviscogl.taskmaster.util;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.manage.ProcessEntity;
import fr.aviscogl.taskmaster.manage.ProcessHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            Server.global.logErr("Impossible to set umask for %s.", mask);
        }
    }

    public static void sendSignal(String signal, int pid) {
        try {
            final ProcessBuilder pb = new ProcessBuilder("kill", String.format("-%s", signal), Integer.toString(pid));
            pb.redirectErrorStream(true);
            pb.start();
        } catch (IOException ignored) { }
    }
}