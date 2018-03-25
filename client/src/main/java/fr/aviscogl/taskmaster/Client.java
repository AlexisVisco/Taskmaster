package fr.aviscogl.taskmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Client {

    private final static String END = Color.YELLOW_BRIGHT + "----------------{ " + Color.RESET + Color.GREEN +
            "TaskMaster" + Color.RESET + Color.YELLOW_BRIGHT + " }----------------" + Color.RESET;


    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private String host;
    private int port;
    boolean isClose = false;
    private ScheduledFuture<?> st;

    Client(String host, int port) {
        this.host = host;
        this.port = port;

    }

    String getResponseFromCommand(String line) {
        if (!socketAlive()) {
            isClose = true;
            return "Connection lost... Attempting to connect to the server !";
        }
        StringBuilder lineFromServer = new StringBuilder();
        try {
            out.println(line);
            String res;
            while ((res = in.readLine()) != null) {
                if (res.equals(END))
                    break;
                lineFromServer.append(res).append('\n');
            }
        } catch (IOException ex) {
            isClose = true;
            return "Connection lost... Attempting to connect to the server !";
        }
        return lineFromServer.toString();
    }

    void connectToServer() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
            out = new PrintWriter(socket.getOutputStream(), true);

            Main.first = false;
            isClose = false;
            if (st == null)
                st = keepAlive();
            System.out.println(END);
            new Readline(this).read();
        } catch (Exception e) {
            if (Main.first)
            {
                System.out.println("Impossible to reach the server ...");
                System.exit(0);
            }
        }
    }

    private ScheduledFuture<?> keepAlive() {
        return Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            if (socket == null || isClose) {
                System.out.println("Trying to reconnecting to the host ! (type ctrl-c to exit)");
                connectToServer();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    Socket getSocket() {
        return socket;
    }

    boolean socketAlive() {
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        Socket socket = new Socket();
        int timeout = 100;
        try {
            socket.connect(socketAddress, timeout);
            socket.close();
        } catch (Exception exception) { return false ; }
        return true;
    }
}