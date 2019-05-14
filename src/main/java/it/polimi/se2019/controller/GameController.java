package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.SelectStop;
import it.polimi.se2019.model.DominationMatch;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.cards.Moment;

import java.util.List;

//TODO this class will be an observer for events
public class GameController {
    private Match match;
    private LobbyController lobbyController;
    private int actionCounter=0;
    private Player currentPlayer;
    private ActionController actionController;

    public GameController(List<Player> players,String boardName, int numSkulls, boolean domination) {
        if(!domination){
            match = new NormalMatch(players,boardName,numSkulls);
        }else{
            match = new DominationMatch(players,boardName,numSkulls);
        }
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
    }

    //TODO: substitute comments with actual communication with the player
    public void playTurn() {
        if(actionCounter < currentPlayer.getMaxActions()){
            //tells the player to choose an action
        }else{
            //tells the player to choose a power up
        }
    }

    public Match getMatch() {
        return match;
    }

    public void updateOnConclusion(){
        actionCounter++;
        actionController = null;
        if(currentPlayer.hasPowerUp(Moment.OWNROUND) || actionCounter < currentPlayer.getMaxActions()){
            playTurn();
        }
        else {
            endTurn();
        }
    }

    public void endTurn(){
        match.newTurn();
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
        playTurn();
    }

    public void updateOnStopSelection(SelectStop selectStop){
        actionController = null;
        playTurn();
    }

}
