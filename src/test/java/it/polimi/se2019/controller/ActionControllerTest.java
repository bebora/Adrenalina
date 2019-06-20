package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ActionControllerTest {
    GameController gameController = new GameController(Arrays.asList(new Player("Nicola"),new Player("Rosetti")),"board3.btlb",8,false, null);
    ActionController actionController = new ActionController(gameController.getMatch(),gameController);
    Player currentPlayer = gameController.getMatch().getPlayers().get(gameController.getMatch().getCurrentPlayer());
    Board board = gameController.getMatch().getBoard();

    @BeforeEach
    void beforeEach() {
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
        Weapon grabbableWeapon = currentPlayer.getTile().getWeapons().stream().filter(w -> currentPlayer.checkForAmmos(w.getCost(), currentPlayer.totalAmmoPool())).findAny().get();
        actionController.updateOnWeapon(grabbableWeapon);
        assertEquals(grabbableWeapon, currentPlayer.getWeapons().get(0));
        assertEquals(2, currentPlayer.getAmmos().size());
        assertEquals(2, currentPlayer.getTile().getWeapons().size());
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
    void testAttack(){
        currentPlayer.addWeapon(CardCreator.parseWeapon("cyberblade.btl"));
        actionController.updateOnAction(currentPlayer.getActions().get(2));
        actionController.updateOnWeapon(currentPlayer.getWeapons().get(0));
        assertNotNull(actionController.getWeaponController());
        actionController.updateOnConclusion();
        assertNull(actionController.getWeaponController());
    }
}
