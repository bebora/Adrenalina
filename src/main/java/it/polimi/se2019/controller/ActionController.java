package it.polimi.se2019.controller;

import it.polimi.se2019.GameProperties;
import it.polimi.se2019.model.*;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.SubAction;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.SelectableOptions;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.se2019.controller.ReceivingType.STOP;
import static it.polimi.se2019.controller.ReceivingType.WEAPON;
import static it.polimi.se2019.model.ThreeState.OPTIONAL;
import static it.polimi.se2019.model.actions.SubAction.GRAB;
import static it.polimi.se2019.model.actions.SubAction.RELOAD;

/**
 * Handles the flow for an action.
 * Supports MOVE, SHOT, GRAB, and RELOAD.
 * At the start, clones the match into {@link #sandboxMatch} to assure the player completes the full action before modifying the {@link #originalMatch}
 */
public class ActionController extends Observer {
    private GameController gameController;

    /**
     * Original match, kept without changes till the conclusion of an action.
     */
    private Match originalMatch;
    /**
     * Sandbox match, created when an action starts.
     * The original match gets restored with the changes once the action completes.
     */
    private Match sandboxMatch;
    private WeaponController weaponController;
    /**
     * Current computing {@link SubAction}, part of the {@link #curAction}
     */
    private SubAction curSubAction;
    private Action curAction;
    /**
     * Index of the current executing SubAction, used to keep track of the state.
     */
    private int subActionIndex = 0;
    private Player curPlayer;
    private Weapon selectedWeapon;
    private List<Ammo> stillToPay = new ArrayList<>();
    private TimerConstrainedEventHandler timerConstrainedEventHandler;
    private AcceptableTypes acceptableTypes;
    private Weapon toDiscard;

    public ActionController(Match match,GameController gameController){
        originalMatch = match;
        curPlayer = originalMatch.getPlayers().get(originalMatch.getCurrentPlayer());
        this.gameController = gameController;
        acceptableTypes = new AcceptableTypes(new ArrayList<>());
    }

    @Override
    public Match getMatch() {
        return sandboxMatch;
    }

    /**
     * Handles receiving the chosen action from the player.
     * Once called, it computes the action till the completion, using a sandboxed match to keep the model untouched until conclusion.
     * @param action chosen by the {@link #curPlayer}
     */
    @Override
    public void updateOnAction(Action action){
        cloneMatch();
        if(curPlayer.getActions().contains(action)) {
            curPlayer = sandboxMatch.getPlayers().get(sandboxMatch.getCurrentPlayer());
            curAction = action;
            nextStep();
        }
        else {
            curPlayer.getVirtualView().getViewUpdater().sendPopupMessage("You can't the selected action!");
        }
    }

    /**
     * Handles receiving the chosen weapon from the player.
     * Supported in SHOT, GRAB, RELOAD subAction.
     * <li>In the SHOT action, uses the {@link #weaponController} to process the selected weapon.</li>
     * <li>In the GRAB or RELOAD action, uses the PaymentController to start the payment.</li>
     * @param weapon
     */
    @Override
    public void updateOnWeapon(Weapon weapon){
        if (curSubAction == GRAB) {
            if (curPlayer.getWeapons().size() == Integer.parseInt(GameProperties.getInstance().getProperty("max_weapons"))) {
                sandboxMatch.updatePopupViews(String.format("%s discard %s!",
                        curPlayer.getUsername(),
                        weapon.getName()));
                subActionIndex--;
                toDiscard = weapon;
                curPlayer.getWeapons().remove(toDiscard);
                sandboxMatch.updateViews();
                nextStep();
            }
            else {
                sandboxMatch.updatePopupViews(String.format("%s draw %s!",
                        curPlayer.getUsername(),
                        weapon.getName()));
                //Add the discarded weapon to the Tile
                if (toDiscard != null) {
                    curPlayer.getTile().addWeapon(toDiscard);
                }
                toDiscard = null;
                selectedWeapon = weapon;
                stillToPay.add(weapon.getCost().get(0));
                startPayingProcess();
            }
        } else if (curSubAction == SubAction.SHOOT) {
            sandboxMatch.updatePopupViews(String.format("%s use %s!",
                    curPlayer.getUsername(),
                    weapon.getName()));
            weaponController = new WeaponController(sandboxMatch, weapon, originalMatch.getPlayers(), this);
        } else if (curSubAction == RELOAD && curPlayer.getWeapons().contains(weapon)) {
            selectedWeapon = weapon;
            stillToPay.addAll(weapon.getCost());
            startPayingProcess();
        }
    }

    /**
     * Handles the selection of tiles from the Client.
     * Supported in MOVE action.
     * @param tiles
     */
    @Override
    public void updateOnTiles(List<Tile> tiles){
        if (curSubAction == SubAction.MOVE && !tiles.isEmpty()) {
            Tile tile = tiles.get(0);
            curPlayer.setTile(tile);
            nextStep();
        }
    }

    /**
     * Handles the cloning of the match, when the action starts.
     * The original get restored, incorporating changes in the sandBox match, when the action is concluded or arrived at a conclusion checkpoint.
     */
    private void cloneMatch(){
        if(originalMatch.getSpawnPoints().isEmpty()){
            sandboxMatch = new NormalMatch(originalMatch);
        }else{
            sandboxMatch = new DominationMatch(originalMatch);
        }
        // Set event helper for sandbox players
        sandboxMatch.getPlayers().stream().filter(p -> p.getVirtualView() != null && p.getVirtualView().getRequestDispatcher() != null).forEach(p -> p.getVirtualView().getRequestDispatcher().setEventHelper(sandboxMatch, p));
    }

    /**
     * Process a move, prompting the {@link #curPlayer} with the possible Tiles to move on.
     * If the selectable Tiles size are unique, the choice is made automatically.
     * If the selectable Tiles are empty, the next step is made.
     */
    private void nextMove() {
        List <ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.TILES));
        List<Tile> selectableTiles = new ArrayList<>(sandboxMatch.getBoard().reachable(curPlayer.getTile(), 0, curAction.getMovements(), false));
        // Compute tiles that can be used to grab ammos or weapons.
        if (curAction.getSubActions().size() > subActionIndex && curAction.getSubActions().get(subActionIndex) == GRAB) {
            selectableTiles.removeIf(t -> !t.isSpawn() && t.getAmmoCard() == null);
            selectableTiles.removeIf(t -> t.isSpawn() && t.getWeapons().stream().noneMatch(weapon -> curPlayer.checkForAmmos(weapon.getCost())));
        }
        selectableTiles.removeIf(Objects::isNull);
        if (selectableTiles.size() == 1) {
            updateOnTiles(selectableTiles);
        }
        else if (!selectableTiles.isEmpty()) {
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1, 1, "Select a tile to move!"));
            timerConstrainedEventHandler = new TimerConstrainedEventHandler(
                    this,
                    curPlayer.getVirtualView().getRequestDispatcher(),
                    acceptableTypes);
            timerConstrainedEventHandler.start();
        }
        else {
            curPlayer.getVirtualView().getViewUpdater().sendPopupMessage("You can't move nowhere!");
            nextStep();
        }
    }

    /**
     * Compute a GRAB action
     * Supports:
     * <li>Grabbing a weapon, discarding a weapon if weapons are already maxed out according to the defined limit, if the Tile is a spawnTile</li>
     * <li>Grabbing an AmmoCard from the tile</li>
     *
     */
    private void nextGrab() {
        String prompt;
        List<ReceivingType> receivingTypes;
        List<Weapon> selectableWeapon;
        if (curPlayer.getTile().isSpawn()) {
            receivingTypes = new ArrayList<>(Arrays.asList(WEAPON));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            //Grab weapon
            if (curPlayer.getWeapons().size() < Integer.parseInt(GameProperties.getInstance().getProperty("max_weapons"))) {
                selectableWeapon = curPlayer.
                        getTile().
                        getWeapons().
                        stream().
                        filter(p -> curPlayer.checkForAmmos(Collections.singletonList(p.getCost().get(0)))). // Filter costly weapons
                        collect(Collectors.toList());
                prompt = "Select a weapon to grab!";
            } //Discard weapon first, then grab
            else {
                selectableWeapon = curPlayer.
                        getWeapons();
                prompt = "Select a weapon to discard!";
            }
            if (selectableWeapon.isEmpty()) {
                nextStep();
            } else {
                acceptableTypes.setSelectableWeapons(new SelectableOptions<>(selectableWeapon, 1, 1, prompt));
                timerConstrainedEventHandler = new TimerConstrainedEventHandler(
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        acceptableTypes);
                timerConstrainedEventHandler.start();
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
    }

    /**
     * Handles a generic step, parsing the current subAction from {@link #curAction}.
     * Supports the subActions:
     * <li>Move, using the related method {@link #nextMove()}</li>
     * <li>Shoot, prompting the weapon to use to the client, using the WeaponController</li>
     * <li>Grab, using the related method {@link #nextGrab}</li>
     * <li>Reload, </li>
     *
     */
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
                    // Ask what weapon to choose, or reset if no weapon can be chose
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
                        timerConstrainedEventHandler = new TimerConstrainedEventHandler(
                                this,
                                curPlayer.getVirtualView().getRequestDispatcher(),
                                acceptableTypes);
                        timerConstrainedEventHandler.start();
                    }
                    break;
                case GRAB:
                    nextGrab();
                    break;
                case RELOAD:
                    receivingTypes = new ArrayList<>(Arrays.asList(WEAPON, STOP));
                    selectableWeapon = curPlayer.
                            getWeapons().
                            stream().
                            filter(w -> !w.getLoaded() && curPlayer.checkForAmmos(w.getCost())).
                            collect(Collectors.toList());
                    //If the current action is RELOAD, then it must be the last action in the turn.
                    if (selectableWeapon.isEmpty()) {
                        if (curAction.toString().equals("RELOAD"))
                            updateOnStopSelection(OPTIONAL);
                        else
                            nextStep();
                    }else {
                        acceptableTypes = new AcceptableTypes(receivingTypes);
                        acceptableTypes.setSelectableWeapons(new SelectableOptions<>(selectableWeapon, 1, 1, "Choose what weapon to reload if you want!"));
                        acceptableTypes.setStop(true, "Stop reloading");
                        timerConstrainedEventHandler = new TimerConstrainedEventHandler(
                                this,
                                curPlayer.getVirtualView().getRequestDispatcher(),
                                acceptableTypes);
                        timerConstrainedEventHandler.start();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Handles the start of the payment process, using the PaymentController.
     * Doesn't support "ANY" keyword.
     */
    private void startPayingProcess(){
        PaymentController paymentController = new PaymentController(this, stillToPay, curPlayer);
        paymentController.startPaying();
    }

    /**
     * After the payment is being made, parse the current subAction to process it, and calls nextStep.
     */
    @Override
    public void concludePayment(){
        sandboxMatch.updateViews();
        if(curSubAction == GRAB){
            curPlayer.addWeapon(curPlayer.getTile().grabWeapon(selectedWeapon));
            sandboxMatch.updateViews();
            nextStep();
        }
        else if(curSubAction == SubAction.SHOOT){
            selectedWeapon.setLoaded(true);
            nextStep();
        }
        else if (curSubAction == RELOAD) {
            curPlayer.reload(selectedWeapon);
            sandboxMatch.restoreMatch(originalMatch);
            nextStep();
        }
    }

    /**
     * It handles the conclusion of an action, or a {@link WeaponController} compute.
     * Passes to the next step.
     */
    @Override
    public void updateOnConclusion(){
        weaponController = null;
        nextStep();
    }

    /**
     * Handles the lack of a choice from the player.
     * No non-reverse stop are supported  in ActionController.
     * @param skip
     */
    @Override
    public void updateOnStopSelection(ThreeState skip){
        if (skip.toBoolean() || acceptableTypes.isReverse()) {
            if (selectedWeapon != null)
                selectedWeapon.getEffects().forEach(e -> e.setActivated(false));
            originalMatch.getPlayers().stream().filter(p -> p.getVirtualView() != null && p.getVirtualView().getRequestDispatcher() != null).forEach(p -> p.getVirtualView().getRequestDispatcher().setEventHelper(originalMatch, p));
            originalMatch.updateViews();
            gameController.updateOnStopSelection((acceptableTypes != null)?skip.compare(acceptableTypes.isReverse()):skip);
        }
    }

    public WeaponController getWeaponController(){
        return weaponController;
    }



    public void setOriginalMatch(Match originalMatch) {
        this.originalMatch = originalMatch;
    }

    public Match getOriginalMatch() {
        return originalMatch;
    }
}
