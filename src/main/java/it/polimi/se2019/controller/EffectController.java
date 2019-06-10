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

    private boolean noInput;

    private int enemyWithPowerUps;

    private boolean askingForSource;

    private Player currentEnemy;
    private TimerCostrainedEventHandler timerCostrainedEventHandler;
    private AcceptableTypes acceptableTypes;
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
    }

    /**
     * Read the next ActionType to be executed and
     * call the method to check if the effect needs a Direction.
     * If there is no next ActionType prepare clean the EffectController
     * for a new input.
     */
     void nextStep(){
        orderIndex+=1;
        playersToMove = new ArrayList<>();
        if(orderIndex < curEffect.getOrder().size()) {
            curActionType = curEffect.getOrder().get(orderIndex);
            if(curActionType == MOVE) {
                moveIndex += 1;
                curMove = curEffect.getMoves().get(moveIndex);
                if(curMove.getTargetSource() != null && curMove.getTargetSource().getVisibility() != null)
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
     * @param players a List of Player to which the current subeffect is applied
     * @see Player
     */
    @Override
    public void updateOnPlayers(List<Player> players){
        if (acceptableTypes.getSelectablePlayers().checkForCoherency(players)) {
            if (curActionType == MOVE && askingForSource) {
                updateMoveOnPlayers(players);
            } else {
                updateDealDamageOnPlayers(players);
            }
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
        if (acceptableTypes.getSelectableTileCoords().checkForCoherency(tiles)) {
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
                        .peek(p -> p.receiveShot(getOriginalPlayer(player), curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()))
                        .collect(Collectors.toList());
                handleTargeting(curDealDamage.getTargeting(),temp);
                checkPowerUps(temp);
                nextStep();
            }
        }
        else {
            throw new IncorrectEvent("Tile sbagliate!");
        }
    }

    /**
     * Checks if the room is valid and apply the current DealDamage
     * to all the Player in the room. If the room is not a valid target
     * signals the mistake to the player.
     * Only check for using samePlayerRoom and Visibility filters.
     * @param room the color of the target room
     */
    @Override
    public void updateOnRoom(Color room){
        List<Player> possibleTargets = curMatch.getPlayersInRoom(room);
        if(acceptableTypes.getSelectableRooms().checkForCoherency(Collections.singletonList(room))){
            possibleTargets.forEach(p -> p.receiveShot(getOriginalPlayer(player),curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
            handleTargeting(curDealDamage.getTargeting(),possibleTargets);
            checkPowerUps(possibleTargets);
            nextStep();
        }
        else {
            //tells the player that the target is wrong
        }
    }
    @Override
    public void updateOnStopSelection(ThreeState skip){
        if (skip.toBoolean() || acceptableTypes.isReverse()) {
            controller.updateOnStopSelection(skip.compare(acceptableTypes.isReverse()));
        }
        else {
            //TODO WHAT IF NOT REVERSING HERE, CHECK
        }


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
            timerCostrainedEventHandler = new TimerCostrainedEventHandler( this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerCostrainedEventHandler.start();
        }
        else processStep();
    }

    /**
     * Ask the player for the proper target after checking the current Move
     */
    private void processMove(){
        List<Tile> selectableTiles;
        List<ReceivingType> receivingTypes;
        switch(curMove.getObjectToMove()){
            case PERSPECTIVE:
                selectableTiles = tileTargets(curMove.getTargetDestination());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                askingForSource = false;
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1,1, "Seleziona una tile di arrivo per il tuo punto di vista!"));
                break;
            case SELF:
                selectableTiles = tileTargets(curMove.getTargetDestination());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                askingForSource = false;
                playersToMove.add(player);
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1,1, "Seleziona una tile di arrivo per te stesso!"));
                break;
            case TARGETSOURCE:
                askingForSource = true;
                processTargetSource(curMove.getTargetSource());
                if (!askingForSource) {
                    selectableTiles = tileTargets(curMove.getTargetDestination());
                    receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                    acceptableTypes = new AcceptableTypes(receivingTypes);
                    acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, 1,1, "Seleziona una tile di arrivo per i giocatori!"));
                }
                break;
            default:
                break;
        }
        if (!askingForSource) {
            timerCostrainedEventHandler = new TimerCostrainedEventHandler( this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerCostrainedEventHandler.start();
        }

    }

    /**
     * Ask the player for the proper target after checking the current DealDamage
     */
    private void processDealDamage(){
        Area targetType = curDealDamage.getTarget().getAreaDamage();
        List<ReceivingType> receivingTypes;
        int min = curDealDamage.getTarget().getMinTargets();
        int max = curDealDamage.getTarget().getMaxTargets();
        switch(targetType){
            case TILE:
                List<Tile> selectableTiles = tileTargets(curDealDamage.getTarget());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.TILES));
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(selectableTiles, max, min, "Seleziona la tile dove attaccare"));
                break;
            case ROOM:
                List<Color> selectableRoom = board.getTiles().
                        stream().
                        flatMap(List::stream).
                        filter(curDealDamage.getTarget().getFilterRoom(board,pointOfView)).
                        map(Tile::getRoom).
                        distinct().
                        collect(Collectors.toList());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.ROOM));
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectableRooms(new SelectableOptions<>(selectableRoom,max,min,"Seleziona una room"));
                break;
            case SINGLE:
                List<Player> players = playerTargets(curDealDamage.getTarget());
                receivingTypes = new ArrayList<>(Collections.singleton(ReceivingType.PLAYERS));
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePlayers(new SelectableOptions<>(players, max, min, "Seleziona i players da attaccare"));
                break;
            default:
                break;
        }
        timerCostrainedEventHandler = new TimerCostrainedEventHandler(this,player.getVirtualView().getRequestDispatcher(),acceptableTypes);
        timerCostrainedEventHandler.start();
    }

    /**
     * Checks the current target and select the players to be moved if no input is needed
     * @param target the current Move target
     */
    private void processTargetSource(Target target){
        if(target.getMaxTargets() == 0 && target.getCheckTargetList() == TRUE){
            askingForSource = false;
            playersToMove = curWeapon.getTargetPlayers();
        }
        else if(target.getMaxTargets() == 0 && target.getCheckBlackList() == TRUE){
            askingForSource = false;
            playersToMove = curWeapon.getBlackListPlayers();
        }
        else {
            List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.PLAYERS));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            int min = target.getMinTargets();
            int max = target.getMaxTargets();
            List<Player> players = playerTargets(target);
            players.remove(player);
            if (players.isEmpty()) {
                player.getVirtualView().getViewUpdater().sendPopupMessage("You can't move anyone! Wrong choice mate!");
                updateOnStopSelection(OPTIONAL);
            }
            else {
                acceptableTypes.setSelectablePlayers(new SelectableOptions<>(players, max, min, "Seleziona i giocatori da muovere!"));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerCostrainedEventHandler.start();
            }
        }
    }

    private List<Player> playerTargets(Target target) {
        checkPointOfView(target);
        List<Player> acceptablePlayer = curMatch.getPlayers().
                stream().
                filter(curWeapon!=null?target.getPlayerListFilter(player,curWeapon.getTargetPlayers(), curWeapon.getBlackListPlayers()): s->true).
                collect(Collectors.toList());
        List<Tile> acceptableTiles = curMatch.getBoard().getTiles().
                stream().flatMap(List::stream).
                filter(target.getFilterTiles(board,pointOfView)).
                collect(Collectors.toList());
        return acceptablePlayer.stream().
                filter(p -> acceptableTiles.contains(p.getTile())).
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
                         .allMatch(target.getFilterTiles(board,pointOfView)) &&
                 players.stream()
                         .allMatch((curWeapon!=null)?target.getPlayerListFilter(player,curWeapon.getTargetPlayers(),curWeapon.getBlackListPlayers()): p -> true);
        return result;
    }

    private List<Tile> tileTargets(Target target) {
        checkPointOfView(target);
        return board.getTiles().stream().
                flatMap(List::stream).
                filter(Objects::nonNull).
                filter(target.getFilterTiles(board,pointOfView)).
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
                .allMatch(target.getFilterTiles(board,pointOfView));
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
    private void checkPointOfView(Target target){
        switch(target.getPointOfView()){
            case OWN:
                pointOfView = player.getTile();
                break;
            case PERSPECTIVE:
                pointOfView = player.getPerspective();
                break;
            case LASTPLAYER:
                pointOfView = curWeapon.getTargetPlayers().get(0).getTile();
                break;
            //case TARGET is already handled when askingForSource
            default:
                break;
        }
    }

    private void checkPowerUps(List<Player> players){
        List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP));
        for (Player p : players) {
            if (curDealDamage.getDamagesAmount() != 0 && player.hasPowerUp(Moment.DAMAGING) && !player.getAmmos().isEmpty()) {
                currentEnemy = p;
                AcceptableTypes ammosAccepted = new AcceptableTypes(Collections.singletonList(ReceivingType.AMMO));
                List<Ammo> ammos = new ArrayList<>(new HashSet<>(player.getAmmos()));
                ammosAccepted.setSelectableAmmos(new SelectableOptions<>(ammos, 1 , 1, "Select an ammo to discard"));
                Choice ammoRequest = new Choice(player.getVirtualView().getRequestDispatcher(), ammosAccepted);
                Ammo toPay;
                switch (ammoRequest.getReceivingType()) {
                    case STOP:
                        updateOnStopSelection(TRUE);
                        return;
                    case AMMO:
                        toPay = ammoRequest.getAmmo();
                        player.getAmmos().remove(toPay);
                        break;
                }
                List<PowerUp> selectablePowerUps= player.getPowerUps().stream().filter(pUp -> pUp.getApplicability().equals(Moment.DAMAGING)).collect(Collectors.toList());
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps, selectablePowerUps.size(), 0, String.format("Seleziona tra 0 e %d PowerUp!", selectablePowerUps.size())));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler( this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerCostrainedEventHandler.start();
                try {
                    timerCostrainedEventHandler.join();
                }
                catch (Exception e) {
                    Logger.log(Priority.DEBUG, "Ended handler powerup damaging");
                }
                player.getVirtualView().getRequestDispatcher().clear();
            }
            else break;
        }
        List<TimerCostrainedEventHandler> handlersPowerUp = new ArrayList<>();
        for(Player p: players){
            if(p.hasPowerUp(Moment.DAMAGED)){
                List<PowerUp> applicable = p.getPowerUps().stream().filter(pUp -> pUp.getApplicability().equals(Moment.DAMAGED)).collect(Collectors.toList());
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(applicable, applicable.size(), 0, String.format("Seleziona tra 0 e %d PowerUp!", applicable.size())));
                Observer damagedController = new DamagedController(p, player, applicable);
                TimerCostrainedEventHandler temp = new TimerCostrainedEventHandler(damagedController,p.getVirtualView().getRequestDispatcher(), acceptableTypes);
                timerCostrainedEventHandler.setNotifyOnEnd(false);
                handlersPowerUp.add(temp);
            }
        }
        for (TimerCostrainedEventHandler t : handlersPowerUp) {
            try {
                t.join();
            }
            catch (Exception e) {
                Logger.log(Priority.DEBUG, "Ended handler powerup damaged");
            }
        }
    }

    private void updateMoveOnPlayers(List<Player> originalTargetPlayers){
        List<Player> players = getSandboxPlayers(originalTargetPlayers);
        if(checkPlayerTargets(curMove.getTargetSource(),players)) {
            player.getVirtualView().getRequestDispatcher().clear();
            if (curMove.getTargetDestination().getPointOfView() == PointOfView.TARGET)
                pointOfView = players.get(0).getTile();
            askingForSource = false;
            playersToMove = players;
            List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.TILES));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            List<Tile> tiles = tileTargets(curMove.getTargetDestination());
            acceptableTypes.setSelectableTileCoords(new SelectableOptions<>(tiles, 1, 1, "Seleziona una tile di destinazione!"));
            timerCostrainedEventHandler = new TimerCostrainedEventHandler( this, player.getVirtualView().getRequestDispatcher(), acceptableTypes);
            timerCostrainedEventHandler.start();
        }
        else {
            throw new IncorrectEvent("Bersagli sbagliati!");
        }
    }

    private void updateDealDamageOnPlayers(List<Player> originalTargetPlayers){
        List<Player> players = getSandboxPlayers(originalTargetPlayers);
        if(curDealDamage.getTarget().getMaxTargets() == 0){
            if(curDealDamage.getTarget().getCheckTargetList() == TRUE)
                curWeapon.getTargetPlayers().forEach(p -> p.receiveShot(getOriginalPlayer(player),curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
            else if(curDealDamage.getTarget().getCheckBlackList() == TRUE)
                curWeapon.getBlackListPlayers().forEach(p -> p.receiveShot(getOriginalPlayer(player),curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
        }
        else if(checkPlayerTargets(curDealDamage.getTarget(),players)){
            players.forEach(p -> p.receiveShot(getOriginalPlayer(player),curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
            handleTargeting(curDealDamage.getTargeting(),players);
            checkPowerUps(players);
            nextStep();
        }
        else{
            //communicate the error to the player
        }
    }

    /**
     * Receive a powerUp that can be used after a inflicting damage
     * and prepare the controller for executing its effect
     * Assumptions:
     * <li>Moment.damaging powerup inflict damage</li>
     * @param powerUps a single powerUp to be used
     * @param discard whether you are discarding the powerup (mostly deprecated)
     */
    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard){
        int damagesAmount;
        int marksAmount;
        powerUps = powerUps.stream().filter(powerUp -> player.getPowerUps().contains(powerUp) && powerUp.getApplicability() == Moment.DAMAGING).collect(Collectors.toList());
        for(PowerUp p: powerUps){
            player.discardPowerUp(p, false);
            damagesAmount = p.getEffect().getDamages().get(0).getDamagesAmount();
            marksAmount = p.getEffect().getDamages().get(0).getMarksAmount();
            player.receiveShot(currentEnemy,damagesAmount,marksAmount);
        }
        nextStep();
    }

    private Player getOriginalPlayer(Player sandboxPlayer){
        return originalPlayers.stream()
                .filter(p -> p.getId().equals(sandboxPlayer.getId()))
                .findAny().orElse(null);
    }
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

    void setCurWeapon(Weapon weapon){this.curWeapon = weapon;}
    void setCurEffect(Effect effect){this.curEffect = effect;}
    void setCurMatch(Match match){this.curMatch = match;}
    void setOriginalPlayers(List<Player> originalPlayers){this.originalPlayers = originalPlayers;}
    public void setPlayer(Player player){this.player = player;}

}