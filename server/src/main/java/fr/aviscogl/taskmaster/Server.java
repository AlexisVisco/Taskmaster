package fr.aviscogl.taskmaster;

import fr.aviscogl.taskmaster.log.Logger;
import fr.aviscogl.taskmaster.manage.RequestHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class Server {

    public static int PORT = 9898;
    public static int CLIENT_NUMBER = 0;


    public static void main(String[] args) throws Exception {
        System.out.println("The capitalization server is running.");
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true)
                new RequestHandler(listener.accept(), CLIENT_NUMBER++).start();
        }
    }
}