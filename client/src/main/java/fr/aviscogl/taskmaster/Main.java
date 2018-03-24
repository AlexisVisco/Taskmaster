package fr.aviscogl.taskmaster;

import java.io.IOException;

public class Main {

    private final static String DEFAULT_HOST = "LOCALHOST";
    private final static int DEFAULT_PORT = 9898;
    static boolean first = true;

    public static void main(String[] args) {

        Client client = args.length == 2
                ? new Client(args[0], Integer.parseInt(args[1]))
                : new Client(DEFAULT_HOST, DEFAULT_PORT);


        client.connectToServer();


        new Readline(client).read();

    }
}
