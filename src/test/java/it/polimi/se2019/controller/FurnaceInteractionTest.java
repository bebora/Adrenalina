package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.rmi.RemoteException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class FurnaceInteractionTest extends EffectControllerFramework{
    @BeforeEach
    void prepareWeapon() throws RemoteException {
        prepareWeapon("furnace.btl");
        setupRequestDispatcher();
    }

    @Test
    void testCozyFire(){
        for (Player p : notCurrentPlayers) {
            p.setTile(testMatch.getBoard().getTile(0,1));
        }
        wp.updateOnEffect("in cozy fire mode");
        Utils.waitABit();
        EffectController ec = spy(wp.getEffectController());
        //Choose the tile you wanna attack
        Mockito.verify(requestDispatcher).addReceivingType((ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.TILES))), any(TimerConstrainedEventHandler.class));
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.TILES);
        eventHandler.receiveTiles(Collections.singletonList(testMatch.getBoard().getTile(0,1)));
        Utils.waitABit();
        //Verify damages
        sandboxMatch.restoreMatch(testMatch);
        for (Player p : notCurrentPlayers) {
            assertEquals(1, p.getDamagesCount());
        }
    }

    @Test
    void testBasic(){
        //Set the player in a valid position for basic effect
        notCurrentPlayers.get(1).setTile(testMatch.getBoard().getTile(1,0));
        notCurrentPlayers.get(0).setTile(testMatch.getBoard().getTile(1,1));
        wp.updateOnEffect("basic mode");
        Utils.waitABit();
        EffectController ec = spy(wp.getEffectController());
        //Choose the room you want to attack
        Mockito.verify(requestDispatcher).addReceivingType((ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.ROOM))), any(TimerConstrainedEventHandler.class));
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.ROOM);
        eventHandler.receiveRoom(testMatch.getBoard().getTile(1,1).getRoom());
        Utils.waitABit();

        //Check if both players got the damage
        for (Player p : notCurrentPlayers) {
            assertEquals(1, p.getDamagesCount());
        }



    }
}
