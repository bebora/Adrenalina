package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EffectControllerTest {
    private List<Player> testPlayers = new ArrayList<>(Arrays.asList(new Player("paolo"),new Player("roberto"),new Player("carmelo")));
    private Match testMatch = new NormalMatch(testPlayers,"board1.btlb",5);
    private Weapon testWeapon = CardCreator.parseWeapon("spadaFotonica.btl");
    private Player currentPlayer = testMatch.getPlayers().get(testMatch.getCurrentPlayer());
    private WeaponController wp = new WeaponController(testMatch,testWeapon);

    @Test
    void testCorrectTarget(){
        wp.update(testWeapon.getEffects().get(0));
        EffectController ec = wp.getEffectController();
        List<Player> notCurrentPlayer = testMatch.getPlayers().stream()
                .filter(p -> p != testMatch.getPlayers().get(testMatch.getCurrentPlayer()))
                .collect(Collectors.toList());
        //player satisfy the target condition
        currentPlayer.setTile(testMatch.getBoard().getTile(0,0));
        notCurrentPlayer.remove(1);
        notCurrentPlayer.get(0).setTile(testMatch.getBoard().getTile(0,0));
        ec.updateOnPlayers(notCurrentPlayer);
        assertEquals(2,notCurrentPlayer.get(0).getDamagesCount());
        assertEquals(currentPlayer,notCurrentPlayer.get(0).getDamages().get(0));
    }

    @Test
    void testWrongTarget(){
        Player enemy = new Player("nemico");
        testWeapon.getEffects().get(0).setActivated(false);
        enemy.setTile(testMatch.getBoard().getTile(1,0));
        wp.update(testWeapon.getEffects().get(0));
        wp.getEffectController().updateOnPlayers(Arrays.asList(enemy));
        assertNotEquals(2,enemy.getDamagesCount());
    }

    @Test
    void testMultipleEffects(){
        Player firstEnemy = new Player("nemico");
        Player secondEnemy = new Player("nemicoCattivo");
        testWeapon.getEffects().get(0).setActivated(false);
        secondEnemy.setTile(testMatch.getBoard().getTile(1,0));
        currentPlayer.setTile(testMatch.getBoard().getTile(1,0));
        firstEnemy.setTile(testMatch.getBoard().getTile(1,0));
        wp.update(testWeapon.getEffects().get(0));
        wp.getEffectController().updateOnPlayers(Arrays.asList(firstEnemy));
        assertEquals(2,firstEnemy.getDamagesCount());
        currentPlayer.addAmmo(Ammo.YELLOW);
        wp.update(testWeapon.getEffects().get(2));
        wp.getEffectController().updateOnPlayers(Arrays.asList(secondEnemy));
        assertEquals(2,secondEnemy.getDamagesCount());
    }

    @Test
    void testMove(){
        //move self
        wp.update(testWeapon.getEffects().get(1));
        currentPlayer.setTile(testMatch.getBoard().getTile(0,0));
        wp.getEffectController().updateOnTiles(Arrays.asList(testMatch.getBoard().getTile(0,1)));
        assertEquals(testMatch.getBoard().getTile(0,1),currentPlayer.getTile());
        //move opponent
        testWeapon = CardCreator.parseWeapon("fucileAPompa.btl");
        Player enemy = new Player("nemico");
        enemy.setTile(testMatch.getBoard().getTile(0,0));
        currentPlayer.setTile(testMatch.getBoard().getTile(0,0));
        currentPlayer.addWeapon(testWeapon);
        wp.update(testWeapon);
        wp.update(testWeapon.getEffects().get(0));
        wp.getEffectController().updateOnPlayers(Arrays.asList(enemy));
        wp.getEffectController().updateOnTiles(Arrays.asList(testMatch.getBoard().getTile(0,1)));
        assertEquals(3,enemy.getDamagesCount());
        assertEquals(testMatch.getBoard().getTile(0,1),enemy.getTile());
    }
}
