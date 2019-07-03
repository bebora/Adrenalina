package it.polimi.se2019.view.gui;

import it.polimi.se2019.GameProperties;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.cli.CliInputHandler;

import java.util.Arrays;
import java.util.HashSet;

public class ClientMain {
    public static void main(String[] args){
        if (args.length > 0) {
            GameProperties.setDefaultPath(args[0]);
        }
        Logger.setPrioritiesLoggingToStdout(new HashSet<>(Arrays.asList(Priority.ERROR, Priority.INFO, Priority.WARNING)));
        Thread start = new Thread(new CliInputHandler(args));
        start.start();
    }
}
