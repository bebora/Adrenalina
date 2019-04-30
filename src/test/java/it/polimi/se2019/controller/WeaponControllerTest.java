package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
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
    @Test
    void assignWeapon(){
        testMatch.getPlayers().get(testMatch.getCurrentPlayer()).addWeapon(testWeapon);
        WeaponController weaponControllerTest = new WeaponController(testMatch,null);
        weaponControllerTest.update(testWeapon);
        assertEquals(weaponControllerTest.getWeapon(),testWeapon);
        testMatch.getPlayers().get(testMatch.getCurrentPlayer()).getWeapons().remove(testWeapon);
        weaponControllerTest.update(testWeapon);
        assertEquals(weaponControllerTest.getWeapon(),null);
    }


}
