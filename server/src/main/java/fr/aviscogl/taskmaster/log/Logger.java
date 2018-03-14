package fr.aviscogl.taskmaster.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Logger {
    public static void log(Level level, String message, Object... o) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
        String date = dt.format(new Date());
        System.out.println(String.format("(%s) [%s] %s", date, level.getName(), String.format(message, o)));
    }

    public static void log(Level level, String message) {
        log(level, message, (Object)null);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(String message, Object... o) {
        log(Level.INFO, message, o);
    }

    public static void logErr(String message) {
        log(Level.SEVERE, message, (Object)null);
    }

    public static void logErr(String message, Object... o) {
        log(Level.SEVERE, message, o);
    }
}
