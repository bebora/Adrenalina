package it.polimi.se2019.controller;

import it.polimi.se2019.Choice;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Observer;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.*;
import it.polimi.se2019.view.SelectableOptions;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static it.polimi.se2019.model.ThreeState.*;
import static it.polimi.se2019.model.cards.ActionType.DEALDAMAGE;
import static it.polimi.se2019.model.cards.ActionType.MOVE;

/**
 * This class contains all the logic related to the execution
 * of an effect after the player has chosen it.
 */
public class EffectController extends Observer {
    private Board board;
    private Player player;
    private Weapon curWeapon;
    private Match curMatch;
    private Observer controller;
    private List<Player> originalPlayers;

    private Effect curEffect;

    private Move curMove;

    private DealDamage curDealDamage;

    private ActionType curActionType;

    private Tile pointOfView;

    private List<Player> playersToMove;

    private int dealDamageIndex;

    private int moveIndex;

    private int orderIndex;

    private boolean askingForSource;

    private Player currentEnemy;
    private TimerCostrainedEventHandler timerCostrainedEventHandler;
    private AcceptableTypes acceptableTypes;
    private CountDownLatch countDownLatch;
    EffectController(Effect curEffect, Weapon weapon,Match match,Player player,List<Player> originalPlayers, Observer controller){
        this.curMatch = match;
        this.curEffect = curEffect;
        this.moveIndex = -1;
        this.orderIndex = -1;
        this.dealDamageIndex = -1;
        this.curWeapon = weapon;
        this.player = player;
        this.board = match.getBoard();
        this.playersToMove = new ArrayList<>();
        this.originalPlayers = originalPlayers;
        this.controller = controller;
        acceptableTypes = new AcceptableTypes(new ArrayList<>());
    }

    /**
     * Read the next ActionType to be executed and
     * call the method to check if the effect needs a Direction.
     * If there is no next ActionType prepare clean the EffectController
     * for a new input.
     */
     void nextStep(){
        playersToMove = new ArrayList<>();
        orderIndex+=1;
        if(orderIndex < curEffect.getOrder().size()) {
            curActionType = curEffect.getOrder().get(orderIndex);
            if(curActionType == MOVE) {
                moveIndex += 1;
                curMove = curEffect.getMoves().get(moveIndex);
                if(curMove.getTargetSource() != null && curMove.getTargetSource().getVisibility() != null && curMove.getObjectToMove().equals(ObjectToMove.TARGETSOURCE))
                    processDirection(curMove.getTargetSource());
                else
                    processDirection(curMove.getTargetDestination());
            }else{
                dealDamageIndex += 1;
                curDealDamage = curEffect.getDamages().get(dealDamageIndex);
                processDirection(curDealDamage.getTarget());
            }
        }
        else{
            //Unit test purposes check
            if (controller != null)
                controller.updateOnConclusion();
        }
    }

    /**
     * Check the value of the current ActionType
     * and call the proper method to prepare for user input
     */
    private void processStep(){
        if(curActionType == MOVE)
            processMove();
        else
            processDealDamage();
    }

    /**
     * If the current value is null memorize the new value,
     * otherwise tell the user to send another Direction
     * @param direction the Direction in which the effect is applied
     * @see Direction
     */
    @Override
    public void updateOnDirection(Direction direction){
        player.getVirtualView().getRequestDispatcher().clear();
        curEffect.setDirection(direction);
        processStep();
    }

    /**
     * players is a list of players provided from the user to which the
     * current Move or DealDamage is applied.
     * @param originalPlayers a List of Player to which the current subeffect is applied
     */
    @Override
    public void updateOnPlayers(List<Player> originalPlayers){
        Target target;
        List<Player> players = getSandboxPlayers(originalPlayers);
        player.getVirtualView().getRequestDispatcher().clear();
        if (curActionType == MOVE) {
            target = curMove.getTargetSource();
        } else {
            target = curDealDamage.getTarget();
        }
        //Additional check for different square players
        if (target.checkDifferentSquare(players)) {
            if (curActionType == MOVE && askingForSource) {
                updateMoveOnPlayers(players);
            } else {
                updateDealDamageOnPlayers(players);
            }
        }
        //Restart the timer at the time it stopped if the check fails
        else {
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(timerCostrainedEventHandler);
            timerCostrainedEventHandler.start();
        }
    }

    /**
     * If the current sub effect is Move tiles contains a single Tile
     * to which the selected objects must be moved.
     * If the current sub effect is DealDamage tiles contains
     * the targets for the area damage.
     * @param tiles a target for Move(must contain a single tile) or DealDamage
     * @see Tile
     */
    @Override
    public void updateOnTiles(List<Tile> tiles){
        player.getVirtualView().getRequestDispatcher().clear();
        if(curActionType == MOVE) {
            if(curMove.getObjectToMove() != ObjectToMove.PERSPECTIVE)
                playersToMove.forEach(p -> p.setTile(tiles.get(0)));
            else
                player.setPerspective(tiles.get(0));
            handleTargeting(curMove.getTargeting(), playersToMove);
            nextStep();
        }
        else if (curActionType == DEALDAMAGE) {
            List<Player> temp = tiles.stream()
                    .map(t -> curMatch.getPlayersInTile(t))
                    .flatMap(List::stream)
                    .peek(p -> p.receiveShot(getOriginalPlayer(player), curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount(), true))
                    .collect(Collectors.toList());
            temp.removeIf(p -> p.getUsername().equals(player.getUsername()));
            handleTargeting(curDealDamage.getTargeting(),temp);
            checkPowerUps(temp);
            nextStep();
        }
    }

    /**
     * Checks if the room is valid and apply the current DealDamage
     * to all the Player in the room (but the player itself). If the room is not a valid target
     * signals the mistake to the player.
     * Only check for using samePlayerRoom and Visibility filters.
     * @param room the color of the target room
     */
    @Override
    public void updateOnRoom(Color room){
        List<Player> possibleTargets = curMatch.getPlayersInRoom(room);
        possibleTargets.removeIf(p -> p.getUsername().equals(player.getUsername()));
        player.getVirtualView().getRequestDispatcher().clear();
        possibleTargets.forEach(p -> p.receiveShot(getOriginalPlayer(player),curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount(), true));
        handleTargeting(curDealDamage.getTargeting(),possibleTargets);
        checkPowerUps(possibleTargets);
        nextStep();
    }

    /**
     * Handles receiving a stop from the client.
     * No non-reverting stops are accepted into the {@link EffectController}, so it always reverts the action
     * @param skip
     */
    @Override
    public void updateOnStopSelection(ThreeState skip){
        player.getVirtualView().getRequestDispatcher().clear();
        controller.updateOnStopSelection(skip);
    }

    /**
     * Ask the player for a Direction if the current target requires one,
     * otherwise go on with the effect
     * @param target the target of the current subeffect
     */
    private void processDirection(Target target){
        if ((target.getCardinal() == TRUE || target.getCardinal() == ThreeState.FALSE) && curEffect.getDirection() == null) {
            List<ReceivingType> receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.DIRECTION));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            List<Direction> directions = Arrays.asList(Direction.values());
            acceptableTypes.setSelectableDirections(new SelectableOptions<>(directions, 1,1,"Select a direction!"));
            timerCostrainedEventHandler = new TimerCostrainedEventHandler( this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerCostrainedEventHandler.start();
        }
        else processStep();
    }

    /**
     * Ask the player for the proper target after checking the current Move
     * Supports:
     * <li>Moving the player perspective, an helping position for defining complex effects</li>
     * <li>Moving the player itself</li>
     * <li>Moving other players, asking the player who to move.</li>
     */
    private void processMove(){
        List<Tile> selectableTiles = new ArrayList<>();
        List<ReceivingType> receivingTypes;
        switch(curMove.getObjectToMove()){
            //Handles the move of the perspective
            case PERSPECTIVE:
                selectableTiles = tileTargets(curMove.getTargetDestination());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                askingForSource = false;
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1,1, curMove.getPrompt()));
                break;
            //Handles the move of the player itself
            case SELF:
                selectableTiles = tileTargets(curMove.getTargetDestination());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                askingForSource = false;
                playersToMove.add(player);
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1,1, curMove.getPrompt()));
                break;
            //Handles moving other targets; if they are chosen without the user input, they are automatically selected.
            //If user input is needed, it parses the movable targets using TargetSource
            case TARGETSOURCE:
                askingForSource = true;
                processTargetSource(curMove.getTargetSource());
                if (!askingForSource) {
                    selectableTiles = tileTargets(curMove.getTargetDestination());
                    //Check if no players can't be moved or no tiles can be selected
                    if (playersToMove.isEmpty() || selectableTiles.isEmpty()) {
                        if (curMove.getTargetSource().getMinTargets() == 0)
                            nextStep();
                        else
                            updateOnStopSelection(OPTIONAL);
                        return;
                    }
                    //Check if there is a spawnPlayer
                    else if (playersToMove.stream().anyMatch(Player::getDominationSpawn)) {
                        selectableTiles = Collections.singletonList(playersToMove.stream().filter(Player::getDominationSpawn).findFirst().orElseThrow(() -> new IncorrectEvent("No tiles!")).getTile());
                    }
                    receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                    acceptableTypes = new AcceptableTypes(receivingTypes);
                    acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1,1, curMove.getPrompt()));
                }
                break;
        }
        //Check if asking for source and need to handle the request to the client
        if (!askingForSource) {
            if (selectableTiles.isEmpty()) {
                updateOnStopSelection(OPTIONAL);
            }else {
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerCostrainedEventHandler.start();
            }
        }

    }
    /**
     * Ask the player for the proper target after checking the current DealDamage
     * Supports:
     * <li>Attacking every player in a {@link Tile}</li>
     * <li>Attacking every player in a Room</li>
     * <li>Attack single players</li>
     * The target gets parsed from {@link Target}, using the related Predicates.
     */
    private void processDealDamage(){
        Area targetType = curDealDamage.getTarget().getAreaDamage();
        List<ReceivingType> receivingTypes;
        int min = curDealDamage.getTarget().getMinTargets();
        int max = curDealDamage.getTarget().getMaxTargets();
        boolean skip = false;
        switch(targetType) {
            case TILE:
                List<Tile> selectableTiles = tileTargets(curDealDamage.getTarget());
                if (selectableTiles.isEmpty()) {
                    updateOnStopSelection(OPTIONAL);
                    skip = true;
                }
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, max, min, "Select the tile where you attack!"));
                break;
            case ROOM:
                List<Color> selectableRoom = board.getTiles().
                        stream().
                        flatMap(List::stream).
                        filter(curDealDamage.getTarget().getFilterRoom(board, pointOfView)).
                        map(Tile::getRoom).
                        distinct().
                        collect(Collectors.toList());
                if (selectableRoom.isEmpty()) {
                    updateOnStopSelection(OPTIONAL);
                    skip = true;
                }
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.ROOM));
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableRooms(new SelectableOptions<>(selectableRoom, max, min, "Select a room to BOMB!"));
                break;
            case SINGLE:
                List<Player> players = playerTargets(curDealDamage.getTarget());
                if (players.isEmpty()) {
                    if (curDealDamage.getTarget().getMinTargets() == 0) {
                        nextStep();
                    }
                    else {
                        skip = true;
                        updateOnStopSelection(OPTIONAL);
                    }
                }
                else {
                    receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.PLAYERS));
                    acceptableTypes = new AcceptableTypes(receivingTypes);
                    acceptableTypes.setSelectablePlayers(new SelectableOptions<>(players, max, min, "Select players to attack!"));
        }
                break;
            default:
                break;
        }
        if (!skip) {
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerCostrainedEventHandler.start();
        }
    }


    /**
     * It filters {@code playersToFilter} with additional rules for players object of a move action.
     * It supports the move of spawnPoints, authorized only if the move into the same Tile they are.
     * @param playersToFilter filtered list
     * @param target used to filter the players
     */
    private void filterPlayers(List<Player> playersToFilter, Target target) {
        List<Player> players = playerTargets(target).stream().filter(p -> !p.getDominationSpawn()).collect(Collectors.toList());
        List<Player> spawnPoints = playerTargets(target.getMoveDominationTarget()).stream().filter(Player::getDominationSpawn).collect(Collectors.toList());
        players.addAll(spawnPoints);
        players.remove(player);
        playersToFilter.retainAll(players);
        Set<Player> playerSet = new HashSet<>(playersToFilter);
        playersToFilter.clear();
        playersToFilter.addAll(playerSet);
    }

    /**
     * Checks the current target and select the players to be moved if no input is needed
     * @param target the current Move target
     */
    private void processTargetSource(Target target){
        if(target.getMaxTargets() == 0 && target.getCheckTargetList() == TRUE){
            askingForSource = false;
            playersToMove = new ArrayList<>(curWeapon.getTargetPlayers());
            filterPlayers(playersToMove, target);
        }
        else if(target.getMaxTargets() == 0 && target.getCheckBlackList() == TRUE){
            askingForSource = false;
            playersToMove = new ArrayList<>(curWeapon.getBlackListPlayers());
            filterPlayers(playersToMove, target);
        }
        else {
            List<Player> players = new ArrayList<>(curMatch.getPlayers());
            filterPlayers(players, target);
            List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.PLAYERS));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            int min = target.getMinTargets();
            int max = target.getMaxTargets();
            if (players.isEmpty()) {
                if (target.getMinTargets() == 0)
                    nextStep();
                else {
                    player.getVirtualView().getViewUpdater().sendPopupMessage("You can't move anyone! Wrong choice mate!");
                    updateOnStopSelection(OPTIONAL);
                }
            }
            else {
                acceptableTypes.setSelectablePlayers(new SelectableOptions<>(players, max, min,curMove.getPrompt()));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerCostrainedEventHandler.start();
            }
        }
    }

    /**
     * Checks what players can be selected using the current {@code target}.
     * Checks for acceptable Players, using {@link Weapon#blackListPlayers} and {@link Weapon#targetPlayers}.
     * Checks for acceptable tiles, using {@link Target#getFilterTiles(Board, Tile, Direction)}
     * @param target
     * @return
     */
    private List<Player> playerTargets(Target target) {
        if (!checkPointOfView(target))
            return new ArrayList<>();
        List<Player> acceptablePlayer = curMatch.getPlayers().
                stream().
                filter(curWeapon!=null?target.getPlayerListFilter(player,curWeapon.getTargetPlayers(), curWeapon.getBlackListPlayers()): s->true).
                collect(Collectors.toList());
        List<Tile> acceptableTiles = curMatch.getBoard().getTiles().
                stream().flatMap(List::stream).
                filter(target.getFilterTiles(board,pointOfView, curEffect.getDirection())).
                collect(Collectors.toList());
        return acceptablePlayer.stream().
                filter(p -> acceptableTiles.contains(p.getTile()) && !p.getUsername().equals(player.getUsername())).
                collect(Collectors.toList());
    }

    /**
     * Returns true if the list of Player respects the parameters
     * specified in the Target.
     * @param target the Target to get the filters from
     * @param players a List of Player to be checked
     * @return <code>true</code> if every player satisfies the parameters
     *         <code>false</code> otherwise.
     */
    private boolean checkPlayerTargets(Target target, List<Player> players){
        boolean result;
        checkPointOfView(target);
        result = players.stream()
                         .map(Player::getTile)
                         .allMatch(target.getFilterTiles(board,pointOfView, curEffect.getDirection())) &&
                 players.stream()
                         .allMatch((curWeapon!=null)?target.getPlayerListFilter(player,curWeapon.getTargetPlayers(),curWeapon.getBlackListPlayers()): p -> true);
        return result;
    }

    /**
     * Checks what tiles can be selected, following {@code #target}.
     * @param target
     * @return list of selectable tiles
     */
    private List<Tile> tileTargets(Target target) {
        checkPointOfView(target);
        return board.getTiles().stream().
                flatMap(List::stream).
                filter(Objects::nonNull).
                filter(target.getFilterTiles(board,pointOfView,curEffect.getDirection())).
                collect(Collectors.toList());
    }
    /**
     * Return true if the tiles respect the parameters specified
     * in Target
     * @param target the Target to get the filters from
     * @param tiles the tiles to be verified
     * @return <code>true</code> if the tiles respect the filters
     *         <code>false</code> otherwise.
     */
    private boolean checkTileTargets(Target target,List<Tile> tiles){
        checkPointOfView(target);
        return tiles.stream()
                .allMatch(target.getFilterTiles(board,pointOfView,curEffect.getDirection()));
    }

    private void handleTargeting(ThreeState targeting, List<Player> players){
        if(targeting == TRUE){
            curWeapon.setTargetPlayers(players);
        }else if(targeting == FALSE)
            curWeapon.setBlackListPlayers(players);
    }

    /**
     * Check the pointOfView required by the target for the
     * current sub effect and set it accordingly.
     * @param target
     */
    private boolean checkPointOfView(Target target){
        switch(target.getPointOfView()){
            case OWN:
                pointOfView = player.getTile();
                break;
            case PERSPECTIVE:
                pointOfView = player.getPerspective();
                break;
            case LASTPLAYER:
                int size = curWeapon.getTargetPlayers().size();
                if (size != 0) {
                    Player temp = curWeapon.getTargetPlayers().get(0);
                    if (temp.getDominationSpawn())
                        return false;
                    else {
                        pointOfView = temp.getTile();
                    }
                }
                else return false;
                break;
            //case TARGET ihs already handled when askingForSource
            default:
                break;
        }
        return true;
    }

    /**
     * Checks the powerUps after a damage is dealt.
     * <li>Checks if the damaging player has {@link Moment#DAMAGING} powerUps, and if so ask whether to use them or not.</li>
     * <li>Checks if the damaged players have {@link Moment#DAMAGED} powerUps, and if so ask all of them, asynchronously, to select them.</li>
     * @param players
     */
    private void checkPowerUps(List<Player> players){
        List<ReceivingType> receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.POWERUP));
        for (Player p : players.stream().filter(p -> !p.getDominationSpawn()).collect(Collectors.toList())) {
            if (curDealDamage.getDamagesAmount() != 0 && player.hasPowerUp(Moment.DAMAGING) && !player.getAmmos().isEmpty()) {
                currentEnemy = p;
                List<PowerUp> selectablePowerUps= player.getPowerUps().stream().filter(pUp -> pUp.getApplicability().equals(Moment.DAMAGING)).collect(Collectors.toList());
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps, selectablePowerUps.size(), 0, String.format("Seleziona tra 0 e %d PowerUp!", selectablePowerUps.size())));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler( this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                countDownLatch = new CountDownLatch(1);
                timerCostrainedEventHandler.start();
                try {
                    countDownLatch.await();
                }
                catch (Exception e) {
                    Logger.log(Priority.DEBUG, "Ended handler powerup damaging");
                }
                player.getVirtualView().getRequestDispatcher().clear();
            }
            else break;
        }
        List<Player> damagedPlayers = players.stream().filter(p -> p.getOnline() && p.hasPowerUp(Moment.DAMAGED)).collect(Collectors.toList());
        countDownLatch = new CountDownLatch(damagedPlayers.size());
        for(Player p : damagedPlayers){
            List<PowerUp> applicable = p.getPowerUps().stream().filter(pUp -> pUp.getApplicability().equals(Moment.DAMAGED)).collect(Collectors.toList());
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(applicable, applicable.size(), 0, String.format("Select from 0 and %d powerUps!", applicable.size())));
            countDownLatch = new CountDownLatch(1);
            Observer damagedController = new DamagedController(countDownLatch, p, player, applicable);
            TimerCostrainedEventHandler temp = new TimerCostrainedEventHandler(damagedController,p.getVirtualView().getRequestDispatcher(), acceptableTypes);
            temp.start();
        }
        try {
            countDownLatch.await();
        }
        catch (Exception e) {
            Logger.log(Priority.DEBUG, "Ended handler powerup damaged");
        }
    }

    /**
     * Handles the move of selected players, from client input or directly from the card.
     * <li>If the possible tile is one, it automatically moves the players.</li>
     * <li>If the possible tiles are more than one, it prompt the user with the choice.</li>
     * <li>It resets the action if there are no possible tiles.</li>
     * @param players
     */
    private void updateMoveOnPlayers(List<Player> players){
        player.getVirtualView().getRequestDispatcher().clear();
        if (curMove.getTargetDestination().getPointOfView() == PointOfView.TARGET)
            pointOfView = players.get(0).getTile();
        askingForSource = false;
        playersToMove = players;
        List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.TILES));
        List<Tile> tiles = tileTargets(curMove.getTargetDestination());
        acceptableTypes = new AcceptableTypes(receivingTypes);
        if (playersToMove.stream().anyMatch(Player::getDominationSpawn)) {
            Tile spawnTile = playersToMove.stream().filter(Player::getDominationSpawn).findFirst().orElseThrow(() -> new IncorrectEvent("Error in event!")).getTile();
            tiles.removeIf(tile -> !tile.equals(spawnTile));
        }
        if (tiles.isEmpty())
            updateOnStopSelection(TRUE);
        else if (tiles.size() == 1) {
            updateOnTiles(tiles);
        }
        else {
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(tiles, 1, 1, curMove.getPrompt()));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerCostrainedEventHandler.start();
            }
    }

    /**
     * Handles shooting players chosen by client.
     * Supports:
     * <li>Shooting all players in the targetLists if {@link Target#maxTargets} is 0, without checking the user choice.</li>
     * <li>Shooting players from the user choice if conditions are checked</li>
     * Checks powerUps for different applicability.
     * @param players
     */
    private void updateDealDamageOnPlayers(List<Player> players){
        List<Player> toShoot = new ArrayList<>();
        if(curDealDamage.getTarget().getMaxTargets() == 0){
            if(curDealDamage.getTarget().getCheckTargetList() == TRUE)
                toShoot = curWeapon.getTargetPlayers();
            else if(curDealDamage.getTarget().getCheckBlackList() == TRUE)
                toShoot = curWeapon.getBlackListPlayers();
        }
        else {
            toShoot = players;
        }
        toShoot.forEach(p -> p.receiveShot(getOriginalPlayer(player),curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount(), true));
        handleTargeting(curDealDamage.getTargeting(),toShoot);
        checkPowerUps(players);
        nextStep();

    }

    /**
     * Receive a powerUp that can be used after a inflicting damage
     * and prepare the controller for executing its effect
     * Assumptions:
     * <li>Moment.damaging powerup inflict damage</li>
     * <li>Moment.damaging powerup require a {@code Ammo.ANY} ammo.
     * @param powerUps a single powerUp to be used
     *
     */
    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps){
        player.getVirtualView().getRequestDispatcher().clear();
        int damagesAmount;
        int marksAmount;
        powerUps = powerUps.stream().filter(powerUp -> player.getPowerUps().contains(powerUp) && powerUp.getApplicability() == Moment.DAMAGING).collect(Collectors.toList());
        damagingLoop: for (PowerUp p: powerUps){
            List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.AMMO, ReceivingType.STOP));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            List<PowerUp> discardablePowerUps = player.getPowerUps().stream().filter(powerUp -> !p.equals(powerUp)).collect(Collectors.toList());
            List<Ammo> ammos = player.getAmmos().stream().distinct().collect(Collectors.toList());
            if (player.canDiscardPowerUp(Collections.singletonList(Ammo.ANY))) {
                receivingTypes.add(ReceivingType.POWERUP);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(discardablePowerUps, 1, 1, "Select a powerUp to discard!"));
            }
            acceptableTypes.setSelectableAmmos(new SelectableOptions<>(ammos, 1 , 1, "Select an ammo to discard"));
            acceptableTypes.setStop(true, "Don't pay for no more powerUps!");
            Choice ammoRequest = new Choice(player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            Ammo toPay;
            switch (ammoRequest.getReceivingType()) {
                case STOP:
                    if (ammoRequest.getStop().equals(TRUE)) {
                        updateOnStopSelection(TRUE);
                        return;
                    }
                    else
                        continue damagingLoop;
                case POWERUP:
                    PowerUp powerUp = ammoRequest.getPowerUps().get(0);
                    player.discardPowerUp(powerUp, true);
                    player.getAmmos().remove(powerUp.getDiscardAward());
                    break;
                case AMMO:
                    toPay = ammoRequest.getAmmo();
                    player.getAmmos().remove(toPay);
                    break;
                default:
                    updateOnStopSelection(TRUE);
                    return;
            }
            player.discardPowerUp(p, false);
            damagesAmount = p.getEffect().getDamages().get(0).getDamagesAmount();
            marksAmount = p.getEffect().getDamages().get(0).getMarksAmount();
            currentEnemy.receiveShot(player,damagesAmount,marksAmount, false);
            if (player.getAmmos().isEmpty() && player.getPowerUps().stream().allMatch(p::equals)) {
                break;
            }
        }
        nextStep();
    }

    /**
     * Helper method to get original Players from sandboxPlayers, created to keeping the state of the original match.
     * @param sandboxPlayer
     * @return
     */
    private Player getOriginalPlayer(Player sandboxPlayer){
        return originalPlayers.stream()
                .filter(p -> p.getId().equals(sandboxPlayer.getId()))
                .findAny().orElse(null);
    }
    /**
     * Helper method to get sandboxPlayer from originalPlayer, created to keeping the state of the original match.
     * @param originalTargetPlayer
     * @return
     */
    private Player getSandboxPlayer(Player originalTargetPlayer){
        return curMatch.getPlayers().stream()
                .filter(p -> p.getId().equals(originalTargetPlayer.getId()))
                .findAny().orElse(null);
    }
    private List<Player> getSandboxPlayers(List<Player> originalTargetPlayers){
        return originalTargetPlayers.stream()
                .map(this::getSandboxPlayer)
                .collect(Collectors.toList());
    }

    public void setPlayer(Player player){this.player = player;}

}