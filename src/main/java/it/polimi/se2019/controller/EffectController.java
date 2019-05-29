package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Observer;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.*;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.se2019.model.ThreeState.FALSE;
import static it.polimi.se2019.model.ThreeState.TRUE;
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
    private WeaponController weaponController;
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

    private int enemyWithPowerUps;

    private boolean askingForSource;
    private Player currentEnemy;

    private List<TimerCostrainedEventHandler> handlersPowerUp;
    EffectController(Effect curEffect, Weapon weapon,Match match,Player player,List<Player> originalPlayers, WeaponController weaponController){
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
        this.weaponController = weaponController;
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
                if(curMove.getTargetSource() != null)
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
            weaponController.updateOnConclusion();
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
    public void updateOnDirection(Direction direction){
        if(curEffect.getDirection() == null){
            player.getVirtualView().getRequestDispatcher().clear();
            curEffect.setDirection(direction);
            processStep();
        }
        else if(curEffect.getDirection() == direction) {
            player.getVirtualView().getRequestDispatcher().clear();
            processStep();
        }
        else {
            //TODO ask user for same direction as curEffect.getDirection()!
        }
    }

    /**
     * players is a list of players provided from the user to which the
     * current Move or DealDamage is applied.
     * @param players a List of Player to which the current subeffect is applied
     * @see Player
     */
    public void updateOnPlayers(List<Player> players){
        if(curActionType == MOVE && askingForSource){
            updateMoveOnPlayers(players);
        }
        else{
            updateDealDamageOnPlayers(players);
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
    public void updateOnTiles(List<Tile> tiles){
        if(curActionType == MOVE) {
            if (checkTileTargets(curMove.getTargetDestination(), tiles)){
                if(curMove.getObjectToMove() != ObjectToMove.PERSPECTIVE)
                    playersToMove.forEach(p -> p.setTile(tiles.get(0)));
                else
                    player.setPerspective(tiles.get(0));
                nextStep();
            }
            else {
                //communicate the error to the player
            }
        }
        else{
            if(checkTileTargets(curDealDamage.getTarget(),tiles)){
                List<Player> temp = tiles.stream()
                        .map(t -> curMatch.getPlayersInTile(t))
                        .flatMap(List::stream)
                        .peek(p -> p.receiveShot(getOriginalPlayer(player), curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()))
                        .collect(Collectors.toList());
                handleTargeting(curDealDamage.getTargeting(),temp);
                if(!checkPowerUps(temp))
                    nextStep();
            }
        }
    }

    /**
     * Checks if the room is valid and apply the current DealDamage
     * to all the Player in the room. If the room is not a valid target
     * signals the mistake to the player.
     * Only check for using samePlayerRoom and Visibility filters.
     * @param room the color of the target room
     */
    public void updateOnRoom(Color room){
        List<Player> possibleTargets = curMatch.getPlayersInRoom(room);
        if(possibleTargets.stream()
                .map(Player::getTile)
                .allMatch(curDealDamage.getTarget().getFilterRoom(board,pointOfView))){
            possibleTargets.forEach(p -> p.receiveShot(getOriginalPlayer(player),curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
            handleTargeting(curDealDamage.getTargeting(),possibleTargets);
            if(!checkPowerUps(possibleTargets))
                nextStep();
        }
        else{
            //tells the player that the target is wrong
        }
    }

    public void updateOnStopSelection(boolean reverse, boolean skip){
        if (reverse)
            weaponController.updateOnStopSelection(true, skip);
    }

    /**
     * Ask the player for a Direction if the current target requires one,
     * otherwise go on with the effect
     * @param target the target of the current subeffect
     */
    private void processDirection(Target target){
        if(target.getCardinal() == TRUE || target.getCardinal() == ThreeState.FALSE) {
            //ask for direction
        }
        else
            processStep();
    }

    /**
     * Ask the player for the proper target after checking the current Move
     */
    //TODO: complete processMove and processDealDamage with proper methods to communicate with view
    private void processMove(){
        switch(curMove.getObjectToMove()){
            case SELF:
                askingForSource = false;
                playersToMove.add(player);
                //ask for tile destination
                break;
            case PERSPECTIVE:
                askingForSource = false;
                //ask for tile destination
                break;
            case TARGETSOURCE:
                askingForSource = true;
                processTargetSource(curMove.getTargetSource());
                //ask for tile destination
                break;
            default:
                break;
        }

    }

    /**
     * Ask the player for the proper target after checking the current DealDamage
     */
    private void processDealDamage(){
        Area targetType = curDealDamage.getTarget().getAreaDamage();
        switch(targetType){
            case TILE:
                //ask for tiles
                break;
            case ROOM:
                //ask for room
                break;
            case SINGLE:
                //ask for players;
                break;
            default:
                break;
        }
    }

    /**
     * Checks the current target and select the players to be moved if no input is needed
     * @param target the current Move target
     */
    private void processTargetSource(Target target){
        if(target.getMaxTargets() == 0 && target.getCheckTargetList() == TRUE){
                playersToMove = curWeapon.getTargetPlayers();
        }
        if(target.getMaxTargets() == 0 && target.getCheckBlackList() == TRUE){
            playersToMove = curWeapon.getBlackListPlayers();
        }
        //set visitor to accept only tiles
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
                         .allMatch(target.getPlayerListFilter(player,curWeapon.getTargetPlayers(),curWeapon.getBlackListPlayers()));
        return result;
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

    private boolean checkPowerUps(List<Player> players){
        boolean handlePowerup = false;
        enemyWithPowerUps = 0;
        if(curDealDamage.getDamagesAmount() != 0 && player.hasPowerUp(Moment.DAMAGING)){
            curWeapon.setTargetPlayers(players);
            handlePowerup = true;
        }
        handlersPowerUp = new ArrayList<>();
        for(Player p: players){
            if(p.hasPowerUp(Moment.DAMAGED)){
                handlePowerup = true;
                List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP));
                List<PowerUp> applicable = p.getPowerUps().stream().filter(pUp -> pUp.getApplicability().equals(Moment.DAMAGED)).collect(Collectors.toList());
                Observer damagedController = new DamagedController(p, player, applicable);
                TimerCostrainedEventHandler temp = new TimerCostrainedEventHandler(5,damagedController,p.getVirtualView().getRequestDispatcher(), receivingTypes);
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
        return handlePowerup;
    }

    private void updateMoveOnPlayers(List<Player> originalTargetPlayers){
        List<Player> players = getSandboxPlayers(originalTargetPlayers);
        if(checkPlayerTargets(curMove.getTargetSource(),players)) {
            player.getVirtualView().getRequestDispatcher().clear();
            if (curMove.getTargetSource().getPointOfView() == PointOfView.TARGET)
                pointOfView = players.get(0).getTile();
            askingForSource = false;
            playersToMove = players;
            //ask for targetDestination
        }
        else{
            //TODO send error to the player
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
            if(!checkPowerUps(players))
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
     * <li>Moment.damaged powerup inflict mark</li>
     * @param powerUps a single powerUp to be used
     * @param discard whether you are discarding the powerup (mostly deprecated)
     */
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard){
        int damagesAmount;
        int marksAmount;
        powerUps = powerUps.stream().filter(powerUp -> player.getPowerUps().contains(powerUp) && powerUp.getApplicability() == Moment.DAMAGING).collect(Collectors.toList());
        for(PowerUp p: powerUps){
            player.discardPowerUp(p);
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