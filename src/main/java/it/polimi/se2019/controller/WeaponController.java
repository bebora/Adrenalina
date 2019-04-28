package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.EventWrapper;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.Weapon;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeaponController extends Thread {
    private int curEffect;
    private int lastUsedIndex;
    private Weapon weapon;
    private Match match;
    private Player curPlayer;

    public WeaponController(Match match, Weapon weapon){
        this.match = match;
        this.weapon = weapon;
        this.curEffect = 1;
        this.lastUsedIndex = -1;
        this.curPlayer = match.getPlayers().get(match.getCurrentPlayer());
    }
    public List<String> getUsableEffects(){
        List<Effect> allEffects = weapon.getEffects();

        Stream<Effect> possiblyUsableEffects = allEffects.stream()
                .filter(effect -> !effect.getActivated())
                .filter(effect -> curPlayer.checkForAmmos(effect.getCost()));

        List<Integer> notUsedIndexes = allEffects.stream()
                .filter(effect -> !effect.getActivated())
                .map(allEffects::indexOf)
                .collect(Collectors.toList());

        Stream<String> checkAbsolute = possiblyUsableEffects
                .filter(effect -> effect.getAbsolutePriority() != 0)
                .filter(effect -> effect.getAbsolutePriority() == curEffect)
                .map(Effect::getName);

        Stream<String> checkRelativeAfter = possiblyUsableEffects
                .filter(effect-> effect.getAbsolutePriority() == 0)
                .filter(effect-> effect.getRelativePriority().contains(lastUsedIndex))
                .map(Effect::getName);

        Stream<String> checkRelativeBefore = possiblyUsableEffects
                .filter(effect -> effect.getAbsolutePriority() == 0)
                .filter(effect -> effect.getRelativePriority().stream().filter(i -> i<0).anyMatch(i->notUsedIndexes.contains(-i-1)))
                .map(Effect::getName);

        Stream<String> checkRelative = Stream.concat(checkRelativeAfter,checkRelativeBefore).distinct();

        return Stream.concat(checkAbsolute,checkRelative).collect(Collectors.toList());
    }


    void update(EventWrapper e) {

    }
}
