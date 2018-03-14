package fr.aviscogl.taskmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final static String END_RESPONSE = "{END}";


    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private String host;
    private int port;

    Client(String host, int port) {
        this.host = host;
        this.port = port;

    }

    String getResponseFromCommand(String line) {
        out.println(line);
        StringBuilder lineFromServer = new StringBuilder();
        try {
            String res;
            while ((res = in.readLine()) != null) {
                if (res.equals(END_RESPONSE))
                    break;
                lineFromServer.append(res).append('\n');
            }
        } catch (IOException ex) {
            exitSocket(ex.getMessage());
        }
        return lineFromServer.toString();
    }

    void exitSocket(String message) {
        try {
            socket.close();
            System.out.println("End connection with server due to `" + message + "`.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void connectToServer() throws IOException {

        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("-----------------------------------------------------------------");
        System.out.println(in.readLine());
        System.out.println("-----------------------------------------------------------------\n");
    }


    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }
}