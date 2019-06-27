package it.polimi.se2019.view.cli;

public class BotMain {
    public static void main(String[] args){
        Thread start = new Thread(new BotCliHandler());
        start.start();
    }
}
