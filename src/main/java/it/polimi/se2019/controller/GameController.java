package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Moment;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.view.SelectableOptions;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//TODO this class will be an observer for events
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

    public GameController(List<Player> players,String boardName, int numSkulls, boolean domination) {
        if(!domination){
            match = new NormalMatch(players,boardName,numSkulls);
        }else{
            match = new DominationMatch(players,boardName,numSkulls);
        }
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
    }

    public void startTurn(){
        if(currentPlayer.getAlive() == ThreeState.OPTIONAL){
            for(int i = 0; i < 2; i++){
                currentPlayer.addPowerUp(match.getBoard().drawPowerUp(),false);
            }
            List<ReceivingType> receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.POWERUP));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(currentPlayer.getPowerUps(), 1,1, "Seleziona un PowerUp!"));
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
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
        timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
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
        match.newTurn();
        currentPlayer = match.getPlayers().get(match.getCurrentPlayer());
        spawnablePlayers =match.getPlayers().stream()
                .filter(p -> p.getAlive() == ThreeState.FALSE)
                .collect(Collectors.toList());
        if(spawnablePlayers.isEmpty())
            startTurn();
        else
            startSpawning();
    }

    public void startSpawning(){
        //TODO fix thread coherence @fabio!
        //TODO:ask every player in spawnablePlayers to discard a powerUp
        //if the timer is over randomly spawn them?
        ExecutorService spawnerManager = Executors.newCachedThreadPool();
        for(Player p: spawnablePlayers){
            spawnerManager.execute(new Spawner(p,match.getBoard()));
        }
        spawnerManager.shutdown();
        try{
            spawnerManager.awaitTermination(1, TimeUnit.MINUTES);
        }catch(InterruptedException e){
            spawnerManager.shutdownNow();
            for(Player p: spawnablePlayers){
                if(p.getAlive() == ThreeState.FALSE){
                    PowerUp discarded = p.getPowerUps().get(random.nextInt(p.getPowerUps().size()));
                    p.getPowerUps().remove(discarded);
                    p.setTile(match.getBoard().getTiles().stream()
                            .flatMap(Collection::stream)
                            .filter(Tile::isSpawn)
                            .filter(t->t.getRoom() == Color.valueOf(discarded.getDiscardAward().name()))
                            .findFirst().orElse(null));
                    p.setAlive(ThreeState.TRUE);
                }
            }
        }
    }

    @Override
    public void updateOnStopSelection(boolean reverse, boolean skip){
        currentPlayer.getVirtualView().getRequestDispatcher().clear();
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
