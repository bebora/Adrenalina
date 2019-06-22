package it.polimi.se2019.model;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player testPlayer, enemyPlayer, thirdPlayer;

    @BeforeEach
    void setUp() {
        testPlayer = new Player("test1");
        enemyPlayer = new Player("test2");
        thirdPlayer = new Player("test3");

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
        testPlayer.receiveShot(enemyPlayer, 9,1, true);

        // Test order and correct marks selection
        assertEquals(11,testPlayer.getDamages().size());
        assertEquals(1, testPlayer.getMarks().stream().filter(p->p.getId().equals(enemyPlayer.getId())).count());
        assertEquals(2, testPlayer.getMarks().stream().filter(p->p.getId().equals(thirdPlayer.getId())).count());


    }

    @Test
    void reload() {
        Weapon testWeapon = CardCreator.parseWeapon("rocketLauncher.btl");
        for (int i = 0; i < 2; i++)
            testPlayer.addAmmo(Ammo.RED);

        testPlayer.addWeapon(testWeapon);
        if (Player.checkForAmmos(testWeapon.getCost(), testPlayer.getAmmos()))
            testPlayer.reload(testWeapon);

        assertTrue(testPlayer.getWeapons().get(0).getLoaded());
        assertTrue(testPlayer.getWeapons().get(0).getEffects().stream().noneMatch(Effect::getActivated));
        assertTrue(testPlayer.getWeapons().get(0).getEffects().stream().noneMatch(e -> e.getDirection() != null));
    }

    @Test
    void checkForAmmos() {
        Weapon testWeapon = CardCreator.parseWeapon("rocketLauncher.btl");
        for (int i = 0; i < 2; i++)
            testPlayer.addAmmo(Ammo.RED);
        testPlayer.addAmmo(Ammo.YELLOW);
        assertTrue(Player.checkForAmmos(testWeapon.getCost(),testPlayer.getAmmos()));

        assertFalse(Player.checkForAmmos(testWeapon.getCost(),enemyPlayer.getAmmos()));

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

    @Test
    void checkMaxActions() {
        assert(testPlayer.getMaxActions() == 2);
        assert(enemyPlayer.getMaxActions() == 2);
        testPlayer.notifyFrenzy(false);
        enemyPlayer.notifyFrenzy(true);
        assert(testPlayer.getMaxActions() == 2);
        assert(enemyPlayer.getMaxActions() == 1);
    }

    @Test
    void canReloadTest() {
        Player leaf = new Player("route");
        Weapon testWeapon = CardCreator.parseWeapon("furnace.btl");
        leaf.addWeapon(testWeapon);
        assertFalse(leaf.canReload());
        testWeapon.setLoaded(false);
        assertTrue(leaf.canReload());
    }

    @Test
    void checkTotalAmmoPool() {
        Player ar = new Player("lib");
        assertEquals(3, ar.totalAmmoPool().size());
        PowerUp powerUp = new PowerUp(new PowerUp.Builder().setDiscardAward(Ammo.RED));
        ar.addPowerUp(powerUp, true);
        assertEquals(4, ar.totalAmmoPool().size());
    }

    @Test
    void canDiscardPowerUpTest() {
        Player i3 = new Player("gaps");
        i3.getAmmos().clear();
        i3.getPowerUps().clear();
        assertFalse(i3.canDiscardPowerUp(Collections.singletonList(Ammo.RED)));
        PowerUp powerUp = new PowerUp(new PowerUp.Builder().setDiscardAward(Ammo.RED));
        i3.addPowerUp(powerUp, true);
        assertFalse(i3.canDiscardPowerUp(Collections.singletonList(Ammo.YELLOW)));
        assertTrue(i3.canDiscardPowerUp(Collections.singletonList(Ammo.RED)));
    }

    @Test
    void hasPowerUpTest() {
        Player herb = new Player("stluftwm");
        assertFalse(herb.hasPowerUp(Moment.DAMAGING));
        PowerUp powerUp = new PowerUp(new PowerUp.Builder().setDiscardAward(Ammo.RED).setApplicability(Moment.DAMAGING));
        herb.addPowerUp(powerUp, true);
        assertTrue(herb.hasPowerUp(Moment.DAMAGING));
    }

    @Test
    void discardPowerUpTest() {
        Player fire = new Player("fox");
        //Match needed due to discardPowerUp interacting with match's board
        Match match = new NormalMatch(Collections.singletonList(fire), "board4.btlb", 8);
        fire.setMatch(match);
        fire.getAmmos().clear();
        fire.getPowerUps().clear();
        PowerUp powerUp = new PowerUp(new PowerUp.Builder().setDiscardAward(Ammo.RED));
        fire.addPowerUp(powerUp, true);
        //There should be one powerup and zero ammos
        assertEquals(1, fire.getPowerUps().size());
        assertTrue(fire.getAmmos().isEmpty());
        fire.discardPowerUp(powerUp, false);
        //Both should be empty because "award" was false (=do not add discarded to ammos)
        assertTrue(fire.getPowerUps().isEmpty());
        assertTrue(fire.getAmmos().isEmpty());
        fire.addPowerUp(powerUp, true);
        fire.discardPowerUp(powerUp, true);
        //Cost should be empty because "award" was true
        assertTrue(fire.getPowerUps().isEmpty());
        assertEquals(1, fire.getAmmos().size());
    }
}