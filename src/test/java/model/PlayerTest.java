package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player testPlayer, enemyPlayer, thirdPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player(false);
        enemyPlayer = new Player(false);
        thirdPlayer = new Player(false);

        // Check UUID reliability
        assertNotEquals(testPlayer.getId(), enemyPlayer.getId());
    }


    /**
     * Check if the logic rules of the game (first add damages, convert marks, then add marks) stands
     */
    @Test
    void receiveShotandMarks() {
        for (int i = 0; i < 2; i++) {
            testPlayer.receiveMark(enemyPlayer);
            testPlayer.receiveMark(thirdPlayer);
        }
        testPlayer.receiveShot(enemyPlayer, 9,1);

        // Test order and correct marks selection
        assertTrue(testPlayer.getDamages().size() == 11);
        assertTrue(testPlayer.getMarks().stream().filter(p->p.getId() == enemyPlayer.getId()).collect(Collectors.toList()).size() == 1);
        assertTrue(testPlayer.getMarks().stream().filter(p->p.getId() == thirdPlayer.getId()).collect(Collectors.toList()).size() == 2);


    }

    @Test
    void reload() {
        Weapon testWeapon = CardCreator.parseWeapon("lanciarazzi.btl");
        for (int i = 0; i < 2; i++)
            testPlayer.addAmmo(Ammo.RED);

        testPlayer.addWeapon(testWeapon);
        if (testPlayer.checkForAmmos(testWeapon.getCost()))
            testPlayer.reload(testWeapon);

        assertTrue(testPlayer.getWeapons().get(0).getLoaded());
        assertFalse(testPlayer.checkForAmmos(testPlayer.getWeapons().get(0).getCost()));
    }

    @Test
    void checkForAmmos() {
        Weapon testWeapon = CardCreator.parseWeapon("lanciarazzi.btl");
        for (int i = 0; i < 2; i++)
            testPlayer.addAmmo(Ammo.RED);
        testPlayer.addAmmo(Ammo.YELLOW);
        assertTrue(testPlayer.checkForAmmos(testWeapon.getCost()));

        enemyPlayer.addAmmo(Ammo.RED);
        assertFalse(enemyPlayer.checkForAmmos(testWeapon.getCost()));

    }

    @Test
    void receiveMark() {
        for (int i = 0; i < 4; i++) {
            testPlayer.receiveMark(enemyPlayer);
        }

        // Test if the limits of marks work
        assert(testPlayer.getMarks().size() == 3);
        assert(testPlayer.getMarks().contains(enemyPlayer));
    }
}