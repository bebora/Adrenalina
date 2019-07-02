package it.polimi.se2019.view.cli;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;

import java.util.Arrays;
import java.util.HashSet;

public class BotMain {
    public static void main(String[] args){
        Logger.setPrioritiesLoggingToStdout(new HashSet<>(Arrays.asList(Priority.values())));
        Thread start = new Thread(new BotCliHandler());
        start.start();
    }
}
