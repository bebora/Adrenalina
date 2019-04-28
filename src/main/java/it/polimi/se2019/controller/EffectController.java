package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.*;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.se2019.model.cards.ActionType.MOVE;

public class EffectController {
    private Board board;
    private Player player;
    private Weapon curWeapon;

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

    public EffectController(Effect curEffect,Board board, Player player, Weapon weapon){
        this.curEffect = curEffect;
        this.moveIndex = -1;
        this.orderIndex = -1;
        this.dealDamageIndex = 0;
        this.curWeapon = weapon;
        this.player = player;
        this.board = board;
        this.playersToMove = new ArrayList<>();
    }

    public void nextStep(){
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
            orderIndex = 0;
        }
    }

    private void update(Direction direction){
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

    private void processStep(){
        if(curActionType == MOVE)
            processMove();
        else
            processDealDamage();
    }

    //TODO:complete the update method and add update for others input targets
    public void update(List<Player> players){
        if(curActionType == MOVE && askingForSource){
                if(checkTargets(curMove.getTargetSource(),players)) {
                    if (curMove.getTargetSource().getPointOfView() == PointOfView.TARGET)
                        pointOfView = players.get(0).getTile();
                    askingForSource = false;
                    playersToMove = players;
                }
                else{
                    //communicate the error to the player
            }
        }
        else{
            if(checkTargets(curDealDamage.getTarget(),players)){
                players.forEach(p -> p.receiveShot(player,curDealDamage.getDamagesAmount(),curDealDamage.getMarksAmount()));
                if(curDealDamage.getTargeting() == ThreeState.TRUE)
                    curWeapon.setTargetPlayers(players);
                else if(curDealDamage.getTargeting() == ThreeState.FALSE)
                    curWeapon.setBlackListPlayers(players);
            }
            else{
                //communicate the error to the player
            }
        }
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

    private boolean checkTargets(Target target, List<Player> players){
        boolean result;

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
        result = players.stream()
                         .map(Player::getTile)
                         .allMatch(target.getFilterTiles(board,pointOfView)) &&
                 players.stream()
                         .allMatch(target.getPlayerListFilter(player,curWeapon.getTargetPlayers(),curWeapon.getBlackListPlayers()));
        return result;
    }

}
