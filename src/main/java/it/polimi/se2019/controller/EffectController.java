package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.*;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.se2019.model.cards.ActionType.MOVE;

/**
 * This class contains all the logic related to the execution
 * of an effect after the player has chosen it.
 */
public class EffectController implements Observer {
    private Board board;
    private Player player;
    private Weapon curWeapon;
    private Match curMatch;

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

    EffectController(Effect curEffect, Weapon weapon,Match match,Player player){
        this.curMatch = match;
        this.curEffect = curEffect;
        this.moveIndex = -1;
        this.orderIndex = -1;
        this.dealDamageIndex = -1;
        this.curWeapon = weapon;
        this.player = player;
        this.board = match.getBoard();
        this.playersToMove = new ArrayList<>();
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
            curEffect = null;
            curWeapon = null;
            player = null;
            curMove = null;
            curDealDamage = null;
            curActionType = null;
            dealDamageIndex = -1;
            moveIndex = -1;
            orderIndex = -1;
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

    //TODO:complete the update method and add update for others input targets

    /**
     * direction is the cardinal Direction in which the effect
     * is applied. If the current value is null memorize the new value,
     * otherwise tell the user to send another Direction
     * @param direction the Direction in which the effect is applied
     * @see Direction
     */
    public void updateOnDirection(Direction direction){
        if(curEffect.getDirection() == null){
            curEffect.setDirection(direction);
            processStep();
        }
        else if(curEffect.getDirection() == direction)
            processStep();
        else {
            //signals that the direction is not the same as the previous step
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
                if(checkPlayerTargets(curMove.getTargetSource(),players)) {
                    if (curMove.getTargetSource().getPointOfView() == PointOfView.TARGET)
                        pointOfView = players.get(0).getTile();
                    askingForSource = false;
                    playersToMove = players;
                    //ask for targetDestination
                }
                else{
                    //communicate the error to the player
            }
        }
        else{
            if(checkPlayerTargets(curDealDamage.getTarget(),players)){
                players.forEach(p -> p.receiveShot(player,curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
                if(curDealDamage.getTargeting() == ThreeState.TRUE)
                    curWeapon.setTargetPlayers(players);
                else if(curDealDamage.getTargeting() == ThreeState.FALSE)
                    curWeapon.setBlackListPlayers(players);
                nextStep();
            }
            else{
                //communicate the error to the player
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
                tiles.forEach(t -> curMatch.getPlayersInTile(t)
                        .forEach(p -> p.receiveShot(player, curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount())));
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
            possibleTargets.forEach(p -> p.receiveShot(player,curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
        nextStep();
        }
        else{
            //tells the player that the target is wrong
        }
    }

    /**
     * Ask the player for a Direction if the current target requires one,
     * otherwise go on with the effect
     * @param target the target of the current subeffect
     */
    private void processDirection(Target target){
        if(target.getCardinal() == ThreeState.TRUE || target.getCardinal() == ThreeState.FALSE) {
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

    void setCurWeapon(Weapon weapon){this.curWeapon = weapon;}
    void setCurEffect(Effect effect){this.curEffect = effect;}
    public void setPlayer(Player player){this.player = player;}
}
