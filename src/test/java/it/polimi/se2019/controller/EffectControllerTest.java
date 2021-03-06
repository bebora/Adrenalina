package it.polimi.se2019.controller;

import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.CardCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EffectControllerTest extends EffectControllerFramework {

    @BeforeEach
    void prepareWeapon() throws RemoteException {
        prepareWeapon("cyberblade.btl");
    }
    @Test
    void testCorrectTarget(){
        //
        wp.updateOnEffect(testWeapon.getEffects().get(0).getName());
        EffectController ec = wp.getEffectController();
        List<Player> notCurrentPlayer = sandboxMatch.getPlayers().stream()
                .filter(p -> p != sandboxMatch.getPlayers().get(sandboxMatch.getCurrentPlayer()))
                .collect(Collectors.toList());
        Player originalNotCurrentPlayer = testMatch.getPlayers().stream()
                .filter(p -> p.getId().equals(notCurrentPlayer.get(0).getId()))
                .findAny().orElse(null);
        //Player satisfy the target condition
        currentPlayer.setTile(testMatch.getBoard().getTile(0,0));
        notCurrentPlayer.get(0).setTile(testMatch.getBoard().getTile(0,0));
        ec.updateOnPlayers(Arrays.asList(originalNotCurrentPlayer));
        sandboxMatch.restoreMatch(testMatch);
        //Set the target has been damaged
        assertEquals(2,notCurrentPlayer.get(0).getDamagesCount());
        assertEquals(testMatch.getPlayers().get(testMatch.getCurrentPlayer()),originalNotCurrentPlayer.getDamages().get(0));
        assertEquals(2,notCurrentPlayer.get(0).getDamagesCount());
    }

    @Test
    void testWrongTarget(){
        //Setup an enemy player
        Player enemy = new Player("nemico");
        testMatch.addPlayer(enemy);
        sandboxMatch = new NormalMatch(testMatch);
        wp.setMatch(sandboxMatch);
        testWeapon.getEffects().get(0).setActivated(false);
        //Test that damages weren't made to the enemy after the effect
        enemy.setTile(testMatch.getBoard().getTile(1,0));
        wp.updateOnEffect(testWeapon.getEffects().get(0).getName());
        wp.getEffectController().updateOnPlayers(Arrays.asList(enemy));
        assertNotEquals(2,enemy.getDamagesCount());
    }

    @Test
    void testMultipleEffects(){
        //Setup the players
        Player firstEnemy = new Player("nemico");
        Player secondEnemy = new Player("nemicoCattivo");
        testMatch.addPlayer(firstEnemy);
        testMatch.addPlayer(secondEnemy);
        testMatch.getPlayers().get(testMatch.getCurrentPlayer()).addAmmo(Ammo.YELLOW);
        sandboxMatch = new NormalMatch(testMatch);
        wp.setMatch(sandboxMatch);
        wp.setOriginalPlayers(testMatch.getPlayers());
        testWeapon.getEffects().get(0).setActivated(false);
        //Test the use of multiple effects, related to the cyberblade
        secondEnemy.setTile(testMatch.getBoard().getTile(1,0));
        currentPlayer.setTile(testMatch.getBoard().getTile(1,0));
        firstEnemy.setTile(testMatch.getBoard().getTile(1,0));
        wp.updateOnEffect(testWeapon.getEffects().get(0).getName());
        wp.getEffectController().updateOnPlayers(Arrays.asList(firstEnemy));
        wp.updateOnEffect(testWeapon.getEffects().get(2).getName());
        wp.getEffectController().updateOnPlayers(Arrays.asList(secondEnemy));
        //test that damages were made
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(2,firstEnemy.getDamagesCount());
        assertEquals(2,secondEnemy.getDamagesCount());
    }

    @Test
    void testMove(){
        //move self
        currentPlayer.setTile(testMatch.getBoard().getTile(0,0));
        wp.updateOnWeapon(testWeapon);
        wp.updateOnEffect(testWeapon.getEffects().get(1).getName());
        wp.getEffectController().updateOnTiles(Arrays.asList(testMatch.getBoard().getTile(0,1)));
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(testMatch.getBoard().getTile(0,1),originalCurrentPlayer.getTile());
        //move opponent
        testWeapon = CardCreator.parseWeapon("shotgun.btl");
        Player enemy = new Player("nemico");
        testMatch.addPlayer(enemy);
        enemy.setTile(testMatch.getBoard().getTile(0,0));
        originalCurrentPlayer.setTile(testMatch.getBoard().getTile(0,0));
        originalCurrentPlayer.addWeapon(testWeapon);
        sandboxMatch = new NormalMatch(testMatch);
        wp.setMatch(sandboxMatch);
        wp.updateOnWeapon(testWeapon);
        wp.updateOnEffect(testWeapon.getEffects().get(0).getName());
        actionController.updateOnAction(originalCurrentPlayer.getActions().get(0));
        wp.setActionController(actionController);
        wp.getEffectController().updateOnTiles(Arrays.asList(testMatch.getBoard().getTile(0,1)));
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(3,enemy.getDamagesCount());
        assertEquals(testMatch.getBoard().getTile(0,1),enemy.getTile());
    }
}
