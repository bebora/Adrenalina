package it.polimi.se2019.view.cli;

import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewPlayer;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewWeapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the drawing of the information about the displayed player
 */
public class AsciiPlayer {
    private AsciiPlayer() {}
    static final int playerInfoHeight = 8;

    /**
     * Print the marks that the player received, in the color of the player that marked him
     * @param player whose marks need to be printed
     */
    static void printMarks(ViewPlayer player){
        CLI.moveCursor(AsciiBoard.offsetX,AsciiBoard.boardBottomBorder+2);
        CLI.printInColor("w","Marks:\n");
        for(String m: player.getMarks()){
            CLI.printInColor(m,"\uD83E\uDE78 ");
        }
        CLI.printInColor("red","\n");
    }

    /**
     * Print the damages that the player received, in the color of the player that damaged him
     * @param player whose damages need to be printed
     */
    static void printDamages(ViewPlayer player){
        CLI.printInColor("w","Damages:\n");
        for(String d: player.getDamages())
            CLI.printInColor(d,"\uD83E\uDE78 ");
        CLI.printInColor("red","\n");
    }

    /**
     * Print the current ammos that the displayed player possess
     * @param player whose ammos need to be printed
     */
    static void printAmmos(ViewPlayer player){
        CLI.moveCursor(12*2, AsciiBoard.boardBottomBorder + 2);
        CLI.printInColor("w","Ammos:");
        CLI.moveCursor(12*2, AsciiBoard.boardBottomBorder + 3);
        CLI.saveCursorPosition();
        for(String a: player.getAmmos())
            CLI.printInColor(a,	"\u2610 ");
    }

    /**
     * Print the weapons that the player possess, differentiating between unloaded(visible to everyone) and loaded(hidden) weapons
     * @param unloadedWeapons weapons that need to be reloaded before use
     * @param loadedWeapons weapons that can be used
     */
    static void printWeapons(List<ViewWeapon> unloadedWeapons, List<ViewWeapon> loadedWeapons){
        int i = 1;
        CLI.moveCursor(48,AsciiBoard.boardBottomBorder + 4);
        CLI.saveCursorPosition();
        CLI.printInColor("w","Weapons:");
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(1);
        for(ViewWeapon w: unloadedWeapons){
            CLI.printInColor("g",i +")"+ w.getName() + " ");
            i++;
        }
        for(ViewWeapon w: loadedWeapons) {
            CLI.printInColor("r", i + ")" + w.getName() + " ");
            i++;
        }
        List<ViewWeapon> allWeapons = new ArrayList<>(unloadedWeapons);
        allWeapons.addAll(loadedWeapons);
        CLI.displayedWeapons = allWeapons;
    }

    /**
     * Print the current powerups(hidden from other players) that the displayed player possess
     * @param view relative to the player whose powerups needs to be printed
     */
    static void printPowerUps(View view){
        CLI.moveCursor(48,AsciiBoard.boardBottomBorder + 2);
        CLI.saveCursorPosition();
        CLI.printInColor("w","PowerUps:");
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(1);
        for(ViewPowerUp p: view.getPowerUps()){
            CLI.printInColor(p.getDiscardAward(),p.getName() + " ");
        }
    }

    /**
     * Print the current reward points that the displayed player possess
     * @param viewPlayer whose reward points need to be printed
     */
    static void printRewardPoint(ViewPlayer viewPlayer){
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(1);
        CLI.printInColor("w","Reward points:");
        CLI.restoreCursorPosition();
        CLI.shiftCursorDown(2);
        for(Integer i: viewPlayer.getRewardPoints()){
            CLI.printInColor("w", i + " ");
        }
    }

    /**
     * Print the current powerups(hidden from other players) that the displayed player possess
     * @param player whose info needs to be displayed
     * @param loadedWeapons to display (present only if the displayed player is the same as the client's player)
     * @param unloadedWeapons to display
     */
    static void drawPlayerInfo(ViewPlayer player,List<ViewWeapon> loadedWeapons, List<ViewWeapon> unloadedWeapons){
        CLI.moveCursor(AsciiBoard.offsetX,AsciiBoard.boardBottomBorder+1);
        CLI.clearUntilEndOfLine(AsciiBoard.boardBottomBorder + 1,AsciiBoard.boardBottomBorder + 6, AsciiBoard.offsetX);
        printMarks(player);
        printDamages(player);
        printAmmos(player);
        printRewardPoint(player);
        printWeapons(unloadedWeapons, loadedWeapons);
    }

}
