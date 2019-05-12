package it.polimi.se2019.view.cli;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.ViewBoard;
import it.polimi.se2019.view.ViewTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CLI extends View {
    static final String ANSI_RESET = "\u001B[0m";
    static final char escCode = 0x1B;

    static void printInColor(String color, String text){
        Color trueColor = Color.initialToColor(color.charAt(0));
        System.out.print(Color.getANSIColor(trueColor) + text + ANSI_RESET);
    }

    static void moveCursor(int posX,int posY){
        System.out.print(String.format("%c[%d;%df",escCode,posY,posX));
    }
    static void shiftCursorDown(int rows){System.out.print(String.format("%c[%dB",escCode,rows));}
    static void shiftCursorRight(int columns){System.out.print(String.format("&c[%dC",escCode,columns));}




    public static void main(String[] args){
        List<Player> testPlayers = new ArrayList<>(Arrays.asList(new Player("buono"),new Player("cattivo")));
        Match testMatch = new NormalMatch(testPlayers,"board1.btlb",5);
        ViewBoard testBoard = new ViewBoard(testMatch.getBoard());
        AsciiBoard.setBoard(testBoard);
        AsciiBoard.drawBoard();
        moveCursor(0, AsciiTile.Y_SIZE * 3 + 1);
    }


}
