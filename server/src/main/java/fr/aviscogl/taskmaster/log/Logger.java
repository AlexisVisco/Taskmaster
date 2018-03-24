package fr.aviscogl.taskmaster.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class Logger {

    private java.util.logging.Logger logger;

    public Logger(String prefix, String where) {
        prefix = prefix.replaceAll(" ", "_");
        where = where.replaceAll(" ", "_");
        logger = java.util.logging.Logger.getLogger(prefix);
        File file = new File(System.getProperty("user.home") + "/.taskmaster/" + where);
        new File(file.getParent()).mkdirs();
        Handler handler = null;
        try {
            handler = new FileHandler(file.getAbsolutePath(), 1000000 * 25, 10, true);
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void log(Level level, String message, Object... o) {
        logger.log(level, String.format("%s", String.format(message, o)));
    }

    public void log(Level level, String message) {
        log(level, message, (Object) null);
    }

    public void log(String message) {
        log(Level.INFO, message);
    }

    public void log(String message, Object... o) {
        log(Level.INFO, message, o);
    }

    public void logErr(String message) {
        log(Level.SEVERE, message, (Object) null);
    }

    public void logErr(String message, Object... o) {
        log(Level.SEVERE, message, o);
    }
}
