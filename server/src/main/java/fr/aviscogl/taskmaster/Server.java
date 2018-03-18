package fr.aviscogl.taskmaster;

import fr.aviscogl.taskmaster.command.CommandHandler;
import fr.aviscogl.taskmaster.command.TestCommand;
import fr.aviscogl.taskmaster.data.ProcessConfig;
import fr.aviscogl.taskmaster.data.Programs;
import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.manage.RequestHandler;
import fr.aviscogl.taskmaster.util.Jsoner;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Optional;

public class Server {

    public static int PORT = 9898;
    public static int CLIENT_NUMBER = 0;
    public static Programs programs;

//    public static void main(String[] args)
//    {
//        Pattern p = Pattern.compile("^hello \\d+ \".+\"");
//        String test = "hello 34 \"hey\"";
//        Matcher m = p.matcher(test);
//        if (m.find()) {
//            for (int i = 1; i < m.groupCount(); i++) {
//                System.out.println(m.group(i));
//            }
//        }
//        System.out.println(test.split(" ")[0]);
//    }


    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        System.out.println("The server is running !");

        Optional<Programs> jsonFromFile = Jsoner.getJsonFromFile(new File("sample.json"), Programs.class);
        if (jsonFromFile.isPresent())
            programs = jsonFromFile.get();
        else
        {
            Logger.logErr("No configuration file loaded !");
            System.exit(0);
        }
        CommandHandler.registerCommand(TestCommand.class);
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true)
                new RequestHandler(listener.accept(), CLIENT_NUMBER++).start();
        }
    }


}