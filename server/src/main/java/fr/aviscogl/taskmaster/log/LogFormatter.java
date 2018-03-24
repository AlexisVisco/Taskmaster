package fr.aviscogl.taskmaster.log;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class LogFormatter extends Formatter {

    @Override
        public String format(LogRecord record) {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
            String date = dt.format(record.getMillis());
            return String.format("(%s) (%s) %s\n", date, record.getLevel(), record.getMessage());
        }
    }