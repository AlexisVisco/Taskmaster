package fr.aviscogl.taskmaster;

import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.manage.RequestHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    public static int PORT = 9898;
    public static int CLIENT_NUMBER = 0;

    public static void main(String[] args)
    {
        Pattern p = Pattern.compile("^hello \\d+ \".+\"");
        String test = "hello 34 \"hey\"";
        Matcher m = p.matcher(test);
        if (m.find()) {
            for (int i = 1; i < m.groupCount(); i++) {
                System.out.println(m.group(i));
            }
        }
        System.out.println(test.split(" ")[0]);
    }


//    public static void main(String[] args) throws Exception {
//        System.out.println("The capitalization server is running.");
//        try (ServerSocket listener = new ServerSocket(PORT)) {
//            while (true)
//                new RequestHandler(listener.accept(), CLIENT_NUMBER++).start();
//        }
//    }
}