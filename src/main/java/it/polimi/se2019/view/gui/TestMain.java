package it.polimi.se2019.view.gui;

import it.polimi.se2019.view.cli.BotCliHandler;

public class TestMain {
    public static void main(String[] args){
        Thread start = new Thread(new BotCliHandler());
        start.start();
    }
}
