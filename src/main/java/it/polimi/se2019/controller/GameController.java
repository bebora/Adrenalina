package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Observer;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Moment;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.view.SelectableOptions;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.se2019.controller.ReceivingType.PLAYERS;
import static it.polimi.se2019.model.ThreeState.OPTIONAL;
import static it.polimi.se2019.model.ThreeState.TRUE;

public class GameController extends Observer {
    private Match match;
    private LobbyController lobbyController;
    private int actionCounter=0;
    private Player currentPlayer;
    private ActionController actionController;
    private List<Player> spawnablePlayers;
    private Random random = new Random();
    private TimerCostrainedEventHandler timerCostrainedEventHandler;
    private AcceptableTypes acceptableTypes;
    List<Player> overkillPlayers;


    public void checkEnd() {
        if (match.getPlayers().stream().filter(p -> !p.getDominationSpawn() && p.getOnline()).count() < 3)
            sendWinners();
    }

    @Override
    public void updateOnAction(Action action){
        actionController = new ActionController(match,this);
        actionController.updateOnAction(action);
    }

    @Override
    public void updateOnPlayers(List<Player> players) {
        if (acceptableTypes.getSelectablePlayers().checkForCoherency(players)) {
            currentPlayer.getVirtualView().getRequestDispatcher().clear();
            players.get(0).receiveShot(currentPlayer, 1, 0);
            endTurn();
        }
        else {
            throw new IncorrectEvent("Wrong players!");
        }
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        if (acceptableTypes.getSelectablePowerUps().checkForCoherency(powerUps)) {
            currentPlayer.getVirtualView().getRequestDispatcher().clear();
            currentPlayer.setAlive(TRUE);
            Tile tile = match.getBoard().getTiles().stream().flatMap(List::stream).
                    filter(t -> t != null && t.isSpawn() && t.getRoom() == Color.valueOf(powerUps.get(0).getDiscardAward().toString())).findFirst().orElseThrow(() -> new IncorrectEvent("Errore nel powerUp!"));
            currentPlayer.setTile(tile);
            currentPlayer.discardPowerUp(powerUps.get(0), false);
            playTurn();
        }
        else {
            throw new IncorrectEvent("PowerUps not acceptable!");
        }
    }

    public GameController(List<Player> players, String boardName, int numSkulls, boolean domination, LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        if(!domination){
            match = new NormalMatch(players,boardName,numSkulls);
        }else{
            match = new DominationMatch(players,boardName,numSkulls);
        }
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
    }

    public void startTurn(){
        if (!currentPlayer.getOnline()) {
            endTurn();
        }
        if(currentPlayer.getAlive() == ThreeState.OPTIONAL){
            for(int i = 0; i < 2; i++){
                currentPlayer.addPowerUp(match.getBoard().drawPowerUp(),false);
            }
            List<ReceivingType> receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.POWERUP));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(currentPlayer.getPowerUps(), 1,1, "Seleziona un PowerUp!"));
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                    this,
                    currentPlayer.getVirtualView().getRequestDispatcher(),
                    acceptableTypes);
            timerCostrainedEventHandler.start();
        }
        else {
            playTurn();
        }
    }

    public void playTurn() {
        List<ReceivingType> receivingTypes = new ArrayList<>();
        if(actionCounter < currentPlayer.getMaxActions()) {
            receivingTypes.add(ReceivingType.ACTION);
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectableActions(new SelectableOptions<>(currentPlayer.getActions(), 1, 1, "Seleziona un'azione"));
        }
        if (currentPlayer.getPowerUps().stream().
                filter(p -> p.getApplicability() == Moment.OWNROUND).
                count() >= 1) {
            receivingTypes.addAll(Arrays.asList(ReceivingType.POWERUP, ReceivingType.STOP));
            List<PowerUp> usablePowerups = currentPlayer.getPowerUps().stream().
                    filter(p -> p.getApplicability() == Moment.OWNROUND).collect(Collectors.toList());
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(usablePowerups, 1, 1, "Seleziona un'azione o un powerup"));
        }
        timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                this,
                currentPlayer.getVirtualView().getRequestDispatcher(),
                acceptableTypes);
        timerCostrainedEventHandler.start();
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
        overkillPlayers = match.
                getPlayers().
                stream().
                filter(p -> !p.getDominationSpawn() && p.getDamages().size() == 8).
                collect(Collectors.toList());
        List<Player> spawnPoints = match.
                getPlayers().
                stream().
                filter(Player::getDominationSpawn).
                collect(Collectors.toList());
        if (!spawnPoints.isEmpty() &&  !overkillPlayers.isEmpty()) {
            acceptableTypes = new AcceptableTypes(Arrays.asList(PLAYERS));
            acceptableTypes.setSelectablePlayers(new SelectableOptions<>(spawnPoints, 1,1, String.format("Select a spawn point to deposit %s overkill", overkillPlayers.get(0).getUsername())));
            overkillPlayers.get(0).getDamages().remove(7);
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, currentPlayer.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerCostrainedEventHandler.start();
            try {
                timerCostrainedEventHandler.join();
            }
            catch (InterruptedException e) {
                Logger.log(Priority.ERROR, "Join on domination overkill blocked by " + e.getMessage());
            }
            if (!timerCostrainedEventHandler.isBlocked()) {
                   overkillPlayers.stream().forEach(p -> p.getDamages().remove(7));
            }
        }
        else if (match.newTurn()) {
            sendWinners();
            lobbyController.getGames().remove(this);
            return;
        }
        actionCounter = 0;
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
        spawnablePlayers =match.getPlayers().stream()
                .filter(p -> p.getAlive() == ThreeState.FALSE)
                .collect(Collectors.toList());
        if(spawnablePlayers.isEmpty())
            startTurn();
        else
            startSpawning();
    }

    public void sendWinners() {
        List<Player> players = match.getWinners();
        StringBuilder stringBuffer = new StringBuilder("WINNERS$Winners are ");
        for (Player p : players) {
            stringBuffer.append(p.getUsername() + ", ");
        }
        players.stream().filter(Player::getOnline).forEach(p -> p.getVirtualView().getViewUpdater().sendPopupMessage(stringBuffer.toString()));
        //TODO @simone send winners to players
        //TODO stop all the socket connections
        match.getPlayers().stream().filter(p -> p.getOnline()).forEach(p -> p.setOnline(false));

    }

    public void startSpawning(){
        List<TimerCostrainedEventHandler> timerCostrainedEventHandlers = new ArrayList<>();
        for(Player p: spawnablePlayers){
            List<ReceivingType> receivingTypes = Collections.singletonList(ReceivingType.POWERUP);
            Observer spawner = new Spawner(p,match.getBoard());
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(p.getPowerUps(), 1,1,"Seleziona il powerup da scartare!"));
            timerCostrainedEventHandlers.add(new TimerCostrainedEventHandler(spawner,p.getVirtualView().getRequestDispatcher(), acceptableTypes));
        }
        for (TimerCostrainedEventHandler t : timerCostrainedEventHandlers) {
            try {
                t.join();
            }
            catch (Exception e) {
                Logger.log(Priority.DEBUG, "Ended handler powerup damaged");
            }
        }
    }

    @Override
    public void updateOnStopSelection(boolean reverse, boolean skip){
        if (currentPlayer.getAlive() == OPTIONAL) {
            updateOnPowerUps(Arrays.asList(acceptableTypes.getSelectablePowerUps().getOptions().stream().findAny().orElse(null)), true);
        }
        else {
            currentPlayer.getVirtualView().getRequestDispatcher().clear();
        }
        if (reverse) {
            actionCounter ++;
            actionController = null;
            if (skip || actionCounter == currentPlayer.getMaxActions()) {
                endTurn();
            }
            else {
                playTurn();
            }
        }
    }

}
