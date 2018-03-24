package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.Server;
import fr.aviscogl.taskmaster.command.CommandHandler;
import fr.aviscogl.taskmaster.log.Logger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

public class RequestHandler extends Thread {

        private Socket socket;
        private int clientNumber;

        public RequestHandler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            Server.global.log("New connection with client #%d.", clientNumber);
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Hello, you are client #" + clientNumber + ".");

                while (true) {
                    String input = in.readLine();
                    Server.global.log("Client #%d send: %s.", clientNumber, input);
                    if (input != null)
                        CommandHandler.execute(input, out);
                    else
                        break;
                }
            } catch (IOException e) {
                Server.global.logErr("Error handling client #%d : %s" + clientNumber, e.getCause());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    Server.global.log(Level.WARNING, "Couldn't close a socket.. WTF ??");
                }
                Server.global.log("Connection with client # " + clientNumber + " closed");
            }
        }

        private void sendPacket(PrintWriter out, String packet) {
            out.println(packet);
            out.println(Server.END);
        }
    }