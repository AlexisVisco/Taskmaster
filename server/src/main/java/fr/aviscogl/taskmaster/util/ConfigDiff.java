package fr.aviscogl.taskmaster.util;

import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.data.Programs;

import java.lang.reflect.Field;
import java.util.Optional;

public class ConfigDiff {
    Programs origin;
    Programs updated;

    public ConfigDiff(Programs origin, Programs updated) {
        this.origin = origin;
        this.updated = updated;
    }

    public String viewDiff() {
        StringBuilder sb = new StringBuilder();
        updated.getPrograms().forEach(newConfig -> sb.append(viewDiff(newConfig, newConfig.name)));
        return sb.toString();
    }

    public String viewDiff(String name) {
        StringBuilder sb = new StringBuilder();
        updated.getConfig(name).ifPresentOrElse(
                pr -> sb.append(viewDiff(pr, name)),
                () -> sb.append(viewDiff(null, name))
        );
        return sb.toString();
    }

    private String viewDiff(ProcessConfig newConfig, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":\n");
        Optional<ProcessConfig> config = origin.getConfig(name);
        for (Field field : ProcessConfig.class.getDeclaredFields()) {
            try {
                if (!config.isPresent()) {
                    Object newField = field.get(newConfig);
                    sb.append(Color.GREEN + "  + ").append(getSimple(field, newField)).append(Color.RESET).append('\n');
                }
                else {
                    Object oldField = field.get(config.get());
                    if(newConfig == null)
                        sb.append(Color.RED + "  - ").append(getSimple(field, oldField)).append(Color.RESET).append('\n');
                    else {
                        Object newField = field.get(newConfig);
                        if (!oldField.toString().equals(newField.toString()))
                            sb.append(Color.RED + "  - ").append(getSimple(field, oldField)).append(Color.RESET).append('\n')
                                .append(Color.GREEN + "  + ").append(getSimple(field, newField)).append(Color.RESET).append('\n');
                        else
                            sb.append("  ").append(getSimple(field, oldField)).append('\n');
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String getSimple(Field f, Object o) {
        return f.getName() + ": " + (o == null ? "Unspecified" : o.toString());
    }
}