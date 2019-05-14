package it.polimi.se2019.view.cli;

import it.polimi.se2019.view.ViewPlayer;
import it.polimi.se2019.view.ViewWeapon;

import java.util.List;

public class AsciiPlayer {
    static void printMarks(ViewPlayer player){
        CLI.moveCursor(AsciiBoard.offsetX,AsciiBoard.boardBottomBorder+2);
        CLI.printInColor("w","Marchi:\n");
        for(String m: player.getMarks()){
            CLI.printInColor(m,"\uD83E\uDE78 ");
        }
        CLI.printInColor("red","\n");
    }

    static void printDamages(ViewPlayer player){
        CLI.printInColor("w","Danni:\n");
        for(String d: player.getDamages())
            CLI.printInColor(d,"\uD83E\uDE78 ");
        CLI.printInColor("red","\n");
    }

    static void printAmmos(ViewPlayer player){
        CLI.moveCursor(12*2, AsciiBoard.boardBottomBorder + 2);
        CLI.printInColor("w","Munizioni:");
        CLI.moveCursor(12*2, AsciiBoard.boardBottomBorder + 3);
        CLI.saveCursorPosition();
        for(String a: player.getAmmos())
            CLI.printInColor(a,	"\u2610 ");
    }

    static void printWeapons(List<ViewWeapon> weapons){
        int i = 1;
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(1);
        CLI.printInColor("w","Armi:");
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(2);
        for(ViewWeapon w: weapons){
            if(w.getLoaded())
                CLI.printInColor("r",i +")"+ w.getName() + " ");
            else
                CLI.printInColor("g",i +")"+ w.getName() + " ");
            i++;
        }
    }

    static void drawPlayerInfo(ViewPlayer player,List<ViewWeapon> weapons){
        CLI.moveCursor(AsciiBoard.offsetX,AsciiBoard.boardBottomBorder+1);
        CLI.clearUntilEndOfLine(AsciiBoard.boardBottomBorder + 1,AsciiBoard.boardBottomBorder + 6, AsciiBoard.offsetX);
        CLI.printInColor(player.getColor(),player.getColor());
        printMarks(player);
        printDamages(player);
        printAmmos(player);
        printWeapons(weapons);
    }

}
