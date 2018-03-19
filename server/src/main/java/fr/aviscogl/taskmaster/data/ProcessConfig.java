package fr.aviscogl.taskmaster.data;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public long starttime;
    public long stoptime;
    public String stdout;
    public String stderr;
    public String stopsignal;
    public HashMap<String, String> env;

    public Optional<ProcessBuilder> constructProcessBuilder() {

        ProcessBuilder pb = new ProcessBuilder();

        if (cmd == null)
            return Optional.empty();

        pb.command(cmd.split(" "));
        if (this.workingdir != null) {
            File wd = new File(this.workingdir);
            if (wd.exists() && wd.isDirectory())
                pb.directory(wd);
        }

        if (this.env != null) {
            Map<String, String> environment = pb.environment();
            this.env.forEach(environment::put);
        }

        if (this.stderr != null)
            pb.redirectError(new File(this.stderr));
        if (this.stdout != null)
            pb.redirectOutput(new File(this.stdout));

        return Optional.ofNullable(pb);
    }
}
