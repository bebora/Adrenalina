package it.polimi.se2019.view.cli;

import it.polimi.se2019.view.ViewEffect;
import it.polimi.se2019.view.ViewWeapon;

import java.util.List;

/**
 * Handles the drawing of a weapon, and its info.
 */
public class AsciiWeapon {
    private AsciiWeapon () {}

    /**
     * Draws the info of a particular weapon:
     * <li>Name</li>
     * <li>Cost</li>
     * <lI>Effects name and cost</lI>
     * @param weaponIndex of the weapon to draw
     * @param displayedWeapons list of the weapon, containing the weapon to draw
     */
    public static void drawWeaponInfo(int weaponIndex, List<ViewWeapon> displayedWeapons){
        ViewWeapon weapon = displayedWeapons.get(weaponIndex);
        CLI.clearUntilEndOfLine(AsciiBoard.offsetY,AsciiBoard.offsetY+AsciiBoard.boardBottomBorder,AsciiBoard.offsetX);
        int x = AsciiBoard.offsetX;
        int y = AsciiBoard.offsetY;
        CLI.printInColor("w","Name: ");
        CLI.printInColor(weapon.getColor(),weapon.getName());
        CLI.moveCursor(x,++y);
        CLI.printInColor("w","Cost: ");
        for(String c: weapon.getCost())
            CLI.printInColor(c, AsciiTile.unicodeAmmo + " ");
        CLI.moveCursor(x,++y);
        for(ViewEffect e: weapon.getEffects()){
            CLI.printInColor("w",e.getName());
            CLI.moveCursor(x,++y);
            CLI.printInColor("w", "Cost: ");
            for(String a: e.getCost())
                CLI.printInColor(a,AsciiTile.unicodeAmmo + " ");
            CLI.moveCursor(x,++y);
        }
        drawEffectsDesc(weapon);
    }

    /**
     * Draw the description of the effects of the weapon
     * @param weapon that needs his effects displayed
     */
    public static void drawEffectsDesc(ViewWeapon weapon){
        int x = 40;
        int y = AsciiBoard.offsetY;
        for(ViewEffect effect : weapon.getEffects()) {
            CLI.moveCursor(x,y);
            CLI.printInColor("w", effect.getName() + ":");
            CLI.moveCursor(x, y +1);
            CLI.fixedWidthPrint(20, effect.getDesc());
            x += 40;
        }
        CLI.moveCursor(0, AsciiBoard.boardBottomBorder + 6);
        CLI.cleanRow();
    }
}
