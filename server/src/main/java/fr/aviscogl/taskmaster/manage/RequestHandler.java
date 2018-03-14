package fr.aviscogl.taskmaster.manage;

import fr.aviscogl.taskmaster.command.CommandHandler;
import fr.aviscogl.taskmaster.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;

public class RequestHandler extends Thread {

        private final static String END_RESPONSE = "{END}";

        private Socket socket;
        private int clientNumber;

        public RequestHandler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            Logger.log("New connection with client #" + clientNumber + " at " + socket);
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Hello, you are client #" + clientNumber + ".");

                while (true) {
                    String input = in.readLine();
                    Logger.log("Client #" + clientNumber + " send: " + input);
                    if (input != null)
                        CommandHandler.execute(input, out);
                    else
                        break;
                }
            } catch (IOException e) {
                Logger.logErr("Error handling client # " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    Logger.log(Level.WARNING, "Couldn't close a socket.. WTF ??");
                }
                Logger.log("Connection with client # " + clientNumber + " closed");
            }
        }

        private void sendPacket(PrintWriter out, String packet) {
            out.println(packet);
            out.println(END_RESPONSE);
        }
    }