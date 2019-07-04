package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.network.ViewUpdaterRMI;
import it.polimi.se2019.view.ConcreteViewReceiver;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class EffectControllerFramework {
    List<Player> testPlayers;
    GameController gameController;
    Match testMatch;
    Match sandboxMatch;
    Weapon testWeapon;
    Player currentPlayer;
    Player originalCurrentPlayer;
    ActionController actionController;
    WeaponController wp;
    RequestDispatcher requestDispatcher;
    List<Player> notCurrentPlayers;

    /**
     * Utility method to setup the game to for further interactions
     * Sets the player, the gameController and the actionController
     * @param weapon
     */
    public void prepareWeapon(String weapon) throws RemoteException{
        testPlayers = new ArrayList<>(Arrays.asList(new Player("paolo"),new Player("roberto"),new Player("carmelo")));
        gameController = new GameController(testPlayers,"board1" +".btlb",5,false, null);
        testMatch = gameController.getMatch();
        testWeapon = CardCreator.parseWeapon(weapon);
        originalCurrentPlayer = testMatch.getPlayers().get(testMatch.getCurrentPlayer());
        originalCurrentPlayer.setVirtualView(new VirtualView(new LobbyController(new ArrayList<>(Arrays.asList(Mode.NORMAL)))));
        VirtualView view = new VirtualView();
        ViewUpdater viewUpdater = null;
        try {
            viewUpdater = new ViewUpdaterRMI(new ConcreteViewReceiver(view), view);
        }
        catch (RemoteException e) {
            System.out.println("Unable to create ViewReceiver");
        }
        originalCurrentPlayer.getVirtualView().setViewUpdater(viewUpdater, false);
        actionController = new ActionController(testMatch,gameController);
        sandboxMatch = new NormalMatch(testMatch);
        currentPlayer = sandboxMatch.getPlayers().get(sandboxMatch.getCurrentPlayer());
        currentPlayer.addWeapon(testWeapon);
        testWeapon.setLoaded(true);
        wp = new WeaponController(sandboxMatch,null,testMatch.getPlayers(),null);
        wp.updateOnWeapon(testWeapon);
    }

    /**
     * Utility method to set the requestDispatcher, to allow testing of the interaction
     * @throws RemoteException
     */
    void setupRequestDispatcher() throws RemoteException{
        requestDispatcher = new RequestDispatcher(mock(ViewUpdater.class), mock(VirtualView.class));
        requestDispatcher = spy(requestDispatcher);
        currentPlayer.getVirtualView().setRequestDispatcher(requestDispatcher);
        currentPlayer.setTile(testMatch.getBoard().getTile(0,0));
        //Setup the player that is shooting and the enemy
        notCurrentPlayers = sandboxMatch.getPlayers().stream()
                .filter(p -> p != sandboxMatch.getPlayers().get(sandboxMatch.getCurrentPlayer()))
                .collect(Collectors.toList());
        //player satisfy the target condition, not on same tile but visible
        notCurrentPlayers.get(0).setTile(testMatch.getBoard().getTile(0,1));
        notCurrentPlayers.get(1).setTile(testMatch.getBoard().getTile(0,2));
    }
}
