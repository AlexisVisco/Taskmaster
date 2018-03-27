package fr.aviscogl.taskmaster;

import fr.aviscogl.taskmaster.command.CommandHandler;
import fr.aviscogl.taskmaster.command.list.*;
import fr.aviscogl.taskmaster.data.Programs;
import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.manage.ProcessHandler;
import fr.aviscogl.taskmaster.manage.RequestHandler;
import fr.aviscogl.taskmaster.util.Color;
import fr.aviscogl.taskmaster.util.Jsoner;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Optional;

public class Server {

    public static String END = Color.YELLOW_BRIGHT + "----------------{ " + Color.RESET + Color.GREEN +
            "TaskMaster" + Color.RESET + Color.YELLOW_BRIGHT + " }----------------" + Color.RESET;
    private static int PORT = 9898;
    private static int CLIENT_NUMBER = 0;
    public static HashMap<String, ProcessHandler> processes = new HashMap<>();
    public static Logger global = new Logger("taskmaster", "taskmaster.log");
    public static Programs programs = null;

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            if (Integer.parseInt(args[0]) > 0)
                PORT = Integer.parseInt(args[0]);
        }
        System.out.println("The server is running !");
        initPrograms();
        registerCommands();
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true)
                new RequestHandler(listener.accept(), CLIENT_NUMBER++).start();
        }
    }

    private static void registerCommands() {
        Server.global.log("Registering all commands.");
        CommandHandler.registerCommand(Status.class);
        CommandHandler.registerCommand(Start.class);
        CommandHandler.registerCommand(Config.class);
        CommandHandler.registerCommand(Restart.class);
        CommandHandler.registerCommand(Stop.class);
    }

    private static void initPrograms() {
        Server.global.log("Retrieving configuration file at %s.", new File("sample.json").getAbsolutePath());
        Optional<Programs> jsonFromFile = Jsoner.getJsonFromFile(new File("sample.json"), Programs.class);
        if (jsonFromFile.isPresent())
            programs = jsonFromFile.get();
        else
        {
            Server.global.logErr("No configuration file loaded !");
            System.exit(0);
        }
        programs.getPrograms().forEach(e -> processes.put(e.name, new ProcessHandler(e)));
    }
}