package fr.aviscogl.taskmaster;

import fr.aviscogl.taskmaster.command.CommandHandler;
import fr.aviscogl.taskmaster.command.list.Status;
import fr.aviscogl.taskmaster.data.Programs;
import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.manage.ProcessHandler;
import fr.aviscogl.taskmaster.manage.RequestHandler;
import fr.aviscogl.taskmaster.util.Jsoner;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class Server {

    private static int PORT = 9898;
    private static int CLIENT_NUMBER = 0;
    public static HashMap<String, ProcessHandler> process = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        System.out.println("The server is running !");

        initPrograms();
        registerCommands();
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true)
                new RequestHandler(listener.accept(), CLIENT_NUMBER++).start();
        }
    }

    private static void registerCommands() {
        CommandHandler.registerCommand(Status.class);
    }

    private static void initPrograms() {
        Programs programs = null;
        Optional<Programs> jsonFromFile = Jsoner.getJsonFromFile(new File("sample.json"), Programs.class);
        if (jsonFromFile.isPresent())
            programs = jsonFromFile.get();
        else
        {
            Logger.logErr("No configuration file loaded !");
            System.exit(0);
        }
        programs.getPrograms().forEach(e -> process.put(e.name, new ProcessHandler(e)));
    }


}