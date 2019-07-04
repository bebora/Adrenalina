package it.polimi.se2019.view.cli;

import it.polimi.se2019.view.ViewTile;

/**
 * Handles the drawing of a single tile, and its related info.
 */
class AsciiTile {
    private static final String UP = "\u250c\u2500\u2500\u2500\u2500\u2500\u2500\u2510";
    private static final String MIDDLE = "\u2502      \u2502";
    private static final String LOW = "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2518";
    private static final String MIDDLE_SPAWN = "\u2502᪣᪣    \u2502";
    static final int X_SIZE = 8;
    static final int Y_SIZE = 5;
    static final String unicodeAmmo = "☐";
    static final String unicodePowerUp = "\uD83C\uDCA0";

    /**
     * Draw the tile in the terminal
     * @param tile to draw
     * @param offsetX to avoid overwriting in horizontal
     * @param offsetY to avoid overwriting in vertical
     */
    static void drawTile(ViewTile tile, int offsetX, int offsetY){
        int x = tile.getCoords().getPosx()*X_SIZE + offsetX;
        int y = tile.getCoords().getPosy()* Y_SIZE + offsetY;
        String color = tile.getRoom();
        CLI.moveCursor(x,y);
        CLI.printInColor(color,UP);
        CLI.moveCursor(x,++y);
        for(int i = 0; i<3; i++) {
            CLI.saveCursorPosition();
            CLI.printInColor(color, MIDDLE);
            CLI.moveCursor(x,++y);
        }
        CLI.printInColor(color, LOW);
        if(tile.isSpawn()){
            CLI.restoreCursorPosition();
            CLI.printInColor(tile.getRoom(),MIDDLE_SPAWN);
        }
    }

    /**
     * Display the info of the tile, and what it contains, such as:
     * <li>Weapons</li>
     * <li>AmmoCard</li>
     * @param tile to display info of
     * @param offsetX to avoid overwriting in horizontal
     * @param offsetY to avoid overwriting in vertical
     */
    static void drawTileInfo(ViewTile tile, int offsetX, int offsetY){
        int x = AsciiBoard.boardRightBorder;
        int y = offsetY;
        int i = 1;
        CLI.moveCursor(x,y);
        CLI.clearUntilEndOfLine(y,y+8,x);
        CLI.printInColor("w","Weapons:");
        y++;
        CLI.moveCursor(x,y);
        for(String w: tile.getWeapons()){
            CLI.printInColor("w",i + ")" + w);
            y++;
            i++;
            CLI.moveCursor(x,y);
        }
        CLI.printInColor("w","Ammos:");
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
