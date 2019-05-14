package it.polimi.se2019.view.cli;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.view.ClientView;

public class CLI extends ClientView {
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

}
