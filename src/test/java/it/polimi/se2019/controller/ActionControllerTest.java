package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ActionControllerTest {
    GameController gameController = new GameController(Arrays.asList(new Player("Nicola"),new Player("Rosetti")),"board1.btlb",8,false);
    ActionController actionController = new ActionController(gameController.getMatch(),gameController);
    Player currentPlayer = gameController.getMatch().getPlayers().get(gameController.getMatch().getCurrentPlayer());
    Board board = gameController.getMatch().getBoard();

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
        Weapon grabbableWeapon = currentPlayer.getTile().getWeapons().get(0);
        actionController.updateOnWeapon(grabbableWeapon);
        assertEquals(grabbableWeapon,currentPlayer.getWeapons().get(0));
        assertEquals(2,currentPlayer.getAmmos().size());
        assertEquals(2,currentPlayer.getTile().getWeapons().size());
    }

    @Test
    void testGrabPowerUp(){
        currentPlayer.setTile(board.getTile(0,0));
        actionController.updateOnAction(currentPlayer.getActions().get(1));
        actionController.updateOnTiles(Collections.singletonList(currentPlayer.getTile()));
        AmmoCard grabbableAmmocard = currentPlayer.getTile().getAmmoCard();
        actionController.updateOnAmmoCard(grabbableAmmocard);
        currentPlayer.getAmmos().remove(Ammo.RED);
        currentPlayer.getAmmos().remove(Ammo.YELLOW);
        currentPlayer.getAmmos().remove(Ammo.BLUE);
        assertEquals(grabbableAmmocard.getAmmos(),currentPlayer.getAmmos());
        assertNull(currentPlayer.getTile().getAmmoCard());
    }

    @Test
    void testAttack(){
        currentPlayer.addWeapon(CardCreator.parseWeapon("spadaFotonica.btl"));
        currentPlayer.setVirtualView(new VirtualView(new LobbyController(Collections.singletonList(Mode.NORMAL))));
        actionController.updateOnAction(currentPlayer.getActions().get(2));
        actionController.updateOnWeapon(currentPlayer.getWeapons().get(0));
        assertNotNull(actionController.getWeaponController());
        actionController.updateOnConclusion();
        assertNull(actionController.getWeaponController());
    }
}