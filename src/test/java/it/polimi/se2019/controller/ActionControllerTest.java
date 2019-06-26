package it.polimi.se2019.controller;

import it.polimi.se2019.GameProperties;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.Reload;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.network.ViewUpdaterRMI;
import it.polimi.se2019.view.ConcreteViewReceiver;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ActionControllerTest {
    GameController gameController;
    ActionController actionController;
    Player currentPlayer;
    Board board;

    @BeforeEach
    void beforeEach() {
        gameController = Mockito.spy(new GameController(Arrays.asList(new
                Player("Nicola"),new Player("Rosetti")),"board3.btlb",8,false, null));
        currentPlayer = gameController.getMatch().getPlayers().get(gameController.getMatch().getCurrentPlayer());
        board = gameController.getMatch().getBoard();
        actionController = Mockito.spy(new ActionController(gameController.getMatch(),gameController));
        currentPlayer.setVirtualView(new VirtualView(new LobbyController(new ArrayList<>(Arrays.asList(Mode.NORMAL)))));
        VirtualView view = new VirtualView();
        ViewUpdater viewUpdater = null;
        try {
            viewUpdater = new ViewUpdaterRMI(new ConcreteViewReceiver(view), view);
        }
        catch (RemoteException e) {
            System.out.println("Unable to create ViewReceiver");
        }
        currentPlayer.getVirtualView().setViewUpdater(viewUpdater, false);
    }
    @Test
    void testMove(){
        currentPlayer.setTile(board.getTile(0,0));
        actionController.updateOnAction(currentPlayer.getActions().get(0));
        actionController.updateOnTiles(Collections.singletonList(board.getTile(0,1)));
        assertEquals(board.getTile(0,1),currentPlayer.getTile());
    }

    @Test
    void testGrabWeapon(){
        currentPlayer.setTile(board.getTile(0,2));
        actionController.updateOnAction(currentPlayer.getActions().get(1));
        actionController.updateOnTiles(Collections.singletonList(currentPlayer.getTile()));
        Weapon grabbableWeapon = currentPlayer.getTile().getWeapons().stream().filter(w -> currentPlayer.checkForAmmos(Collections.singletonList(w.getCost().get(0)))).findAny().get();
        actionController.updateOnWeapon(grabbableWeapon);
        assertEquals(grabbableWeapon, currentPlayer.getWeapons().get(0));
        assertEquals(2, currentPlayer.getAmmos().size());
        assertEquals(2, currentPlayer.getTile().getWeapons().size());
    }

    @Test
    void testReloadWeaponOnce() {
        for (int i = 0; i < 3; i++) {
            currentPlayer.addAmmo(Ammo.RED);
            currentPlayer.addAmmo(Ammo.BLUE);
            currentPlayer.addAmmo(Ammo.YELLOW);
        }
        Mockito.doNothing().when(actionController).updateOnStopSelection(any());
        //Test it stops the action once no weapon can be no more reload, and it reloads one
        currentPlayer.setTile(board.getTile(0,2));
        Weapon grabbableWeapon = currentPlayer.getTile().getWeapons().stream().filter(w -> currentPlayer.checkForAmmos(Collections.singletonList(w.getCost().get(0)))).findAny().get();
        currentPlayer.addWeapon(grabbableWeapon);
        grabbableWeapon.setLoaded(false);
        Action reload = new Reload();
        currentPlayer.getActions().add(reload);
        actionController.updateOnAction(reload);
        actionController.updateOnWeapon(grabbableWeapon);
        Mockito.verify(actionController).updateOnStopSelection(any());
        assertTrue(grabbableWeapon.getLoaded());
    }

    @Test
    void testReloadMoreWeapons() {
        for (int i = 0; i < 3; i++) {
            currentPlayer.addAmmo(Ammo.RED);
            currentPlayer.addAmmo(Ammo.BLUE);
            currentPlayer.addAmmo(Ammo.YELLOW);
        }
        Mockito.doNothing().when(actionController).updateOnStopSelection(any());
        //Test it stops the action once no weapon can be no more reload, and it reloads one
        currentPlayer.setTile(board.getTile(0,2));
        Weapon grabbableWeapon = null;
        try {
            for (int i = 0; i < 2; i++) {
                grabbableWeapon = currentPlayer.getTile().getWeapons().stream().filter(w -> currentPlayer.checkForAmmos(Collections.singletonList(w.getCost().get(0))) && !currentPlayer.getWeapons().contains(w)).findAny().orElseThrow(UnsupportedOperationException::new);
                currentPlayer.addWeapon(grabbableWeapon);
                grabbableWeapon.setLoaded(false);
            }
        } catch (UnsupportedOperationException e) {
            return;
        }
        Action reload = new Reload();
        currentPlayer.getActions().add(reload);
        actionController.updateOnAction(reload);
        actionController.updateOnWeapon(grabbableWeapon);
        Mockito.verify(actionController, times(0)).updateOnStopSelection(any());
        assertTrue(grabbableWeapon.getLoaded());
    }

    @Test
    void testGrabPowerUp(){
        currentPlayer.setTile(board.getTile(0,0));
        AmmoCard grabbableAmmocard = currentPlayer.getTile().getAmmoCard();
        actionController.updateOnAction(currentPlayer.getActions().get(1));
        actionController.updateOnTiles(Collections.singletonList(currentPlayer.getTile()));
        currentPlayer.getAmmos().remove(Ammo.RED);
        currentPlayer.getAmmos().remove(Ammo.YELLOW);
        currentPlayer.getAmmos().remove(Ammo.BLUE);
        assertEquals(grabbableAmmocard.getAmmos().stream().filter(p -> !p.equals(Ammo.POWERUP)).collect(Collectors.toList()), currentPlayer.getAmmos());
        assertNull(currentPlayer.getTile().getAmmoCard());
    }

    @Test
    void testDiscardWeapon() {
        currentPlayer.setTile(board.getTile(0,2));
        Weapon grabbableWeapon = currentPlayer.getTile().getWeapons().stream().filter(w -> currentPlayer.checkForAmmos(Collections.singletonList(w.getCost().get(0)))).findAny().get();
        for (int i = 0; i < Integer.parseInt(GameProperties.getInstance().getProperty("max_weapons")); i++) {
            currentPlayer.addWeapon(grabbableWeapon);
        }
        actionController.updateOnAction(currentPlayer.getActions().get(1));
        actionController.updateOnTiles(Collections.singletonList(currentPlayer.getTile()));
        //Test discarding of a weapon
        actionController.updateOnAction(currentPlayer.getActions().get(1));
        actionController.updateOnTiles(Collections.singletonList(currentPlayer.getTile()));
        grabbableWeapon = currentPlayer.getTile().getWeapons().stream().filter(w -> currentPlayer.checkForAmmos(Collections.singletonList(w.getCost().get(0)))).findAny().get();
        actionController.updateOnWeapon(grabbableWeapon);
        assertEquals(2, currentPlayer.getWeapons().size());
        //Test get weapon
        actionController.updateOnWeapon(grabbableWeapon);
        assertEquals(3, currentPlayer.getWeapons().size());
    }


    @Test
    void testAttack(){
        currentPlayer.addWeapon(CardCreator.parseWeapon("cyberblade.btl"));
        actionController.updateOnAction(currentPlayer.getActions().get(2));
        actionController.updateOnWeapon(currentPlayer.getWeapons().get(0));
        assertNotNull(actionController.getWeaponController());
        actionController.updateOnConclusion();
        assertNull(actionController.getWeaponController());
    }
}
