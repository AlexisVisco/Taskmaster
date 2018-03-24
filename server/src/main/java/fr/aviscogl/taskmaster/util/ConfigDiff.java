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
        updated.getPrograms().forEach(newConfig -> {
            sb.append(newConfig.name + "\n");
            Optional<ProcessConfig> config = origin.getConfig(newConfig.name);
            for (Field field : ProcessConfig.class.getDeclaredFields()) {
                try {
                    Object newField = field.get(newConfig);
                    if (!config.isPresent())
                        sb.append(Color.GREEN + "  (+) " + getSimple(field, newField) + Color.RESET).append('\n');
                    else {
                        Object oldField = field.get(config.get());
                        if (!oldField.toString().equals(newField.toString())) {
                            sb.append(Color.RED + "  (-)   " + getSimple(field, oldField) + Color.RESET).append('\n')
                              .append(Color.GREEN + "  (+)-> " + getSimple(field, newField) + Color.RESET).append('\n');
                        }
                        else
                            sb.append("  " + getSimple(field, oldField)).append('\n');
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

        });
        return sb.toString();
    }

    public String getSimple(Field f, Object o) {
        return f.getName() + ": " + (o == null ? "Unspecified" : o.toString());
    }
}