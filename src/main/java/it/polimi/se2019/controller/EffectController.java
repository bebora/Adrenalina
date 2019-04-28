package it.polimi.se2019.controller;

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

public class EffectController {
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

    public EffectController(Effect curEffect, Weapon weapon,Match match){
        this.curMatch = match;
        this.curEffect = curEffect;
        this.moveIndex = -1;
        this.orderIndex = -1;
        this.dealDamageIndex = -1;
        this.curWeapon = weapon;
        this.player = match.getPlayers().get(match.getCurrentPlayer());
        this.board = match.getBoard();
        this.playersToMove = new ArrayList<>();

    }

    public void nextStep(){
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
            curMove = null;
            curDealDamage = null;
            curActionType = null;
            dealDamageIndex = -1;
            moveIndex = -1;
            orderIndex = -1;
        }
    }

    private void processStep(){
        if(curActionType == MOVE)
            processMove();
        else
            processDealDamage();
    }

    //TODO:complete the update method and add update for others input targets

    public void update(Direction direction){
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

    public void update(List<Player> players){
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
            }
            else{
                //communicate the error to the player
            }
            nextStep();
        }

    }

    public void update(ArrayList<Tile> tiles){
        if(curActionType == MOVE) {
            if (checkTileTargets(curMove.getTargetDestination(), tiles))
                playersToMove.forEach(p -> p.setTile(tiles.get(0)));
            else {
                //communicate the error to the player
            }
        }
        else{
            if(checkTileTargets(curDealDamage.getTarget(),tiles)){
                tiles.forEach(t -> curMatch.getPlayersInTile(t)
                        .forEach(p -> p.receiveShot(player, curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount())));
            }
        }
        nextStep();
    }

    public void update(Color room){
        List<Player> possibleTargets = curMatch.getPlayersInRoom(room);
        if(possibleTargets.stream()
                .map(p -> p.getTile())
                .allMatch(curDealDamage.getTarget().getFilterRoom(board,pointOfView))){
            possibleTargets.forEach(p -> p.receiveShot(player,curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
        }
        else{
            //tells the player that the target is wrong
        }
        nextStep();
    }

    private void processDirection(Target target){
        if(target.getCardinal() == ThreeState.TRUE || target.getCardinal() == ThreeState.FALSE) {
            //ask for direction
        }
        else
            processStep();
    }

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

    private boolean checkTileTargets(Target target,List<Tile> tiles){
        checkPointOfView(target);
        return tiles.stream()
                .allMatch(target.getFilterTiles(board,pointOfView));
    }

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

}
