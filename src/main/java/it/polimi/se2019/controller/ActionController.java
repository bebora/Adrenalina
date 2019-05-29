package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.model.DominationMatch;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.SubAction;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.se2019.controller.ReceivingType.STOP;
import static it.polimi.se2019.controller.ReceivingType.WEAPON;
import static it.polimi.se2019.model.actions.SubAction.GRAB;

//TODO: substitute comments with actual methods to communicate with the view
public class ActionController extends Observer {
    private GameController gameController;
    private Match originalMatch;
    private Match sandboxMatch;
    private WeaponController weaponController;
    private SubAction curSubAction;
    private Action curAction;
    private int subActionIndex = 0;
    private Player curPlayer;
    private Weapon selectedWeapon;
    private List<Ammo> stillToPay = new ArrayList<>();
    private TimerCostrainedEventHandler timerCostrainedEventHandler;

    public ActionController(Match match,GameController gameController){
        originalMatch = match;
        curPlayer = originalMatch.getPlayers().get(originalMatch.getCurrentPlayer());
        this.gameController = gameController;
    }

    @Override
    public void updateOnAction(Action action){
        cloneMatch();
        if(curPlayer.getActions().contains(action)) {
            curPlayer.getVirtualView().getRequestDispatcher().clear();
            curPlayer = sandboxMatch.getPlayers().get(sandboxMatch.getCurrentPlayer());
            curAction = action;
            nextStep();
        }
        else {
            //TODO send update wrong action selected! try again!
        }
    }

    @Override
    public void updateOnWeapon(Weapon weapon){
        if (selectableWeapon.contains(weapon)) {
            if (curSubAction == GRAB) {
                if (curPlayer.getTile().getWeapons().contains(weapon)) {
                    curPlayer.getVirtualView().getRequestDispatcher().clear();
                    selectedWeapon = weapon;
                    stillToPay.add(weapon.getCost().get(0));
                    startPayingProcess();
                } else {
                    //TODO send update impossible weapon selection
                }
            } else if (curSubAction == SubAction.SHOOT) {
                curPlayer.getVirtualView().getRequestDispatcher().clear();
                weaponController = new WeaponController(sandboxMatch, weapon, originalMatch.getPlayers(), this);
            } else if (curSubAction == SubAction.RELOAD && curPlayer.getWeapons().contains(weapon)) {
                curPlayer.getVirtualView().getRequestDispatcher().clear();
                selectedWeapon = weapon;
                stillToPay.addAll(weapon.getCost());
                startPayingProcess();
            }
        }
    }

    @Override
    public void updateOnTiles(List<Tile> tiles){
        if (selectableTiles.containsAll(tiles)) {
            if (curSubAction == SubAction.MOVE && !tiles.isEmpty()) {
                Tile tile = tiles.get(0);
                if (originalMatch.getBoard().reachable(curPlayer.getTile(), 0, curAction.getMovements(), false).contains(tile)) {
                    curPlayer.getVirtualView().getRequestDispatcher().clear();
                    curPlayer.setTile(tile);
                    nextStep();
                } else {
                    //TODO SEND UPDATE he can't reach that tile
                }
            }
        }
    }

    private void cloneMatch(){
        if(originalMatch.getSpawnPoints() == null){
            sandboxMatch = new NormalMatch(originalMatch);
        }else{
            sandboxMatch = new DominationMatch(originalMatch);
        }
    }

    private void nextStep(){
        if(subActionIndex == curAction.getSubActions().size()){
            sandboxMatch.restoreMatch(originalMatch);
            gameController.updateOnConclusion();
        }else{
            curSubAction = curAction.getSubActions().get(subActionIndex);
            List<ReceivingType> receivingTypes;
            subActionIndex++;
            switch(curSubAction){
                case MOVE:
                    receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.TILES, ReceivingType.STOP));
                    selectableTiles = new ArrayList<>(sandboxMatch.getBoard().reachable(curPlayer.getTile(), 0, curAction.getMovements(), false));
                    if (curAction.getSubActions().size() > subActionIndex && curAction.getSubActions().get(subActionIndex) == GRAB) {
                        selectableTiles.removeAll(sandboxMatch.
                                getBoard().
                                getTiles().
                                stream().
                                flatMap(List::stream).
                                filter(t -> t != null && (!t.isSpawn() && t.getAmmoCard() == null)).
                                collect(Collectors.toList()));
                        for (Tile t : sandboxMatch.getBoard().getTiles().stream().flatMap(List::stream).filter(t -> t != null && t.isSpawn()).collect(Collectors.toList())) {
                            boolean toRemove = true;
                            for (Weapon weapon : t.getWeapons()) {
                                if (curPlayer.checkForAmmos(weapon.getCost(), curPlayer.totalAmmoPool()))
                                    toRemove = false;
                            }
                            if (toRemove)
                                selectableTiles.remove(t);
                        }
                    }
                    timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
                            this,
                            curPlayer.getVirtualView().getRequestDispatcher(),
                            receivingTypes);
                    timerCostrainedEventHandler.start();
                    //TODO send update with selectable tiles
                    break;
                case SHOOT:
                    selectableWeapon = curPlayer.getWeapons().stream().filter(w -> w.getLoaded()).collect(Collectors.toList());
                    receivingTypes = new ArrayList<>(Arrays.asList(WEAPON, ReceivingType.STOP));
                    timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
                            this,
                            curPlayer.getVirtualView().getRequestDispatcher(),
                            receivingTypes);
                    timerCostrainedEventHandler.start();
                    //TODO send update with selectable weapons!
                    break;
                case GRAB:
                    if (curPlayer.getTile().isSpawn()) {
                        receivingTypes = new ArrayList<>(Arrays.asList(WEAPON, ReceivingType.STOP));
                        selectableWeapon = curPlayer.
                                getTile().
                                getWeapons().
                                stream().
                                filter(p -> curPlayer.checkForAmmos(p.getCost(), curPlayer.totalAmmoPool())).
                                collect(Collectors.toList());
                        timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
                                this,
                                curPlayer.getVirtualView().getRequestDispatcher(),
                                receivingTypes);
                        timerCostrainedEventHandler.start();
                        //TODO send update with weaponsgrabbable
                    }
                    else {
                        curPlayer.getTile().getAmmoCard().getAmmos().forEach(a -> curPlayer.addAmmo(a));
                        curPlayer.getTile().grabAmmoCard();
                        updateOnConclusion();
                    }
                    break;
                case RELOAD:
                    receivingTypes = new ArrayList<>(Arrays.asList(WEAPON, STOP));
                    selectableWeapon = curPlayer.
                            getWeapons().
                            stream().
                            filter(w -> !w.getLoaded() && curPlayer.checkForAmmos(w.getCost(), curPlayer.totalAmmoPool())).
                            collect(Collectors.toList());
                    timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
                            this,
                            curPlayer.getVirtualView().getRequestDispatcher(),
                            receivingTypes);
                    timerCostrainedEventHandler.start();
                    //TODO send update with weaponsreloadable
                    break;
                default:
                    break;
            }
        }

    }

    private void startPayingProcess(){
        List<ReceivingType> receivingTypes;
        if(curPlayer.checkForAmmos(stillToPay,curPlayer.totalAmmoPool())){
            // if player can use powerup to pay some ammos
            if(curPlayer.canDiscardPowerUp(stillToPay)){
                receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP));
                selectablePowerUps = curPlayer.
                        getPowerUps().
                        stream().
                        filter(p -> stillToPay.contains(p.getDiscardAward())).collect(Collectors.toList());
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        receivingTypes);
                timerCostrainedEventHandler.start();
                //TODO SEND UPDATE ASKING FOR POWERUPS
            }
            // if player HAVE to use powerup to pay for some ammos
            else if (!curPlayer.checkForAmmos(stillToPay,curPlayer.getAmmos())) {
                for(Ammo a: curPlayer.getAmmos()){
                    if(stillToPay.remove(a))
                        curPlayer.getAmmos().remove(a);
                }
                receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP));
                selectablePowerUps = curPlayer.
                        getPowerUps().
                        stream().
                        filter(p -> stillToPay.contains(p.getDiscardAward())).collect(Collectors.toList());
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(5,
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        receivingTypes);
                timerCostrainedEventHandler.start();
                //TODO send update asking for remaining powerups, to be chosen between discadablepowerups
            }
            else {
                for(Ammo a: stillToPay)
                    curPlayer.getAmmos().remove(a);
                concludePayment();
            }
        }else{
            //TODO send update player not enough ammos
        }
    }

    public void concludePayment(){
        if(curSubAction == GRAB){
            curPlayer.addWeapon(curPlayer.getTile().grabWeapon(selectedWeapon));
            nextStep();
        }
        else if(curSubAction == SubAction.SHOOT){
            selectedWeapon.setLoaded(true);
            nextStep();
        }
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard){
        if (selectablePowerUps.containsAll(powerUps)) {
            powerUps.forEach(p -> curPlayer.discardPowerUp(p));
            for (Ammo a : curPlayer.getAmmos()) {
                if (stillToPay.remove(a))
                    curPlayer.getAmmos().remove(a);
            }
            if (stillToPay.isEmpty()) {
                concludePayment();
                curPlayer.getVirtualView().getRequestDispatcher().clear();
            }
        }
    }

    public void updateOnConclusion(){
        weaponController = null;
        nextStep();
    }

    @Override
    public void updateOnStopSelection(boolean reverse, boolean skip){
        curPlayer.getVirtualView().getRequestDispatcher().clear();
        if (reverse) {
            gameController.updateOnStopSelection(true, skip);
        }
    }

    public WeaponController getWeaponController(){
        return weaponController;
    }
}
