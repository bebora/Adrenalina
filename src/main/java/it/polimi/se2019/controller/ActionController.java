package it.polimi.se2019.controller;

import it.polimi.se2019.model.DominationMatch;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.actions.SubAction;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
//TODO: substitute comments with actual methods to communicate with the view
public class ActionController {
    private Match originalMatch;
    private Match sandboxMatch;
    private WeaponController weaponController;
    private SubAction curSubAction;
    private Action curAction;
    private int subActionIndex = 0;
    private Player curPlayer;

    public ActionController(Match match){
        originalMatch = match;
        curPlayer = originalMatch.getPlayers().get(originalMatch.getCurrentPlayer());
    }

    public void update(Action action){
        cloneMatch();
        if(curPlayer.getActions().contains(action))
            curAction = action;
        nextStep();
    }

    public void updateOnWeapon(Weapon weapon){
        if(curSubAction == SubAction.GRAB){
            if(curPlayer.getTile().getWeapons().contains(weapon)){
                curPlayer.addWeapon(curPlayer.getTile().grabWeapon(weapon));
                nextStep();
            }else{
                //signals impossible weapon selection
            }
        }else if(curSubAction == SubAction.SHOOT){
            if(weaponController == null)
                weaponController = new WeaponController(sandboxMatch,weapon,originalMatch.getPlayers());
            else {
                weaponController.setMatch(sandboxMatch);
                weaponController.update(weapon);
            }
        }else if(curSubAction == SubAction.RELOAD){
            if(curPlayer.getWeapons().contains(weapon)){
                if(curPlayer.checkForAmmos(weapon.getCost())) {
                    curPlayer.reload(weapon);
                    nextStep();
                }
                else{
                    //signals not enough ammo
                }
            }
        }
    }

    public void updateOnAmmoCard(AmmoCard ammoCard){
        if(curSubAction == SubAction.GRAB){
            if(curPlayer.getTile().getAmmoCard() == ammoCard){
                ammoCard.getAmmos().forEach(a -> curPlayer.addAmmo(a));
                curPlayer.getTile().grabAmmoCard();
                nextStep();
            }
            else{
                //illegal target
            }
        }else{
            //not waiting for this
        }
    }

    public void updateOnTile(Tile tile){
        if(curSubAction == SubAction.MOVE){
            if(originalMatch.getBoard().reachable(curPlayer.getTile(),0,curAction.getMovements(),false).contains(tile)) {
                curPlayer.setTile(tile);
                nextStep();
            }
            else{
                //tell the player that he can't reach that tile
            }
        }else{
            //not waiting for this
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
        }else{
            curSubAction = curAction.getSubActions().get(subActionIndex);
            //TODO: ask for the proper target
        }
        subActionIndex++;
    }
}
