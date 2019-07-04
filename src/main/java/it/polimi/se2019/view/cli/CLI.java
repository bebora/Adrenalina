package it.polimi.se2019.view.cli;

import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.view.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command Line Interface to display the View on the terminal
 */
public class CLI extends View {
    static final String ANSI_RESET = "\u001B[0m";
    static final char escCode = 0x1B;
    int skullBoardHeight = 5;
    static List<ViewWeapon> displayedWeapons;
    private ViewPlayer displayedPlayer;
    private boolean clean = false;


    /**
     * Sets if the CLI needs to print the game board.
     * @param clean
     */
    public void setClean(boolean clean) {
        this.clean = clean;
    }

    /**
     * Print the text using the specified color
     * @param color to print the text to
     * @param text to print
     */
    static void printInColor(String color, String text){
        Color trueColor = Color.initialToColor(color.charAt(0));
        System.out.print(Color.getANSIColor(trueColor) + text + ANSI_RESET);
    }

    /**
     * Move the cursor to a particular position
     * @param posX coordinate of the cursor
     * @param posY coordinate of the cursor
     */
    static void moveCursor(int posX,int posY){
        System.out.print(String.format("%c[%d;%df",escCode,posY,posX));
    }

    /**
     * Shift the cursor down
     * @param rows to shift the cursor down
     */
    static void shiftCursorDown(int rows){System.out.print(String.format("%c[%dB",escCode,rows));}

    /**
     * Shift the cursor right
     * @param columns to shift the cursor right
     */
    static void shiftCursorRight(int columns){System.out.print(String.format("%c[%dC",escCode,columns));}

    /**
     * Save the cursor position using escape code
     */
    static void saveCursorPosition(){System.out.print(escCode + "[s");}

    /**
     * Restore cursor position, after saving it
     */
    static void restoreCursorPosition(){System.out.print(escCode + "[u");}

    /**
     * Clean the current row with escape code
     */
    static void cleanRow(){System.out.print(escCode + "[2K");}

    /**
     * Print a text with a fixed width
     * @param width max width of the text
     * @param text to print
     */
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

    /**
     * Clear the command line until the end of the line
     * @param rowBegin to start cleaning
     * @param rowEnd to finish cleaning
     * @param column to clean
     */
    static void clearUntilEndOfLine(int rowBegin, int rowEnd, int column){
        for(int i = rowBegin; i < rowEnd; i++){
            moveCursor(column,i);
            System.out.print(CLI.escCode + "[K");
            moveCursor(column,rowBegin);
        }
    }

    /**
     * Print a message in color
     * @param message to print
     * @param color to use to print the message
     */
    static void printMessage(String message,String color){
        moveCursor(1, AsciiBoard.boardBottomBorder + 7);
        cleanRow();
        printInColor(color, message);
    }

    /**
     * Get the weapons that are currently being displayed
     * @return list of weapons displayed
     */
    public List<ViewWeapon> getDisplayedWeapons() {
        return displayedWeapons;
    }

    /**
     * Display the messages, taking them from the {@link View}
     */
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

    /**
     * Display the skulls remaining, and if necessary print the killshot track
     */
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

    /**
     * Display the selectable options, allowing the client to choose between from different events to send.
     * Supports the displaying of PowerUp in their color and tiles as coordinates
     * Prints the other options using string methods.
     */
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

    /**
     * Display the powerUps that can be selected, in their respective color
     * @param selectablePowerUps indicates what powerups can be chosen and in what quantity
     */
    private void displaySelectablePowerUps(SelectableOptions<ViewPowerUp> selectablePowerUps){
        int index = 0;
        for(ViewPowerUp v: selectablePowerUps.getOptions()){
            index++;
            printInColor(v.getDiscardAward(),index + ")" + v.getName() + " ");
        }
    }

    /**
     * Display the tiles that can be selected, as coordinates
     * @param selectableTileCoords indicates what tiles can be chosen and in what quantity
     */
    private void displaySelectableCoords(SelectableOptions<ViewTileCoords> selectableTileCoords){
        for(ViewTileCoords v: selectableTileCoords.getOptions()){
            printInColor("w",v.toString() + " ");
        }
    }

    /**
     * Prints the options as strings in the terminal
     * @param selectableOptions to print as strings
     */
    private void displayStringSelectable(SelectableOptions selectableOptions){
        int index = 0;
        for(Object o : selectableOptions.getOptions()){
            index++;
            printInColor("w", index + ")" + o + " ");
        }
    }

    /**
     * Display the info of the current turn:
     * <li>Current player and respective color</li>
     * <li>Client's username and respective color</li>
     * <li>Client's points</li>
     */
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

    /**
     * Set the displayed player, to display infos about other players
     * @param displayedPlayer player that need to have his info displayed
     */
    public void setDisplayedPlayer(ViewPlayer displayedPlayer){
        this.displayedPlayer = displayedPlayer;
    }


    /**
     * Refresh the current gameBoard, if clean is false.
     * Print on the CLI:
     * <li>the board itself</li>
     * <li>the players</li>
     * <li>the displayed player's powerups, weapon and ammos</li>
     * <li>the possible options to select</li>
     */
    @Override
    public synchronized void  refresh(){
        if (clean) return;
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

    /**
     * Print the message inside blocks, using a specified color
     * @param color to print the message in
     * @param text to print
     * @param blocks number of blocks to surround the message
     */
    public void printInBlocks(String color, String text, int blocks) {
        int firstBlocks = (blocks - text.length()) / 2;
        printInColor("w", String.join("", Collections.nCopies(firstBlocks, "\uD83E\uDC1A")));
        printInColor(color, text);
        printInColor("w", String.join("", Collections.nCopies(blocks- firstBlocks - text.length(), "\uD83E\uDC18")) + "\n");

    }

    /**
     * Print a message to indicates to the player his status of disconnected from the game.
     * Set the status to END to notify the handler and communicate the possibility of a reconnection
     */
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

    /**
     * Print the winners once the game ended.
     * @param winners list of winners to print
     */
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
