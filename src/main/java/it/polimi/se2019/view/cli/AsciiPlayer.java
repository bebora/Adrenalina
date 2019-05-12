package it.polimi.se2019.view.cli;

import it.polimi.se2019.view.ViewPlayer;

public class AsciiPlayer {
    static ViewPlayer player;

    static void printMarks(){
        CLI.printInColor("w","Marchi:\n");
        for(String m: player.getMarks()){
            CLI.printInColor(m,"\uD83E\uDE78 ");
        }
        CLI.printInColor("red","\n");
    }

    static void printDamages(){
        CLI.printInColor("w","Danni:\n");
        for(String d: player.getDamages())
            CLI.printInColor(d,"\uD83E\uDE78 ");
        CLI.printInColor("red","\n");
    }

    static void printAmmos(){
        CLI.moveCursor(12*2, AsciiTile.Y_SIZE * AsciiBoard.board.getTiles().size() + 1);
        CLI.printInColor("w","Munizioni:");
        CLI.moveCursor(12*2, AsciiTile.Y_SIZE * AsciiBoard.board.getTiles().size() + 2);
        CLI.saveCursorPosition();
        for(String a: player.getAmmos())
            CLI.printInColor(a,	"\u2610 ");
    }

    static void printWeapons(){
        int i = 1;
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(1);
        CLI.printInColor("w","Armi:");
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(2);
        for(String w: player.getUnloadedWeapons()){
            CLI.printInColor("w",i +")"+ w + " ");
        }
    }

    static void drawPlayerInfo(){
        printMarks();
        printDamages();
        printAmmos();
        printWeapons();
    }

}
