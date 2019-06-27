package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.EffectControllerFramework;
import it.polimi.se2019.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

public class RocketLauncherInteractionTest extends EffectControllerFramework {
    RequestDispatcher requestDispatcher;
    @BeforeEach
    void prepareWeapon() {
        prepareWeapon("rocketLauncher.btl");
        requestDispatcher = Mockito.mock(RequestDispatcher.class);
        currentPlayer.getVirtualView().setRequestDispatcher(requestDispatcher);
        currentPlayer.setTile(testMatch.getBoard().getTile(0,0));
    }

    @Test
    void testMove() {
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
        Mockito.verify(requestDispatcher).addReceivingType(ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.PLAYERS)), any(TimerCostrainedEventHandler.class));
        ec.updateOnPlayers(Arrays.asList(originalNotCurrentPlayer));
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(2, notCurrentPlayer.get(0).getDamagesCount());
        Utils.waitABit();
        //Assert that the controller is waiting for tiles
        Mockito.verify(requestDispatcher).addReceivingType(ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.TILES)), any(TimerCostrainedEventHandler.class));
        // Choose the tile
        ec.updateOnTiles(Collections.singletonList(sandboxMatch.getBoard().getTile(0,1)));
        Utils.waitABit();
        Mockito.verify(requestDispatcher, times(2)).addReceivingType(ArgumentMatchers.argThat(arg -> arg.contains(ReceivingType.TILES)), any(TimerCostrainedEventHandler.class));
        ec.updateOnTiles(Collections.singletonList(sandboxMatch.getBoard().getTile(0,0)));
        //Verify
        sandboxMatch.restoreMatch(testMatch);
        assertEquals(sandboxMatch.getBoard().getTile(0,0), notCurrentPlayer.get(0).getTile());
    }

}
