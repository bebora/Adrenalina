package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeaponController {
    private int curEffect;
    private int lastUsedIndex;
    private Weapon weapon;
    private Match match;
    private Player curPlayer;
    private List<Player> originalPlayers;
    private EffectController effectController;

    public WeaponController(Match sandboxMatch, Weapon weapon, List<Player> originalPlayers){
        this.match = sandboxMatch;
        this.originalPlayers = originalPlayers;
        update(weapon);
    }
    public List<String> getUsableEffects(){
        List<Effect> allEffects = weapon.getEffects();

        List<Effect> possiblyUsableEffects = allEffects.stream()
                .filter(effect -> !effect.getActivated())
                .filter(effect -> curPlayer.checkForAmmos(effect.getCost()))
                .collect(Collectors.toList());

        List<Integer> notUsedIndexes = possiblyUsableEffects.stream()
                .filter(effect -> !effect.getActivated())
                .map(allEffects::indexOf)
                .collect(Collectors.toList());

        Stream<String> checkAbsolute = possiblyUsableEffects.stream()
                .filter(effect -> effect.getAbsolutePriority() != 0)
                .filter(effect -> effect.getAbsolutePriority() == curEffect)
                .map(Effect::getName);

        Stream<String> checkRelativeAfter = possiblyUsableEffects.stream()
                .filter(effect-> effect.getAbsolutePriority() == 0)
                .filter(effect-> effect.getRelativePriority().contains(lastUsedIndex))
                .map(Effect::getName);

        Stream<String> checkRelativeBefore = possiblyUsableEffects.stream()
                .filter(effect -> effect.getAbsolutePriority() == 0)
                .filter(effect -> effect.getRelativePriority().stream().filter(i -> i<0).anyMatch(i->notUsedIndexes.contains(-i-1)))
                .map(Effect::getName);

        Stream<String> checkRelative = Stream.concat(checkRelativeAfter,checkRelativeBefore).distinct();

        return Stream.concat(checkAbsolute,checkRelative).collect(Collectors.toList());
    }


    void update(Weapon newWeapon) {
        curPlayer = match.getPlayers().get(match.getCurrentPlayer());
        if(curPlayer.getWeapons().contains(newWeapon) && newWeapon.getLoaded()) {
            curEffect = 1;
            lastUsedIndex = -1;
            weapon = newWeapon;
            //tell the player to select the effect
        }
        else {
            weapon = null;
            //tell the player that the missing weapon is unloaded
        }
    }

    void update(Effect effect){
        curPlayer = match.getPlayers().get(match.getCurrentPlayer());
        if(getUsableEffects().contains(effect.getName())) {
            effect.getCost().forEach(ammo -> curPlayer.getAmmos().remove(ammo));
            curEffect ++;
            effect.setActivated(true);
            lastUsedIndex = weapon.getEffects().indexOf(effect) + 1;
            if(effectController == null) {
                effectController = new EffectController(effect, weapon, match, curPlayer,originalPlayers);
                effectController.nextStep();
            }
            else{
                effectController.setCurEffect(effect);
                effectController.setCurWeapon(weapon);
                effectController.setPlayer(curPlayer);
                effectController.nextStep();
            }
        }
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Weapon getWeapon(){return weapon; }
    public EffectController getEffectController(){return effectController;}
}
