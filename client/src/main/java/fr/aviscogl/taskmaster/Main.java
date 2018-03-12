package fr.aviscogl.taskmaster;

import java.io.IOException;

public class Main {

    private final static String DEFAULT_HOST = "LOCALHOST";
    private final static int DEFAULT_PORT = 9898;

    public static void main(String[] args) {
        Client client = args.length == 2
                ? new Client(args[0], Integer.parseInt(args[1]))
                : new Client(DEFAULT_HOST, DEFAULT_PORT);

        try {
            client.connectToServer();
        } catch (IOException e) {
            System.err.println("Impossible to connect the client at host=" + client.getHost() + " and port=" +
                    client.getPort() + ".");
            System.exit(0);
        }

        new Readline(client).read();
    }
}
