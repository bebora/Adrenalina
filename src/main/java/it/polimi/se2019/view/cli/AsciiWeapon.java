package it.polimi.se2019.view.cli;

import it.polimi.se2019.view.ViewEffect;
import it.polimi.se2019.view.ViewWeapon;

public class AsciiWeapon {
    public static void drawWeaponInfo(ViewWeapon weapon){
        CLI.clearUntilEndOfLine(AsciiBoard.offsetY,AsciiBoard.offsetY+AsciiBoard.infoBoxHeight,AsciiBoard.boardRightBorder);
        int x = AsciiBoard.boardRightBorder;
        int y = AsciiBoard.offsetY;
        CLI.printInColor("w","Nome: ");
        CLI.printInColor(weapon.getColor(),weapon.getName());
        CLI.moveCursor(x,++y);
        CLI.printInColor("w","Costo: ");
        for(String c: weapon.getCost())
            CLI.printInColor(c, AsciiTile.unicodeAmmo + " ");
        CLI.moveCursor(x,++y);
        for(ViewEffect e: weapon.getEffects()){
            CLI.printInColor("w",e.getName());
            CLI.moveCursor(x,++y);
            CLI.printInColor("w", "Costo: ");
            for(String a: e.getCost())
                CLI.printInColor(a,AsciiTile.unicodeAmmo + " ");
            CLI.moveCursor(x,++y);
        }
    }

    public static void drawEffectDesc(ViewEffect effect){
        CLI.moveCursor(AsciiBoard.boardRightBorder,AsciiBoard.offsetY);
        CLI.clearUntilEndOfLine(AsciiBoard.offsetY,AsciiBoard.offsetY + AsciiBoard.infoBoxHeight, AsciiBoard.boardRightBorder);
        CLI.printInColor("w", effect.getName() + ":");
        CLI.moveCursor(AsciiBoard.boardRightBorder,AsciiBoard.offsetY + 1);
        CLI.fixedWidthPrint(20,effect.getDesc());
    }
}
