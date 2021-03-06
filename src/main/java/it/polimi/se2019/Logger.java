package it.polimi.se2019;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to log errors and debug info.
 * Supports writing log info to a file of choice.
 */
public final class Logger {


    private Logger() {
        throw new IllegalStateException("Utility class");
    }

    private static final String HOME = System.getProperty("user.home");

    /**
     * Defines the format for the file used to log in.
     */
    private static String LOG = String.format("%s/adrenalina.log", HOME);
    private static boolean logToFile;
    private static Set<Priority> prioritiesToPrint = new HashSet<>();
    private static BufferedWriter bw;

    /**
     * Logs the {@code toLog} string with the priority defined in {@code priority}
     * If {@link #logToFile} is true, the log gets written to the corresponding file
     * @param priority type of the message
     * @param toLog message to be logged
     */
    public static void log(Priority priority, String toLog) {
        DateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss:SSS]");
        Date now = new Date();
        String logWithTime = dateFormat.format(now);
        logWithTime += String.format(" %s: %s", priority.name(), toLog);
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
        if (prioritiesToPrint.contains(priority))
            System.out.println(logWithTime);
    }

    /**
     * Set the suffix to append to the {@link #LOG}, to define the name of the file used to log info.
     * @param suffix
     */
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

    /**
     * Sets which {@link Priority} should be printed to stdout
     * @param enabled Set of priorities that must be printed also on stdout
     */
    public static void setPrioritiesLoggingToStdout(Set<Priority> enabled) {
        prioritiesToPrint = enabled;
    }
}
