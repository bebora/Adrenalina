package it.polimi.se2019.view.cli;
import it.polimi.se2019.view.ViewBoard;
import it.polimi.se2019.view.ViewPlayer;
import it.polimi.se2019.view.ViewTile;

import java.util.List;
import java.util.stream.Collectors;

public class AsciiBoard {
    static ViewBoard board;
    static int offsetX = 1;
    static int offsetY = 1;
    static int boardRightBorder;
    static int boardBottomBorder;
    static int infoBoxHeight = 8;

    static void drawBoard(List<ViewPlayer> players) {
        System.out.print(CLI.escCode + "[J");
        boardBottomBorder = AsciiTile.Y_SIZE * AsciiBoard.board.getTiles().size() + 1;
        boardRightBorder = AsciiBoard.board.getTiles().get(0).size()*AsciiTile.X_SIZE + offsetX + 10;
        CLI.clearUntilEndOfLine(offsetY,offsetY + boardBottomBorder,offsetX);
        for (int i = 0; i < board.getTiles().size(); i++) {
            for (ViewTile tile : board.getTiles().get(i)) {
                if (tile != null) {
                    AsciiTile.drawTile(tile, offsetX, offsetY);
                    drawPlayers(players,tile);
                }
            }
        }
        drawLinks();
    }

    static ViewTile findTile(int posX, int posY) {
        return board.getTiles().get((posY - 1) / 5).get((posX - 1) / 8);
    }

    static void setBoard(ViewBoard board) {
        AsciiBoard.board = board;
    }

    static void drawLeftDoor(ViewTile tile) {
        int x = tile.getCoords().getPosx() * AsciiTile.X_SIZE + offsetX;
        int y = tile.getCoords().getPosy() * AsciiTile.Y_SIZE + offsetY + 1;
        String color = tile.getRoom();
        CLI.moveCursor(x - 1, y);
        CLI.printInColor(findTile(x - 1, y).getRoom(), "\u2514");
        CLI.printInColor(color, "\u2518");
        CLI.moveCursor(x - 1, y + 1);
        CLI.printInColor(color, " ");
        CLI.printInColor(color, " ");
        CLI.moveCursor(x - 1, y + 2);
        CLI.printInColor(findTile(x - 1, y + 2).getRoom(), "\u250c");
        CLI.printInColor(color, "\u2510");
    }

    static void drawBottomDoor(ViewTile tile) {
        int x = tile.getCoords().getPosx() * AsciiTile.X_SIZE + offsetX + 2;
        int y = tile.getCoords().getPosy() * AsciiTile.Y_SIZE + AsciiTile.Y_SIZE;
        String color = tile.getRoom();
        CLI.moveCursor(x, y);
        CLI.printInColor(color, "\u2510  \u250c");
        CLI.moveCursor(x, y + 1);
        CLI.printInColor(findTile(x, y + 1).getRoom(), "\u2518  \u2514");
    }

    static void drawPlayers(List<ViewPlayer> players, ViewTile tile){
        List<String> inTile = players.stream()
                .filter(p -> p.getTile().equals(tile))
                .map(ViewPlayer::getColor)
                .collect(Collectors.toList());
        int x = tile.getCoords().getPosx() * AsciiTile.X_SIZE + offsetX + 1;
        int y = tile.getCoords().getPosy() * AsciiTile.Y_SIZE  + offsetY + 1;
        int i = 0;
        CLI.moveCursor(x,y);
        for(String color: inTile){
            CLI.printInColor(color,"\uD83D\uDEB9 ");
            i += 2;
            if(i>3)
                CLI.moveCursor(x,++y);
        }
    }

    static void drawLinks() {
        //check top row
        ViewTile tile1;
        ViewTile tile2;
        for (int i = 0; i < board.getTiles().size() - 1; i++) {
            tile1 = board.getTiles().get(i).get(0);
            tile2 = board.getTiles().get(i + 1).get(0);
            if (tile1 != null && tile2 != null && board.isLinked(tile1, tile2, false)) {
                drawBottomDoor(tile1);
            }
        }
        //check last row
        for (int i = 1; i < board.getTiles().get(0).size(); i++) {
            tile1 = board.getTiles().get(board.getTiles().size()-1).get(i);
            tile2 = board.getTiles().get(board.getTiles().size()-1).get(i-1);
            if (tile1 != null && tile2 != null && board.isLinked(tile1, tile2, false)) {
                drawLeftDoor(tile1);
            }
        }
        for (int i = 1; i < board.getTiles().get(0).size(); i++) {
            for (int j = 0; j < board.getTiles().size() - 1; j++) {
                tile1 = board.getTiles().get(j).get(i);
                tile2 = board.getTiles().get(j + 1).get(i);
                if (tile1 != null && tile2 != null && board.isLinked(tile1, tile2, false)) {
                    drawBottomDoor(tile1);
                }
                tile2 = board.getTiles().get(j).get(i-1);
                if (tile1 != null && tile2 != null && board.isLinked(tile1, tile2, false)) {
                    drawLeftDoor(tile1);
                }
            }
        }
        CLI.moveCursor(0, boardBottomBorder + 1);
    }

    static void requestTileInfo(int requestedX, int requestedY){
        ViewTile requestedTile = null;
        if (requestedY <= AsciiBoard.board.getTiles().size() && requestedY > 0 && requestedX <= AsciiBoard.board.getTiles().get(requestedY-1).size() && requestedX > 0) {
            requestedTile = AsciiBoard.board.getTiles().get(requestedY - 1).get(requestedX - 1);
            if(requestedTile != null )
                AsciiTile.drawTileInfo(requestedTile, 1, 1);
        }
        if(requestedTile == null){
            CLI.printMessage("The selected tile does not exist", " R");
        }
    }
}
