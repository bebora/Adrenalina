package it.polimi.se2019;

public final class Logger {


    private Logger() {
        throw new IllegalStateException("Utility class");
    }

    private static final String HOME = System.getProperty("user.home");
    private static final String LOG = String.format("%s/adrenalina.log", HOME);

    private static boolean debugMode;
    //TODO log somewhere (cli/file)
    public static void log(Priority priority, String toLog) {
        switch (priority) {
            case DEBUG:
                toLog = "[*] DEBUG: " + toLog;
                break;
            case ERROR:
                toLog = "[*] ERROR: " + toLog;
                break;
            case WARNING:
                toLog = "[*] WARNING: " + toLog;
                break;
        }
        //TODO LOG TO RELATIVE FILE
        System.out.println(toLog);
    }
}
