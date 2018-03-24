package fr.aviscogl.taskmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final static String END =  Color.YELLOW_BRIGHT + "----------------{ " + Color.RESET + Color.GREEN +
            "TaskMaster" + Color.RESET + Color.YELLOW_BRIGHT + " }----------------" + Color.RESET;


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
                if (res.equals(END))
                    break;
                lineFromServer.append(res).append('\n');
            }
        } catch (IOException ex) {
            reconnect(ex.getMessage());
        }
        return lineFromServer.toString();
    }

    void reconnect(String message) {
        if (message.contains("exit")) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
        else {
            System.out.println("Trying to reconnect... (" + message + ")");
            connectToServer();
        }
    }

    void connectToServer() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("-----------------------------------------------------------------");
            System.out.println(in.readLine());
            System.out.println("-----------------------------------------------------------------\n");
            Main.first = false;
        } catch (Exception e) {
            if (Main.first) {
                System.out.println("Impossible to connect at " + host + ":" + port + ".");
                System.exit(1);
            }
            else
                System.out.println("Connection failed, press a key to try reconnection !");
        }
    }


    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }
}