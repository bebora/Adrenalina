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
    public void prepareWeapon(String weapon) {
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
}
