package fr.aviscogl.taskmaster;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;

import java.io.IOException;
import java.util.function.Consumer;

class Readline {

    private final static String PROMPT = String.format("%sTaskmaster> %s", Color.BLUE_BOLD, Color.RESET);
    private Client client;

    Readline(Client client) {
        this.client = client;
    }

    private Completer completer() {
        return (buffer, cursor, candidates) -> {
            Consumer<String> fn = (s) ->  {if ((s.startsWith(buffer.toLowerCase()))) candidates.add(s);};

            fn.accept("status");
            fn.accept("start");
            fn.accept("stop");
            fn.accept("restart");
            fn.accept("reload");
            fn.accept("shutdown");

            return 0;
        };
    }

    void read() {
        try {
            ConsoleReader console = new ConsoleReader();
            console.addCompleter(completer());
            console.setPrompt(PROMPT);
            String line;
            while ((line = console.readLine()) != null) {
                if (line.equals("exit"))
                    client.reconnect("Manual exit");
                else
                    System.out.print(client.getResponseFromCommand(line));
            }
        } catch (IOException e) {
            client.reconnect("Readline didn't work well...");
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
