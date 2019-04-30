package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeaponControllerTest {
    List<Player> testPlayers = new ArrayList<>(Arrays.asList(new Player("buono", Color.RED),new Player("cattivo",Color.BLUE)));
    Match testMatch = new NormalMatch(testPlayers,"board1.btlb",5);
    Weapon testWeapon = CardCreator.parseWeapon("cannoneVortex.btl");
    Player currentPlayer = testMatch.getPlayers().get(testMatch.getCurrentPlayer());

    @Test
    void assignWeapon(){
        currentPlayer.addWeapon(testWeapon);
        WeaponController weaponControllerTest = new WeaponController(testMatch,null);
        weaponControllerTest.update(testWeapon);
        assertEquals(weaponControllerTest.getWeapon(),testWeapon);
        testMatch.getPlayers().get(testMatch.getCurrentPlayer()).getWeapons().remove(testWeapon);
        weaponControllerTest.update(testWeapon);
        assertEquals(weaponControllerTest.getWeapon(),null);
    }

     @Test
    void getUsableEffects(){
        //test with cannoneVortex(absolutePriority, missing Ammos)
         currentPlayer.addWeapon(testWeapon);
         WeaponController weaponControllerTest = new WeaponController(testMatch,testWeapon);
         assertEquals(weaponControllerTest.getUsableEffects().size(),1);
         for(int i = 0; i<3; i++)
             currentPlayer.getAmmos().remove(Ammo.RED);
         assertEquals(weaponControllerTest.getUsableEffects().get(0),"effetto base");
         assertEquals(weaponControllerTest.getUsableEffects().size(),1);

         //fill ammos
         for(int i = 0; i<3; i++) {
             currentPlayer.addAmmo(Ammo.RED);
             currentPlayer.addAmmo(Ammo.YELLOW);
             currentPlayer.addAmmo(Ammo.BLUE);
         }

         //test with fucileLaser(equal absolutePriority)
         testWeapon = CardCreator.parseWeapon("fucileLaser.btl");
         currentPlayer.addWeapon(testWeapon);
         weaponControllerTest.update(testWeapon);
         assertEquals(weaponControllerTest.getUsableEffects().size(), 2);

         //test with spadaFotonica(relativePriority)
         testWeapon = CardCreator.parseWeapon("spadaFotonica.btl");
         currentPlayer.addWeapon(testWeapon);
         weaponControllerTest.update(testWeapon);
         assertEquals(3,weaponControllerTest.getUsableEffects().size());
         assertEquals("passo d'ombra",weaponControllerTest.getUsableEffects().get(1));
         weaponControllerTest.update(testWeapon.getEffects().get(0));
         assertEquals(2,weaponControllerTest.getUsableEffects().size());
         assertEquals("passo d'ombra",weaponControllerTest.getUsableEffects().get(0));
         assertEquals("modalità sminuzzare",weaponControllerTest.getUsableEffects().get(1));
     }


}
