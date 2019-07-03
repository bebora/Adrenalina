package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

public class RocketLauncherInteractionTest extends EffectControllerFramework {
    @BeforeEach
    void prepareWeapon() throws RemoteException {
        prepareWeapon("rocketLauncher.btl");
        setupRequestDispatcher();
    }

    @Test
    void testMovePlayer() {
        //Setup the player that is shooting and the enemy
        List<Player> notCurrentPlayer = sandboxMatch.getPlayers().stream()
                .filter(p -> p != sandboxMatch.getPlayers().get(sandboxMatch.getCurrentPlayer()))
                .collect(Collectors.toList());
        Player originalNotCurrentPlayer = testMatch.getPlayers().stream()
                .filter(p -> p.getId().equals(notCurrentPlayer.get(0).getId()))
                .findAny().orElse(null);
        //player satisfy the target condition, not on same tile but visible
        notCurrentPlayer.get(0).setTile(testMatch.getBoard().getTile(0,1));
        wp.updateOnEffect(testWeapon.getEffects().get(0).getName());
        EffectController ec = spy(wp.getEffectController());
        Utils.waitABit();
        Mockito.verify(requestDispatcher).addReceivingType(ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.PLAYERS)), any(TimerConstrainedEventHandler.class));
        ec.updateOnPlayers(Arrays.asList(originalNotCurrentPlayer));
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(2, notCurrentPlayer.get(0).getDamagesCount());
        Utils.waitABit();
        //Assert that the controller is waiting for tiles
        Mockito.verify(requestDispatcher).addReceivingType(ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.TILES)), any(TimerConstrainedEventHandler.class));
        // Choose the tile to move the player just hit
        Mockito.verify(requestDispatcher, times(1)).addReceivingType(ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.TILES)), any(TimerConstrainedEventHandler.class));
        ec.updateOnTiles(Collections.singletonList(sandboxMatch.getBoard().getTile(0,0)));
        Utils.waitABit();
        //Verify
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(sandboxMatch.getBoard().getTile(0,0), notCurrentPlayer.get(0).getTile());
        assertEquals(2, notCurrentPlayer.get(0).getDamagesCount());
    }

    @Test
    void testBasicPlusFragmenting() throws InterruptedException {
        for (Player p : notCurrentPlayers) {
            p.setTile(testMatch.getBoard().getTile(0, 1));
            p.resetPlayer();
        }
        wp.updateOnEffect(testWeapon.getEffects().get(0).getName());
        EffectController ec = spy(wp.getEffectController());
        Utils.waitABit();
        //Choose target
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.PLAYERS);
        eventHandler.receivePlayer(Collections.singletonList(notCurrentPlayers.get(0)));
        Utils.waitABit();
        //Move target
        eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.TILES);
        eventHandler.receiveTiles(Collections.singletonList(sandboxMatch.getBoard().getTile(0,1)));
        Utils.waitABit();
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(sandboxMatch.getBoard().getTile(0,1), notCurrentPlayers.get(0).getTile());
        assertEquals(2, notCurrentPlayers.get(0).getDamagesCount());
        assertEquals(0, notCurrentPlayers.get(1).getDamagesCount());
        Mockito.verify(requestDispatcher, times(1)).addReceivingType(ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.EFFECT)), any(TimerConstrainedEventHandler.class));

        //Verify warhead works as expected
        eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.EFFECT);
        eventHandler.receiveEffect(testWeapon.getEffects().get(2).getName());
        Utils.waitABit();
        eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.TILES);
        eventHandler.receiveTiles(Collections.singletonList(sandboxMatch.getBoard().getTile(0,1)));
        Utils.waitABit();
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(3, notCurrentPlayers.get(0).getDamagesCount());
        assertEquals(1, notCurrentPlayers.get(1).getDamagesCount());
    }

}
