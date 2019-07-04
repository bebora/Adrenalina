package it.polimi.se2019;

public class Utils {
    /**
     * Make the current thread sleep for {@code time}.
     * Used in context when the interruption during the sleep is forbidden, by code logic.
     * @param time time to sleep
     */
    public static void sleepABit(int time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e) {
            Logger.log(Priority.WARNING, "Unexpected interruption occurred");
            Thread.currentThread().interrupt();
        }
    }
}
