package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;

import static java.lang.Thread.sleep;

public class Utils {
    public static void waitABit() {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            assert false;
        }
    }

    public static void addFullAmmos(Player player) {
        for (int i = 0; i < 3; i++) {
            player.addAmmo(Ammo.RED);
            player.addAmmo(Ammo.BLUE);
            player.addAmmo(Ammo.YELLOW);
        }
    }
}
