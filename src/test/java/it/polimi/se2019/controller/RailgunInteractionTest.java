package it.polimi.se2019.controller;

import it.polimi.se2019.model.cards.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

public class RailgunInteractionTest extends EffectControllerFramework{


    @BeforeEach
    void prepareWeapon() throws RemoteException{
        prepareWeapon("railgun.btl");
        setupRequestDispatcher();
    }



    @Test
    void testPulverize() throws RemoteException {
        new Thread(() -> wp.updateOnEffect("in piercing mode")).start();
        Utils.waitABit();
        EffectController ec = spy(wp.getEffectController());
        Utils.waitABit();
        //Test that the effect is waiting for direction
        Mockito.verify(requestDispatcher).addReceivingType((ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.DIRECTION))), any(TimerConstrainedEventHandler.class));
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.DIRECTION);
        eventHandler.receiveDirection(Direction.EAST);
        Utils.waitABit();
        //Test that the effect is now waiting for players
        Mockito.verify(requestDispatcher).addReceivingType((ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.PLAYERS))), any(TimerConstrainedEventHandler.class));
        Utils.waitABit();
        eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.PLAYERS);
        eventHandler.receivePlayer(Arrays.asList(notCurrentPlayers.get(0), notCurrentPlayers.get(1)));
        Utils.waitABit();
        //Test for applied damages
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(2, notCurrentPlayers.get(0).getDamagesCount());
        assertEquals(2, notCurrentPlayers.get(1).getDamagesCount());
    }

    @Test
    void testBasic() throws RemoteException{
        //Test the basic effect, making sure no damages is made if conditions are not met
        new Thread(() -> wp.updateOnEffect("basic mode")).start();
        Utils.waitABit();
        EffectController ec = spy(wp.getEffectController());
        Utils.waitABit();
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.DIRECTION);
        eventHandler.receiveDirection(Direction.EAST);
        Utils.waitABit();
        eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.PLAYERS);
        eventHandler.receivePlayer(Arrays.asList(notCurrentPlayers.get(0), notCurrentPlayers.get(1)));
        Utils.waitABit();
        //Test for applied damages
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(0, notCurrentPlayers.get(0).getDamagesCount());
        assertEquals(0, notCurrentPlayers.get(1).getDamagesCount());
        eventHandler.receivePlayer(Collections.singletonList(notCurrentPlayers.get(0)));
        Utils.waitABit();
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(3, notCurrentPlayers.get(0).getDamagesCount());
    }
}
