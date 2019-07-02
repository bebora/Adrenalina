package it.polimi.se2019.view.cli;

import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.view.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CLI extends View {
    static final String ANSI_RESET = "\u001B[0m";
    static final char escCode = 0x1B;
    int skullBoardHeight = 5;
    static List<ViewWeapon> displayedWeapons;
    private ViewPlayer displayedPlayer;



    static void printInColor(String color, String text){
        Color trueColor = Color.initialToColor(color.charAt(0));
        System.out.print(Color.getANSIColor(trueColor) + text + ANSI_RESET);
    }

    static void moveCursor(int posX,int posY){
        System.out.print(String.format("%c[%d;%df",escCode,posY,posX));
    }

    static void shiftCursorDown(int rows){System.out.print(String.format("%c[%dB",escCode,rows));}

    static void shiftCursorRight(int columns){System.out.print(String.format("%c[%dC",escCode,columns));}

    static void saveCursorPosition(){System.out.print(escCode + "[s");}

    static void restoreCursorPosition(){System.out.print(escCode + "[u");}

    static void cleanRow(){System.out.print(escCode + "[2K");}

    static void fixedWidthPrint(int width, String text){
        int relativePos = 0;
        saveCursorPosition();
        for(String w: text.split("\\s")){
            System.out.print(w + " ");
            relativePos += w.length();
            if(relativePos > width){
                relativePos = 0;
                restoreCursorPosition();
                shiftCursorDown(1);
                saveCursorPosition();
            }
        }
    }

    static void clearUntilEndOfLine(int rowBegin, int rowEnd, int column){
        for(int i = rowBegin; i < rowEnd; i++){
            moveCursor(column,i);
            System.out.print(CLI.escCode + "[K");
            moveCursor(column,rowBegin);
        }
    }

    static void printMessage(String message,String color){
        moveCursor(1, AsciiBoard.boardBottomBorder + 7);
        cleanRow();
        printInColor(color, message);
    }

    public List<ViewWeapon> getDisplayedWeapons() {
        return displayedWeapons;
    }

    private void displayMessages() {
        int x = AsciiBoard.boardRightBorder + AsciiBoard.infoBoxWidth;
        int y = AsciiBoard.offsetY + skullBoardHeight;
        moveCursor(x,y);
        printInColor("w","MESSAGES:");
        moveCursor(x,++y);
        clearUntilEndOfLine(y,y+40,x);
        for (String m : getMessages()) {
            fixedWidthPrint(40,m);
            restoreCursorPosition();
            shiftCursorDown(1);
        }
    }

    private void displaySkullBoard(){
        int x = AsciiBoard.boardRightBorder  + AsciiBoard.infoBoxWidth;
        int y = AsciiBoard.offsetY;
        ArrayList<String> killshotTrack = getBoard().getKillShotTrack();
        moveCursor(x,y);
        clearUntilEndOfLine(y,y+3,x);
        printInColor("w","Skulls:");
        moveCursor(x,++y);
        for(int i = 0; i < getBoard().getSkulls(); i++)
            printInColor("w","\uD83D\uDC80 ");
        moveCursor(x,y);
        for(int i = 0; i < killshotTrack.size(); i+=2)
            printInColor(killshotTrack.get(i),"\uD83E\uDE78 ");
        moveCursor(x,++y);
        for(int i = 1; i < killshotTrack.size(); i+=2)
            if(killshotTrack.get(i) != null)
                printInColor(killshotTrack.get(i),"\uD83E\uDE78 ");
    }


    private void displaySelectableOptions(){
        int x = AsciiBoard.offsetX;
        int y = AsciiBoard.boardBottomBorder +AsciiPlayer.playerInfoHeight;
        moveCursor(x,y);
        clearUntilEndOfLine(y,y+16,x);
        for(ReceivingType r: getSelectableOptionsWrapper().getAcceptedTypes()){
            moveCursor(x,++y);
            printInColor("w",r.name() + ":");
           switch (r){
               case POWERUP:
                   displaySelectablePowerUps(getSelectableOptionsWrapper().getSelectablePowerUps());
                   moveCursor(x,++y);
                   printInColor("w",getSelectableOptionsWrapper().getSelectablePowerUps().getPrompt());
                   moveCursor(x,++y);
                   printInColor("w",getSelectableOptionsWrapper().getSelectablePowerUps().getNumericalCostraints());
                   break;
               case TILES:
                   displaySelectableCoords(getSelectableOptionsWrapper().getSelectableTileCoords());
                   moveCursor(x,++y);
                   printInColor("w",getSelectableOptionsWrapper().getSelectableTileCoords().getPrompt());
                   moveCursor(x,++y);
                   printInColor("w",getSelectableOptionsWrapper().getSelectableTileCoords().getNumericalCostraints());
                   break;
               case STOP:
                   printInColor("w", getSelectableOptionsWrapper().getStopPrompt());
                   moveCursor(x,++y);
                   break;
               default:
                   displayStringSelectable(getSelectableOptionsWrapper().getSelectableOptions(r));
                   moveCursor(x,++y);
                   printInColor("w",getSelectableOptionsWrapper().getSelectableOptions(r).getPrompt());
                   moveCursor(x,++y);
                   printInColor("w",getSelectableOptionsWrapper().getSelectableOptions(r).getNumericalCostraints());

           }
       }
    }

    private void displaySelectablePowerUps(SelectableOptions<ViewPowerUp> selectablePowerUps){
        int index = 0;
        for(ViewPowerUp v: selectablePowerUps.getOptions()){
            index++;
            printInColor(v.getDiscardAward(),index + ")" + v.getName() + " ");
        }
    }

    private void displaySelectableCoords(SelectableOptions<ViewTileCoords> selectableTileCoords){
        for(ViewTileCoords v: selectableTileCoords.getOptions()){
            printInColor("w",v.toString() + " ");
        }
    }

    private void displayStringSelectable(SelectableOptions selectableOptions){
        int index = 0;
        for(Object o : selectableOptions.getOptions()){
            index++;
            printInColor("w", index + ")" + o + " ");
        }
    }

    void displayTurnInfo(){
        moveCursor(AsciiBoard.offsetX,AsciiBoard.boardBottomBorder+1);
        printInColor("w","You:");
        printInColor(getSelf().getColor(),getUsername() + "  ");
        printInColor("w","Current player:");
        printInColor(getCurrentPlayer().getColor(),getCurrentPlayer().getUsername() + "  ");
        printInColor("w","Displayed player:");
        printInColor(displayedPlayer.getColor(),displayedPlayer.getUsername() + " ");
        printInColor("w", "Points:");
        printInColor("w", String.format("%d ",getPoints()));
    }

    public void setDisplayedPlayer(ViewPlayer displayedPlayer){
        this.displayedPlayer = displayedPlayer;
    }


    @Override
    public synchronized void  refresh(){
        if (getPlayers() != null && !getPlayers().isEmpty() && !getStatus().equals(Status.END)) {
            displayedPlayer = getSelf();
            AsciiBoard.board = this.getBoard();
            AsciiBoard.drawBoard(getPlayers().stream().filter(p -> !p.getDominationSpawn()).collect(Collectors.toList()));
            AsciiPlayer.drawPlayerInfo(getSelf(), getLoadedWeapons(), getSelf().getUnloadedWeapons());
            AsciiPlayer.printPowerUps(this);
            displaySkullBoard();
            displayMessages();
            displaySelectableOptions();
            displayTurnInfo();
            moveCursor(1, AsciiBoard.boardBottomBorder + 6);
        }
    }

    public void printInBlocks(String color, String text, int blocks) {
        int firstBlocks = (blocks - text.length()) / 2;
        printInColor("w", String.join("", Collections.nCopies(firstBlocks, "\uD83E\uDC1A")));
        printInColor(color, text);
        printInColor("w", String.join("", Collections.nCopies(blocks- firstBlocks - text.length(), "\uD83E\uDC18")) + "\n");

    }

    @Override
    public void disconnect() {
        if (!getStatus().equals(Status.END)) {
            printInBlocks("w", "", 35);
            printInBlocks("r", "WARNING!", 35);
            printInBlocks("w", "YOU ARE DISCONNECTED!", 35);
            printInBlocks("g", "CLICK ENTER TO RECONNECT!", 35);
            printInBlocks("w", "", 35);
            setStatus(Status.END);
        }
    }

    @Override
    public void printWinners(List<String> winners) {
        if (!getStatus().equals(Status.END)) {
            printInBlocks("w", "", 35);
            printInBlocks("r", "THE GAME IS OVER!", 35);
            printInBlocks("r", String.format("FAREWELL %s", getUsername()), 35);
            printInBlocks("w", "! WINNERS ! ARE !", 35);
            for (String winner : winners) {
                if (winner.equals(getUsername()))
                    printInBlocks("r", "※※※YOU!※※※", 35);
                else
                    printInBlocks("r", winner, 35);
            }
            printInBlocks("g", "CLICK ENTER TO PLAY AGAIN!", 35);
            printInBlocks("w", "", 35);
            setStatus(Status.END);
        }
    }
}
