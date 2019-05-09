package it.polimi.se2019;

public final class Logger {


    private Logger() {
        throw new IllegalStateException("Utility class");
    }

    private static final String HOME = System.getProperty("user.home");
    private static final String CLIENT = "/adrenalina_client.log";
    private static final String SERVER = "/adrenalina_server.log";

    private static Side side;
    private static boolean debugMode;

    public static void setSide(Side side) {
        Logger.side = side;
    }

    public static void log(Side side, Priority priority, String toLog) {
        if (Logger.side == side) {
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
        }
    }
}