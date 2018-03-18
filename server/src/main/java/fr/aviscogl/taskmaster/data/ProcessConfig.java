package fr.aviscogl.taskmaster.data;

import java.util.Arrays;
import java.util.HashMap;

public class ProcessConfig {

    public String name;
    public String cmd;
    public int numprocs;
    public String umask;
    public String workingdir;
    public boolean autostart;
    public RestartType autorestart;
    public int[] exitcodes;
    public int startretries;
    public int starttime;
    public int stoptime;
    public String stdout;
    public String stderr;
    public String stopsignal;
    public HashMap<String, String> env;

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
