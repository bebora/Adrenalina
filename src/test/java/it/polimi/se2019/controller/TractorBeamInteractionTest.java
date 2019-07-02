package it.polimi.se2019.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.rmi.RemoteException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class TractorBeamInteractionTest extends EffectControllerFramework{
    @BeforeEach
    void prepareWeapon() throws RemoteException {
        prepareWeapon("tractorBeam.btl");
        setupRequestDispatcher();
    }

    @Test
    void testBasic() throws RemoteException, InterruptedException {
        wp.updateOnEffect("basic mode");
        Utils.waitABit();
        EffectController ec = spy(wp.getEffectController());
        Utils.waitABit();
        //Choose the tile to the place you wanna move the player
        Mockito.verify(requestDispatcher).addReceivingType((ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.TILES))), any(TimerConstrainedEventHandler.class));
        //Move it to the tile 0,1
        Utils.waitABit();
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.TILES);
        eventHandler.receiveTiles(Collections.singletonList(sandboxMatch.getBoard().getTile(0,1)));
        Utils.waitABit();
        //Test that the effect is waiting for player
        Mockito.verify(requestDispatcher).addReceivingType((ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.PLAYERS))), any(TimerConstrainedEventHandler.class));
        //Test the move of a player
        eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.PLAYERS);
        eventHandler.receivePlayer(Collections.singletonList(notCurrentPlayers.get(1)));
        Utils.waitABit();
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(0, notCurrentPlayers.get(1).getTile().getPosy());
        assertEquals(1, notCurrentPlayers.get(1).getTile().getPosx());
        assertEquals(1, notCurrentPlayers.get(1).getDamagesCount());
    }

    @Test
    void testPunisher() throws InterruptedException {
        wp.updateOnEffect("basic mode");
        Utils.waitABit();
        EffectController ec = spy(wp.getEffectController());
        Utils.waitABit();
        new Thread(() -> wp.updateOnEffect("in punisher mode")).start();
        Utils.waitABit();
        //Test the targetting of the Player
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.PLAYERS);
        eventHandler.receivePlayer(Collections.singletonList(notCurrentPlayers.get(1)));
        Utils.waitABit();
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(0, notCurrentPlayers.get(1).getTile().getPosy());
        assertEquals(0, notCurrentPlayers.get(1).getTile().getPosx());
        assertEquals(3, notCurrentPlayers.get(1).getDamagesCount());
    }
}


