package fr.aviscogl.taskmaster.command.list;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.command.Command;
import fr.aviscogl.taskmaster.command.CommandExecutor;
import fr.aviscogl.taskmaster.command.CommandRouter;
import fr.aviscogl.taskmaster.data.Programs;
import fr.aviscogl.taskmaster.manage.ProcessHandler;
import fr.aviscogl.taskmaster.util.ConfigDiff;
import fr.aviscogl.taskmaster.util.Jsoner;
import fr.aviscogl.taskmaster.util.Stringify;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(label = "config", alias = {"cfg", "cf"})
public class Config extends CommandExecutor {

    @CommandRouter(regex = "diff")
    public void diff() {
        Optional<Programs> jsonFromFile = Jsoner.getJsonFromFile(new File("sample.json"), Programs.class);
        if (jsonFromFile.isPresent()) {
            Programs programs = jsonFromFile.get();
            out.println(new ConfigDiff(Server.programs, programs).viewDiff());
        }
        end();
    }

    @CommandRouter(regex = "diff (\\w+)")
    public void diff(String name) {
        Optional<Programs> jsonFromFile = Jsoner.getJsonFromFile(new File("sample.json"), Programs.class);
        if (jsonFromFile.isPresent()) {
            Programs programs = jsonFromFile.get();
            out.println(new ConfigDiff(Server.programs, programs).viewDiff(name));
        }
        end();
    }

    @CommandRouter(regex = "show (\\w+)")
    public void showConfig(String name) {
        ProcessHandler.getByName(name).ifPresentOrElse(
                e -> {
                    out.println(name + ":");
                    out.println("\\");
                    out.println(String.join("\n", Arrays.stream(Stringify.of(e.getConfig())
                            .split("\n")).map(x -> " | " + x).collect(Collectors.toList())));
                    out.println(" +---------------------------->");
                },
                () -> out.println("No configuration found for " + name)
        );
        end();
    }

    @CommandRouter(regex = "show all")
    public void showAllConfig() {
        Server.processes.forEach((k, v) -> {
            out.println(k + ":");
            out.println("\\");
            out.println(String.join("\n", Arrays.stream(Stringify.of(v.getConfig())
                    .split("\n")).map(e -> " | " + e).collect(Collectors.toList())));
            out.println(" +---------------------------->");
        });
        end();
    }

    @CommandRouter(regex = "reload (\\w+)")
    public void reloadConfig(String name) {
        Optional<Programs> jsonFromFile = Jsoner.getJsonFromFile(new File("sample.json"), Programs.class);
        if (jsonFromFile.isPresent()) {
            Programs p = jsonFromFile.get();
            p.updateFromThis(name, out);
        }
        end();
    }

    @CommandRouter(regex = "reload")
    public void reloadConfig() {
        List<String> updated = new ArrayList<>();
        Server.programs.programs.forEach(e -> {
            updated.add(e.name);
            reloadConfig(e.name);
        });
        Optional<Programs> jsonFromFile = Jsoner.getJsonFromFile(new File("sample.json"), Programs.class);
        if (jsonFromFile.isPresent()) {
            Programs p = jsonFromFile.get();
            p.programs.forEach(e -> {
                if (!updated.contains(e.name))
                    reloadConfig(name);
            });
        }
        end();
    }

    @Override
    public void defaultMethod() {

        helpLine("Config")
                .helpCommand("config show <name>", "Show config for processes $name.")
                .helpCommand("config show", "Show all config.")
                .helpCommand("config reload <name>", "Reload config for processes $name.")
                .helpCommand("config reload", "Reload all config.")
                .helpCommand("config diff", "Show the difference between the config loaded and the config on the disk.")
                .helpCommand("config diff <name>", "Show the difference between the config loaded and the config on the disk only for the process $name.")
                .end();
    }
}
