package it.polimi.se2019.controller;

import it.polimi.se2019.MyProperties;
import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.SubAction;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.SelectableOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.se2019.controller.ReceivingType.STOP;
import static it.polimi.se2019.controller.ReceivingType.WEAPON;
import static it.polimi.se2019.model.ThreeState.OPTIONAL;
import static it.polimi.se2019.model.actions.SubAction.GRAB;
import static it.polimi.se2019.model.actions.SubAction.RELOAD;

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
    private AcceptableTypes acceptableTypes;

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
            curPlayer.getVirtualView().getViewUpdater().sendPopupMessage("You can't the selected action!");
        }
    }

    @Override
    public void updateOnWeapon(Weapon weapon){
        if (acceptableTypes.getSelectableWeapons().checkForCoherency(Collections.singletonList(weapon))) {
            curPlayer.getVirtualView().getRequestDispatcher().clear();
            if (curSubAction == GRAB) {
                if (curPlayer.getWeapons().size() == Integer.parseInt(MyProperties.getInstance().getProperty("max_weapons"))) {
                    subActionIndex--;
                    curPlayer.getWeapons().remove(weapon);
                    curPlayer.getTile().addWeapon(weapon);
                    sandboxMatch.updateViews();
                    nextStep();
                }
                else {
                    selectedWeapon = weapon;
                    stillToPay.add(weapon.getCost().get(0));
                    startPayingProcess();
                }
            } else if (curSubAction == SubAction.SHOOT) {
                curPlayer.getVirtualView().getRequestDispatcher().clear();
                weaponController = new WeaponController(sandboxMatch, weapon, originalMatch.getPlayers(), this);
            } else if (curSubAction == RELOAD && curPlayer.getWeapons().contains(weapon)) {
                curPlayer.getVirtualView().getRequestDispatcher().clear();
                selectedWeapon = weapon;
                stillToPay.addAll(weapon.getCost());
                startPayingProcess();
            }
        }
        else {
            throw new IncorrectEvent("Incorrect weapon choice!");
        }
    }

    @Override
    public void updateOnTiles(List<Tile> tiles){
        if (acceptableTypes.getSelectableTileCoords().checkForCoherency(tiles)) {
            if (curSubAction == SubAction.MOVE && !tiles.isEmpty()) {
                Tile tile = tiles.get(0);
                if (originalMatch.getBoard().reachable(curPlayer.getTile(), 0, curAction.getMovements(), false).contains(tile)) {
                    curPlayer.getVirtualView().getRequestDispatcher().clear();
                    curPlayer.setTile(tile);
                    nextStep();
                } else {
                    curPlayer.getVirtualView().getViewUpdater().sendPopupMessage("You can't reach the selected tile!");
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
        // Set event helper for sandbox players
        sandboxMatch.getPlayers().stream().filter(p -> p.getVirtualView() != null && p.getVirtualView().getRequestDispatcher() != null).map(p -> p.getVirtualView().getRequestDispatcher()).forEach(rq -> rq.setEventHelper(sandboxMatch));
    }

    private void nextMove() {
        List <ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.TILES));
        List<Tile> selectableTiles = new ArrayList<>(sandboxMatch.getBoard().reachable(curPlayer.getTile(), 0, curAction.getMovements(), false));
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
        if (selectableTiles.size() == 1) {
            updateOnTiles(selectableTiles);
        }
        else if (!selectableTiles.isEmpty()) {
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1, 1, "Seleziona una Tile dove muoverti!"));
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                    this,
                    curPlayer.getVirtualView().getRequestDispatcher(),
                    acceptableTypes);
            timerCostrainedEventHandler.start();
        }
        else {
            curPlayer.getVirtualView().getViewUpdater().sendPopupMessage("You can't move nowhere!");
            nextStep();
        }
    }

    private void nextStep(){
        if(subActionIndex == curAction.getSubActions().size()){
            sandboxMatch.restoreMatch(originalMatch);
            gameController.updateOnConclusion();
        }else{
            List<Weapon> selectableWeapon;
            curSubAction = curAction.getSubActions().get(subActionIndex);
            List<ReceivingType> receivingTypes;
            subActionIndex++;
            switch(curSubAction){
                case MOVE:
                    nextMove();
                    break;

                case SHOOT:
                    receivingTypes = new ArrayList<>(Arrays.asList(WEAPON));
                    acceptableTypes = new AcceptableTypes(receivingTypes);
                    selectableWeapon = curPlayer.getWeapons().stream().filter(Weapon::getLoaded).collect(Collectors.toList());
                    acceptableTypes.setSelectableWeapons(new SelectableOptions<>(selectableWeapon, 1, 1, "Seleziona un'arma!"));
                    if (selectableWeapon.isEmpty()) {
                        nextStep();
                    }
                    else if (selectableWeapon.size() == 1) {
                        updateOnWeapon(selectableWeapon.get(0));
                    }
                    else {
                        timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                                this,
                                curPlayer.getVirtualView().getRequestDispatcher(),
                                acceptableTypes);
                        timerCostrainedEventHandler.start();
                    }
                    break;
                case GRAB:
                    String prompt;
                    if (curPlayer.getTile().isSpawn()) {
                        receivingTypes = new ArrayList<>(Arrays.asList(WEAPON));
                        acceptableTypes = new AcceptableTypes(receivingTypes);
                        if (curPlayer.getWeapons().size() < Integer.parseInt(MyProperties.getInstance().getProperty("max_weapons"))) {
                            selectableWeapon = curPlayer.
                                    getTile().
                                    getWeapons().
                                    stream().
                                    filter(p -> curPlayer.checkForAmmos(Arrays.asList(p.getCost().get(0)), curPlayer.totalAmmoPool())).
                                    collect(Collectors.toList());
                            prompt = "Select a weapon to grab!";
                        } else {
                            selectableWeapon = curPlayer.
                                    getWeapons();
                            prompt = "Select a weapon to discard!";
                        }
                        if (selectableWeapon.isEmpty()) {
                            nextStep();
                        } else {
                            acceptableTypes.setSelectableWeapons(new SelectableOptions<>(selectableWeapon, 1, 0, prompt));
                            timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                                    this,
                                    curPlayer.getVirtualView().getRequestDispatcher(),
                                    acceptableTypes);
                            timerCostrainedEventHandler.start();
                        }
                    }
                    else {
                        AmmoCard ammoCard = curPlayer.getTile().grabAmmoCard();
                        List<Ammo> ammos = ammoCard.getAmmos().stream().
                                filter(a -> !a.equals(Ammo.POWERUP)).
                                collect(Collectors.toList());
                        List<Ammo> powerUps = ammoCard.getAmmos().stream().
                                filter(a -> a.equals(Ammo.POWERUP)).
                                collect(Collectors.toList());
                        ammos.forEach(a -> curPlayer.addAmmo(a));
                        powerUps.forEach(p -> curPlayer.addPowerUp(sandboxMatch.getBoard().drawPowerUp(), true));
                        sandboxMatch.getBoard().discardAmmoCard(ammoCard);
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
                    if (selectableWeapon.isEmpty()) {
                        updateOnStopSelection(OPTIONAL);
                    }else {
                        acceptableTypes = new AcceptableTypes(receivingTypes);
                        acceptableTypes.setSelectableWeapons(new SelectableOptions<>(selectableWeapon, 1, 0, "Ricarica un'arma se vuoi"));
                        timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                                this,
                                curPlayer.getVirtualView().getRequestDispatcher(),
                                acceptableTypes);
                        timerCostrainedEventHandler.start();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private void startPayingProcess(){
        List<ReceivingType> receivingTypes;
        List<PowerUp> selectablePowerUps;
        if(curPlayer.checkForAmmos(stillToPay,curPlayer.totalAmmoPool())){
            // if player can use powerup to pay some ammos
            if(curPlayer.canDiscardPowerUp(stillToPay)){
                for(Ammo a: stillToPay)
                    curPlayer.getAmmos().remove(a);
                receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP));
                selectablePowerUps = curPlayer.
                        getPowerUps().
                        stream().
                        filter(p -> stillToPay.contains(p.getDiscardAward())).collect(Collectors.toList());
                acceptableTypes = new AcceptableTypes(receivingTypes);
                stillToPay = new ArrayList<>();
                sandboxMatch.updateViews();
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps,selectablePowerUps.size(),0,"If you want, select a PowerUp!"));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        acceptableTypes);
                timerCostrainedEventHandler.start();
            }
            // if player HAVE to use powerup to pay for some ammos
            else if (!curPlayer.checkForAmmos(stillToPay,curPlayer.getAmmos())) {
                for(Ammo a: curPlayer.getAmmos()){
                    if(stillToPay.remove(a))
                        curPlayer.getAmmos().remove(a);
                }for(Ammo a: stillToPay)
                    curPlayer.getAmmos().remove(a);
                receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP));
                selectablePowerUps = curPlayer.
                        getPowerUps().
                        stream().
                        filter(p -> stillToPay.contains(p.getDiscardAward())).collect(Collectors.toList());
                sandboxMatch.updateViews();
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps,selectablePowerUps.size(),0,"You MUST select a PowerUp, my dear!"));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        acceptableTypes);
                timerCostrainedEventHandler.start();
            }
            else {
                for(Ammo a: stillToPay)
                    curPlayer.getAmmos().remove(a);
                concludePayment();
            }
        }else{
            curPlayer.getVirtualView().getViewUpdater().sendPopupMessage("You don't have enough ammos!");
        }
    }

    public void concludePayment(){
        if(curSubAction == GRAB){
            curPlayer.addWeapon(curPlayer.getTile().grabWeapon(selectedWeapon));
            selectedWeapon.setLoaded(false);
            sandboxMatch.updateViews();
            nextStep();
        }
        else if(curSubAction == SubAction.SHOOT){
            selectedWeapon.setLoaded(true);
            nextStep();
        }
        else if (curSubAction == RELOAD) {
            curPlayer.reload(selectedWeapon);
            nextStep();
        }
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard){
        if (acceptableTypes.getSelectablePowerUps().checkForCoherency(powerUps) && powerUps.stream().map(PowerUp::getDiscardAward).collect(Collectors.toList()).containsAll(stillToPay)) {
            powerUps.forEach(p -> curPlayer.discardPowerUp(p, true));
            for (Ammo a : curPlayer.getAmmos()) {
                if (stillToPay.remove(a))
                    curPlayer.getAmmos().remove(a);
            }
            if (stillToPay.isEmpty()) {
                curPlayer.getVirtualView().getRequestDispatcher().clear();
                concludePayment();
            }
            else {
                assert false;
                throw new IncorrectEvent("PowerUps are not enough! Try again!");
            }
        }
    }

    public void updateOnConclusion(){
        weaponController = null;
        nextStep();
    }

    @Override
    public void updateOnStopSelection(ThreeState skip){
        curPlayer.getVirtualView().getRequestDispatcher().clear();
        if (skip.toBoolean() || acceptableTypes.isReverse()) {
            originalMatch.updateViews();
            gameController.updateOnStopSelection(skip.compare(acceptableTypes.isReverse()));
        }
    }

    public WeaponController getWeaponController(){
        return weaponController;
    }
}
