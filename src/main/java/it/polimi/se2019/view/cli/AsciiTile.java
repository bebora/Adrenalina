package it.polimi.se2019.view.cli;

import it.polimi.se2019.view.ViewTile;

class AsciiTile {
    private static final String UP = "\u250c\u2500\u2500\u2500\u2510";
    private static final String MIDDLE = "\u2502   \u2502";
    private static final String LOW = "\u2514\u2500\u2500\u2500\u2518";
    static final int X_SIZE = 5;
    static final int Y_SIZE = 5;
    static final String unicodeAmmo = "☐";
    static final String unicodePowerUp = "\uD83C\uDCA0";

    static void drawTile(ViewTile tile, int offsetX, int offsetY){
        int x = tile.getCoords().getPosx()*X_SIZE + offsetX;
        int y = tile.getCoords().getPosy()* Y_SIZE + offsetY;
        String color = tile.getRoom();
        CLI.moveCursor(x,y);
        CLI.printInColor(color,UP);
        y++;
        CLI.moveCursor(x,y);
        for(int i = 0; i<3; i++) {
            CLI.printInColor(color, MIDDLE);
            y++;
            CLI.moveCursor(x,y);
        }
        CLI.printInColor(color, LOW);
    }

    static void drawTileInfo(ViewTile tile, int offsetX, int offsetY){
        int x = AsciiBoard.boardRightBorder;
        int y = offsetY;
        int i = 1;
        CLI.moveCursor(x,y);
        CLI.clearUntilEndOfLine(y,y+8,x);
        CLI.printInColor("w","Armi:");
        y++;
        CLI.moveCursor(x,y);
        for(String w: tile.getWeapons()){
            CLI.printInColor("w",i + ")" + w);
            y++;
            i++;
            CLI.moveCursor(x,y);
        }
        CLI.printInColor("w","Munizioni:");
        y++;
        CLI.moveCursor(x,y);
        for(String p: tile.getAmmos()){
            if(p.equals("POWERUP")){
                CLI.printInColor("w", unicodePowerUp + " ");
            }else{
                CLI.printInColor(p,"☐ ");
            }
        }
    }
}
