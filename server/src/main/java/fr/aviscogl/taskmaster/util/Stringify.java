package fr.aviscogl.taskmaster.util;

import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.manage.ProcessEntity;
import fr.aviscogl.taskmaster.manage.ProcessHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stringify {
    public static String of(Collection<ProcessHandler> processHandlerList) {
        List<String> headers = Stream.of("Name", "Health", "Amount processes", "Alive processes", "Command", "Launched at")
                .map(x -> Color.WHITE_BOLD + x + Color.RESET).collect(Collectors.toList());
        List<List<String>> rows = new ArrayList<>();
        processHandlerList.forEach(e -> {
            List<String> colums = Arrays.asList(e.getConfig().name, e.getStringState(),
                    Integer.toString(e.getConfig().numprocs), Long.toString(e.getAliveProcesses()), e.getConfig().cmd, e.startedAt());
            rows.add(colums);
        });
        return new TableGenerator().generateTable(headers, rows);
    }

    public static String of(ProcessHandler e) {
        List<String> headers = Stream.of("Name", "Alive", "Status", "Uptime", "Started", "Pid")
                .map(x -> Color.WHITE_BOLD + x + Color.RESET).collect(Collectors.toList());
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

    public static String of(ProcessEntity pe) {
        String st = new SimpleTable()
                .put("Id", String.valueOf(pe.getId()), true)
                .put("Name", pe.getCurrentName(), false)
                .put("Parent name", pe.getParentName(), true)
                .put("Alive", pe.isAlive() ? Color.GREEN_BOLD + "Yes" + Color.RESET : Color.RED_BOLD + "No" + Color.RESET, true)
                .put("Status", pe.getStatus().getState(), true)
                .put("Pid", pe.getPid().isPresent() ? String.valueOf(pe.getPid().get()) : "No pid", true)
                .put("Uptime", pe.getStringDuration(), true)
                .put("Restart", pe.getAmountRestart() + " time(s)", true)
                .put("Fail restart", pe.getAmountRestartBecauseFail() + " time(s)", true).toString();
        return st;

    }

    public static String of(ProcessConfig pc) {
        SimpleTable st = new SimpleTable();
        try {
            for (Field field : ProcessConfig.class.getDeclaredFields()) {
                if (field.get(pc) != null)
                    st.put(field.getName(), field.get(pc).toString(), true);
                else
                    st.put(field.getName(), "unspecified");
            }
        } catch (Exception e) { }
        return st.toString();
    }
}
