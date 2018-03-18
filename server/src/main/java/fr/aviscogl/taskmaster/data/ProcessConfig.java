package fr.aviscogl.taskmaster.data;

import java.util.Arrays;
import java.util.HashMap;

public class ProcessConfig {

    String name;
    String cmd;
    int numprocs;
    String umask;
    String workingdir;
    boolean autostart;
    RestartType autorestart;
    int[] exitcodes;
    int startretries;
    int starttime;
    int stoptime;
    String stdout;
    String stderr;
    String stopsignal;
    HashMap<String, String> env;

    @Override
    public String toString() {
        return "ProcessConfig{" +
                "name='" + name + '\'' +
                ", cmd='" + cmd + '\'' +
                ", numprocs=" + numprocs +
                ", umask='" + umask + '\'' +
                ", workingdir='" + workingdir + '\'' +
                ", autostart=" + autostart +
                ", autorestart=" + autorestart +
                ", exitcodes=" + Arrays.toString(exitcodes) +
                ", startretries=" + startretries +
                ", starttime=" + starttime +
                ", stoptime=" + stoptime +
                ", stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                ", stopsignal='" + stopsignal + '\'' +
                ", env=" + env +
                '}';
    }
}
