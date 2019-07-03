package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
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

import static it.polimi.se2019.controller.ReceivingType.DIRECTION;
import static it.polimi.se2019.model.ThreeState.*;
import static it.polimi.se2019.model.cards.ActionType.DEALDAMAGE;
import static it.polimi.se2019.model.cards.ActionType.MOVE;

/**
 * This class contains all the logic related to the execution of an effect.
 * It gets called after a player has chosen it, or by the use of PowerUp.
 */
public class EffectController extends Observer {
    private Board board;
    private Player player;
    private Weapon curWeapon;
    private Match curMatch;
    /**
     * The controller that called the class.
     * It gets notified after the effect is computed.
     */
    private Observer controller;
    private List<Player> originalPlayers;

    private Effect curEffect;

    private Move curMove;

    private DealDamage curDealDamage;

    private ActionType curActionType;

    /**
     * It indicates what is the PointOfView of the {@link #curActionType}.
     */
    private Tile pointOfView;

    /**
     * Contains the player chosen by the client, or directly from the effect, that will be moved.
     * It gets used if {@link #curMove} has {@link ObjectToMove#TARGETSOURCE}.
     */
    private List<Player> playersToMove;

    private int dealDamageIndex;

    private int moveIndex;

    private int orderIndex;

    /**
     * Flag to indicate whether or not a {@link Move} with {@link ObjectToMove#TARGETSOURCE} is computing.
     */
    private boolean askingForSource;

    private Player currentEnemy;
    private TimerConstrainedEventHandler timerConstrainedEventHandler;
    private AcceptableTypes acceptableTypes;

    /**
     * Indicates whether the player choosing powerUps stopped because afk
     */
    private boolean skip = false;

    private CountDownLatch countDownLatch;

    EffectController(Effect curEffect, Weapon weapon, Match match, Player player, List<Player> originalPlayers, Observer controller) {
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

    @Override
    public Match getMatch() {
        return curMatch;
    }

    /**
     * Read the next ActionType to be executed and
     * call the method to check if the effect needs a Direction.
     * If there is no next ActionType prepare clean the EffectController
     * for a new input.
     */
    void nextStep() {
        playersToMove = new ArrayList<>();
        orderIndex += 1;
        //Checks if effect completed
        if (orderIndex < curEffect.getOrder().size()) {
            Target target;
            curActionType = curEffect.getOrder().get(orderIndex);
            if (curActionType == MOVE) {
                moveIndex += 1;
                curMove = curEffect.getMoves().get(moveIndex);
                //Check if the move need to compute the source or destination first
                if (curMove.getTargetSource() != null && curMove.getTargetSource().getVisibility() != null && curMove.getObjectToMove().equals(ObjectToMove.TARGETSOURCE))
                    target = curMove.getTargetSource();
                else
                    target = curMove.getTargetDestination();
            } else {
                dealDamageIndex += 1;
                curDealDamage = curEffect.getDamages().get(dealDamageIndex);
                target = curDealDamage.getTarget();
            }
            //Asks the player for a direction if the target needs it, block the flow if the player didn't answer
            if (!updateDirection(target))
                return;
            processStep();
        }
        //Notifies the upper level controller of conclusion.
        else {
            //Unit test purposes check
            if (controller != null)
                controller.updateOnConclusion();
        }
    }

    /**
     * Check the value of the current ActionType
     * and call the proper method to prepare for user input
     */
    private void processStep() {
        if (curActionType == MOVE)
            processMove();
        else
            processDealDamage();
    }

    /**
     * players is a list of players provided from the user to which the
     * current Move or DealDamage is applied.
     *
     * @param originalPlayers a List of Player to which the current subeffect is applied
     */
    @Override
    public void updateOnPlayers(List<Player> originalPlayers) {
        Target target;
        List<Player> players = getSandboxPlayers(originalPlayers);
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
            timerConstrainedEventHandler = new TimerConstrainedEventHandler(timerConstrainedEventHandler);
            timerConstrainedEventHandler.start();
        }
    }

    /**
     * If the current sub effect is Move tiles contains a single Tile
     * to which the selected objects must be moved.
     * If the current sub effect is DealDamage tiles contains
     * the targets for the area damage.
     *
     * @param tiles a target for Move(must contain a single tile) or DealDamage
     * @see Tile
     */
    @Override
    public void updateOnTiles(List<Tile> tiles) {
        if (curActionType == MOVE) {
            if (curMove.getObjectToMove() != ObjectToMove.PERSPECTIVE)
                playersToMove.forEach(p -> p.setTile(tiles.get(0)));
            else
                player.setPerspective(tiles.get(0));
            handleTargeting(curMove.getTargeting(), playersToMove);
            nextStep();
        } else if (curActionType == DEALDAMAGE) {
            List<Player> temp = tiles.stream()
                    .map(t -> curMatch.getPlayersInTile(t))
                    .flatMap(List::stream).filter(curDealDamage.getTarget().getPlayerListFilter(player, curWeapon.getTargetPlayers(), curWeapon.getBlackListPlayers())).collect(Collectors.toList());
            temp.removeIf(p -> p.getUsername().equals(player.getUsername()));
            temp.forEach(p -> p.receiveShot(getOriginalPlayer(player), curDealDamage.getDamagesAmount(), curDealDamage.getMarksAmount(), true));
            handleTargeting(curDealDamage.getTargeting(), temp);
            checkPowerUps(temp);
            nextStep();
        }
    }

    /**
     * Checks if the room is valid and apply the current DealDamage
     * to all the Player in the room (but the player itself). If the room is not a valid target
     * signals the mistake to the player.
     * Only check for using samePlayerRoom and Visibility filters.
     *
     * @param room the color of the target room
     */
    @Override
    public void updateOnRoom(Color room) {
        List<Player> possibleTargets = curMatch.getPlayersInRoom(room);
        possibleTargets.removeIf(p -> p.getUsername().equals(player.getUsername()));
        possibleTargets.forEach(p -> p.receiveShot(getOriginalPlayer(player), curDealDamage.getDamagesAmount(), curDealDamage.getMarksAmount(), true));
        handleTargeting(curDealDamage.getTargeting(), possibleTargets);
        checkPowerUps(possibleTargets);
        nextStep();
    }

    /**
     * Handles receiving a stop from the client.
     * No non-reverting stops are accepted into the {@link EffectController}, so it always reverts the action.
     *
     * @param skip
     */
    @Override
    public void updateOnStopSelection(ThreeState skip) {
        controller.updateOnStopSelection(skip);
    }

    /**
     * Ask the player for the proper target after checking the current Move
     * Supports:
     * <li>Moving the player perspective, an helping position for defining complex effects</li>
     * <li>Moving the player itself</li>
     * <li>Moving other players, asking the player who to move.</li>
     */
    private void processMove() {
        List<Tile> selectableTiles = new ArrayList<>();
        List<ReceivingType> receivingTypes;
        switch (curMove.getObjectToMove()) {
            //Handles the move of the perspective
            case PERSPECTIVE:
                selectableTiles = tileTargets(curMove.getTargetDestination());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                askingForSource = false;
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1, 1, curMove.getPrompt()));
                break;
            //Handles the move of the player itself
            case SELF:
                selectableTiles = tileTargets(curMove.getTargetDestination());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                askingForSource = false;
                playersToMove.add(player);
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1, 1, curMove.getPrompt()));
                break;
            //Handles moving other targets; if they are chosen without the user input, they are automatically selected.
            //If user input is needed, it parses the movable targets using TargetSource
            case TARGETSOURCE:
                askingForSource = true;
                processTargetSource(curMove.getTargetSource());
                return;
        }
        //Check if asking for source and need to handle the request to the client
        if (!askingForSource) {
            if (selectableTiles.isEmpty()) {
                updateOnStopSelection(OPTIONAL);
            } else if (selectableTiles.size() == 1) {
                updateOnTiles(selectableTiles);
            } else {
                timerConstrainedEventHandler = new TimerConstrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerConstrainedEventHandler.start();
            }
        }
    }
    /**
     * Ask the player for the proper target after checking the current DealDamage
     * Supports:
     * <li>Attacking every player in a {@link Tile}</li>
     * <li>Attacking every player in a Room</li>
     * <li>Attack single players</li>
     * <li>If all the tiles need to be selected, attack every tile</li>
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
                    skip = true;
                    if (min != 0)
                        updateOnStopSelection(OPTIONAL);
                    else
                        nextStep();
                }
                else if (max == 0) {
                    updateOnTiles(selectableTiles);
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
                        filter(Objects::nonNull).
                        filter(curDealDamage.getTarget().getFilterRoom(board, player.getTile())). //Filter the tiles using the current Target
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
                    skip = true;
                    if (curDealDamage.getTarget().getMinTargets() == 0) {
                        nextStep();
                    }
                    else {
                        updateOnStopSelection(OPTIONAL);
                    }
                }
                //Check if max = 0, or if one player is in the list and min is different from 0
                else if (max == 0 || (players.size() == 1 && min != 0)) {
                    skip = true;
                    updateOnPlayers(players);
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
            timerConstrainedEventHandler = new TimerConstrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerConstrainedEventHandler.start();
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
     * Checks the current target and select the players to be moved if no input is needed.
     * Additional checks if the targets imposes to hit players in {@link Weapon#targetPlayers} or {@link Weapon#blackListPlayers}.
     * Asks the players to move to the Player if needed.
     * @param target the current Move target
     */
    private void processTargetSource(Target target){
        //Check if target imposes to hit list players.
        if(target.getMaxTargets() == 0 && target.getCheckTargetList() == TRUE){
            playersToMove = new ArrayList<>(curWeapon.getTargetPlayers());
            filterPlayers(playersToMove, target);
            updateMoveOnPlayers(playersToMove);
        }
        else if(target.getMaxTargets() == 0 && target.getCheckBlackList() == TRUE){
            playersToMove = new ArrayList<>(curWeapon.getBlackListPlayers());
            filterPlayers(playersToMove, target);
            updateMoveOnPlayers(playersToMove);
        }
        //Handles to ask the players to move to the current player
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
                acceptableTypes.setSelectablePlayers(new SelectableOptions<>(players, max, min,curMove.getPrompt().split("\\$")[0]));
                timerConstrainedEventHandler = new TimerConstrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerConstrainedEventHandler.start();
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
        //Return empty if LASTPLAYER is used with a domination Spawn.
        if (!checkPointOfView(target))
            return new ArrayList<>();
        List<Player> acceptablePlayer = curMatch.getPlayers().
                stream().
                filter(curWeapon!=null?target.getPlayerListFilter(player,curWeapon.getTargetPlayers(), curWeapon.getBlackListPlayers()): s->true).
                collect(Collectors.toList());
        acceptablePlayer.removeIf(p -> p.getUsername().equals(player.getUsername()));
        List<Tile> acceptableTiles = curMatch.getBoard().getTiles().
                stream().flatMap(List::stream).
                filter(target.getFilterTiles(board,pointOfView, curEffect.getDirection())).
                collect(Collectors.toList());
        return acceptablePlayer.stream().
                filter(p -> acceptableTiles.contains(p.getTile()) && !p.getUsername().equals(player.getUsername())).
                collect(Collectors.toList());
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
     * Sets the players in the lists after the weapon gets used, if the current SubEffect targets them.
     * @param targeting specify how to target them, using the Target
     * @param players list of moved / hit players to target
     */
    private void handleTargeting(ThreeState targeting, List<Player> players){
        if(targeting == TRUE){
            curWeapon.setTargetPlayers(players);
        }else if(targeting == FALSE)
            curWeapon.setBlackListPlayers(players);
    }

    /**
     * Check the pointOfView required by the target for the current sub effect and set it accordingly.
     * Handles the case when {@link Target#pointOfView} is {@link PointOfView#LASTPLAYER}, and a Domination spawn is hit (t.h.o.r)
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
        //Handles damaged players that have a DAMAGED powerup
        List<Player> damagedPlayers = players.stream().filter(p -> p.getOnline() && p.hasPowerUp(Moment.DAMAGED)).collect(Collectors.toList());
        countDownLatch = new CountDownLatch(damagedPlayers.size());
        for(Player p : damagedPlayers){
            List<PowerUp> applicable = p.getPowerUps().stream().filter(pUp -> pUp.getApplicability().equals(Moment.DAMAGED)).collect(Collectors.toList());
            acceptableTypes = new AcceptableTypes(receivingTypes);
            acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(applicable, applicable.size(), 0, String.format("Select from 0 and %d powerUps to answer from %s attack", applicable.size(), player.getUsername())));
            Observer damagedController = new DamagedController(countDownLatch, p, player, applicable);
            TimerConstrainedEventHandler temp = new TimerConstrainedEventHandler(damagedController,p.getVirtualView().getRequestDispatcher(), acceptableTypes);
            temp.start();
        }
        try {
            countDownLatch.await();
        }
        catch (Exception e) {
            Logger.log(Priority.DEBUG, "Ended handler powerup damaged");
        }

        //Checks DAMAGING PowerUps
        for (Player p : players.stream().filter(p -> !p.getDominationSpawn()).collect(Collectors.toList())) {
            if (!skip && curDealDamage.getDamagesAmount() != 0 && player.hasPowerUp(Moment.DAMAGING) && !player.getAmmos().isEmpty()) {
                currentEnemy = p;
                List<PowerUp> selectablePowerUps= player.getPowerUps().stream().filter(pUp -> pUp.getApplicability().equals(Moment.DAMAGING)).collect(Collectors.toList());
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps, selectablePowerUps.size(), 0, String.format("Select from 0 to %d PowerUp!, to use against %s", selectablePowerUps.size(), p.getUsername())));
                timerConstrainedEventHandler = new TimerConstrainedEventHandler( this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                countDownLatch = new CountDownLatch(1);
                timerConstrainedEventHandler.start();
                try {
                    countDownLatch.await();
                }
                catch (Exception e) {
                    Logger.log(Priority.DEBUG, "Ended handler powerup damaging");
                }
            }
            else break;
        }
    }

    /**
     * Utility method to update the current {@link Effect#direction}.
     * If direction need to be asked, asks to the current player for a direction, and updates the related attribute.
     * @return false if the player didn't answer and flow need to be stopped
     */
    public boolean updateDirection(Target target) {
        //Check if direction need to be asked
        if (target.getCardinal() == TRUE && curEffect.getDirection() == null) {
            acceptableTypes = new AcceptableTypes(Collections.singletonList(DIRECTION));
            List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
            acceptableTypes.setSelectableDirections(new SelectableOptions<>(directions, 1, 1, "Select a direction for the target!"));
            Choice directionRequest = new Choice(player.getVirtualView().getRequestDispatcher(), acceptableTypes, curMatch);
            switch (directionRequest.getReceivingType()) {
                case STOP:
                    updateOnStopSelection(TRUE);
                    return false;
                case DIRECTION:
                    Direction direction = directionRequest.getDirection();
                    curEffect.setDirection(direction);
                    break;
            }
        }
        return true;
    }

    /**
     * Handles the move of selected players, from client input or directly from the card.
     * <li>If the possible tile is one, it automatically moves the players.</li>
     * <li>If the possible tiles are more than one, it prompt the user with the choice.</li>
     * <li>It resets the action if there are no possible tiles.</li>
     * @param players
     */
    private void updateMoveOnPlayers(List<Player> players){
        if (players.isEmpty()) {
            nextStep();
            return;
        }
        if (curMove.getTargetDestination().getPointOfView() == PointOfView.TARGET)
            pointOfView = players.get(0).getTile();
        //Check if direction need to be asked, if so ask it and block the flow if the player didn't answer
        if (!updateDirection(curMove.getTargetDestination()))
            return;
        askingForSource = false;
        playersToMove = players;
        List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.TILES));
        List<Tile> tiles = tileTargets(curMove.getTargetDestination());
        acceptableTypes = new AcceptableTypes(receivingTypes);
        //Handles when player try to move a domination spawn - remove every tile but the one the domination spawn is in.
        if (playersToMove.stream().anyMatch(Player::getDominationSpawn)) {
            Tile spawnTile = playersToMove.stream().filter(Player::getDominationSpawn).findFirst().get().getTile();
            tiles.removeIf(tile -> !tile.equals(spawnTile));
        }
        if (tiles.isEmpty())
            updateOnStopSelection(TRUE);
        else if (tiles.size() == 1) {
            updateOnTiles(tiles);
        }
        else {
            acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(tiles, 1, 1, curMove.getPrompt().split("\\$")[1]));
            timerConstrainedEventHandler = new TimerConstrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerConstrainedEventHandler.start();
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
            else if (player.getAmmos().isEmpty())
                break;
            acceptableTypes.setSelectableAmmos(new SelectableOptions<>(ammos, 1 , 1, "Select an ammo to discard"));
            acceptableTypes.setStop(true, "Don't pay for no more powerUps!");
            Choice ammoRequest = new Choice(player.getVirtualView().getRequestDispatcher(), acceptableTypes, curMatch);
            Ammo toPay;
            switch (ammoRequest.getReceivingType()) {
                case STOP:
                    if (ammoRequest.getStop().equals(TRUE)) {
                        skip = true;
                        countDownLatch.countDown();
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
                    skip = true;
                    countDownLatch.countDown();
                    updateOnStopSelection(TRUE);
                    return;
            }
            player.discardPowerUp(p, false);
            curMatch.updatePopupViews(String.format("%s use %s against %s",
                    player.getUsername(),
                    p.getName(),
                    currentEnemy.getUsername()));
            damagesAmount = p.getEffect().getDamages().get(0).getDamagesAmount();
            marksAmount = p.getEffect().getDamages().get(0).getMarksAmount();
            currentEnemy.receiveShot(player,damagesAmount,marksAmount, false);
            if (player.getAmmos().isEmpty() && player.getPowerUps().stream().allMatch(p::equals)) {
                break;
            }
        }
        countDownLatch.countDown();

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