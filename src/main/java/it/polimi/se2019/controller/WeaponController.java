package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.EventWrapper;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.Weapon;

import java.util.ArrayList;
import java.util.List;

public class WeaponController extends Thread {
    private int curEffect;
    private int effectIndex;
    private Weapon weapon;
    private Match match;
    private Player curPlayer;

    public WeaponController(Match match, Weapon weapon){
        this.match = match;
        this.weapon = weapon;
        this.curEffect = 1;
        this.effectIndex = -1;
        this.curPlayer = match.getPlayers().get(match.getCurrentPlayer());
    }
    public List<String> getUsableEffects(){
        List<String> usableEffects = new ArrayList<>();
        List<Effect> allEffects = weapon.getEffects();
        String curName;
        for(Effect e: allEffects) {
            if (curPlayer.checkForAmmos(e.getCost())) {
                if (e.getAbsolutePriority() != 0) {
                    if (e.getAbsolutePriority() == curEffect)
                        usableEffects.add(e.getName());
                } else {
                    for (Integer i : e.getRelativePriority()) {
                        curName = allEffects.get(Math.abs(i) - 1).getName();
                        if(usableEffects.contains(curName) && i < 0)
                                usableEffects.add(e.getName());
                    }
                }
            }
        }
        return usableEffects;
    }
    void update(EventWrapper e) {
        //TODO will update when the data will be avaiable, notifying the process that is waiting
    }
}
