package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player testPlayer, enemyPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player(false);
        enemyPlayer = new Player(false);

        // Check UUID reliability
        assertNotEquals(testPlayer.getId(), enemyPlayer.getId());
    }
    @Test
    void convertMarks() {
    }

    @Test
    void receiveShot() {
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