package it.polimi.se2019;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Logger {


    private Logger() {
        throw new IllegalStateException("Utility class");
    }

    private static final String HOME = System.getProperty("user.home");
    private static String LOG = String.format("%s/adrenalina.log", HOME);
    private static boolean logToFile;
    private static boolean debugMode;
    private static BufferedWriter bw;
    public static void log(Priority priority, String toLog) {
        DateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss:SSS]");
        Date now = new Date();
        String logWithTime = dateFormat.format(now);
        switch (priority) {
            case DEBUG:
                logWithTime += " DEBUG: " + toLog;
                break;
            case ERROR:
                logWithTime += " ERROR: " + toLog;
                break;
            case WARNING:
                logWithTime += " WARNING: " + toLog;
                break;
        }
        if (logToFile) {
            try {
                bw.write(logWithTime+"\n");
                bw.flush();
            }
            catch (IOException e) {
                logToFile = false;
                System.out.println("Could not log to file");
            }

        }
        System.out.println(logWithTime);
    }

    public static void setLogFileSuffix(String suffix) {
        if (!logToFile) {
            LOG += suffix;
            System.out.println("Logging to: "+LOG);
            try {
                bw = new BufferedWriter(new FileWriter(LOG, true));
                logToFile = true;
            }
            catch (IOException e) {
                System.out.println("Could not log to file");
            }
        }
    }
}
