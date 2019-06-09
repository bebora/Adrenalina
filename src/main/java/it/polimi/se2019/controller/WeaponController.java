package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.SelectableOptions;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeaponController extends Observer {
    private int curEffect;
    private int lastUsedIndex;
    private Weapon weapon;
    private Match match;
    private Player curPlayer;
    private List<Player> originalPlayers;
    private EffectController effectController;
    private Effect selectedEffect;
    private ActionController actionController;
    TimerCostrainedEventHandler timerCostrainedEventHandler;
    List<Ammo> stillToPay;
    AtomicBoolean inputReceived;
    AcceptableTypes acceptableTypes;

    public WeaponController(Match sandboxMatch, Weapon weapon, List<Player> originalPlayers, ActionController actionController) {
        this.match = sandboxMatch;
        this.originalPlayers = originalPlayers;
        this.stillToPay = new ArrayList<>();
        this.actionController = actionController;
        updateOnWeapon(weapon);
    }

    public List<String> getUsableEffects() {
        List<Effect> allEffects = weapon.getEffects();

        List<Effect> possiblyUsableEffects = allEffects.stream()
                .filter(effect -> !effect.getActivated())
                .filter(effect -> curPlayer.checkForAmmos(effect.getCost(), curPlayer.totalAmmoPool()))
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
                .filter(effect -> effect.getAbsolutePriority() == 0)
                .filter(effect -> effect.getRelativePriority().contains(lastUsedIndex))
                .map(Effect::getName);

        Stream<String> checkRelativeBefore = possiblyUsableEffects.stream()
                .filter(effect -> effect.getAbsolutePriority() == 0)
                .filter(effect -> effect.getRelativePriority().stream().filter(i -> i < 0).anyMatch(i -> notUsedIndexes.contains(-i - 1)))
                .map(Effect::getName);

        Stream<String> checkRelative = Stream.concat(checkRelativeAfter, checkRelativeBefore).distinct();

        return Stream.concat(checkAbsolute, checkRelative).collect(Collectors.toList());
    }

    @Override
    public void updateOnWeapon(Weapon newWeapon) {
        curPlayer = match.getPlayers().get(match.getCurrentPlayer());
        if (curPlayer.getWeapons().contains(newWeapon) && newWeapon.getLoaded()) {
            curEffect = 1;
            lastUsedIndex = 0;
            weapon = newWeapon;
            List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.EFFECT));
            acceptableTypes = new AcceptableTypes(receivingTypes);
            List<Effect> selectableEffect = weapon.getEffects().stream().filter(e -> !e.getActivated() && curPlayer.checkForAmmos(e.getCost(), curPlayer.totalAmmoPool())).collect(Collectors.toList());
            acceptableTypes.setSelectabeEffects(new SelectableOptions<>(selectableEffect.stream().map(Effect::getName).collect(Collectors.toList()), 1, 1, "Seleziona un effetto!"));
            timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                    this,
                    curPlayer.getVirtualView().getRequestDispatcher(),
                    acceptableTypes);
            timerCostrainedEventHandler.start();
        } else if (newWeapon != null){
            weapon = null;
            curPlayer.getVirtualView().getViewUpdater().sendPopupMessage("Arma scarica");
        }
        else {
            weapon = null;
        }
    }

    @Override
    public synchronized void updateOnEffect(String effect) {
        curPlayer = match.getPlayers().get(match.getCurrentPlayer());
        selectedEffect = weapon.getEffects().stream().filter(e -> e.getName().equals(effect)).findFirst().orElse(null);
        if (selectedEffect != null) {
            curPlayer.getVirtualView().getRequestDispatcher().clear();
            stillToPay = new ArrayList<>();
            stillToPay.addAll(selectedEffect.getCost());
            if (selectedEffect.getCost().isEmpty()) {
                startEffect();
            } else if (curPlayer.canDiscardPowerUp(stillToPay)) {
                List<PowerUp> selectablePowerUps = curPlayer.
                        getPowerUps().
                        stream().
                        filter(p -> stillToPay.contains(p.getDiscardAward())).collect(Collectors.toList());
                List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP, ReceivingType.STOP));
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps,selectablePowerUps.size(), 0, "Seleziona PowerUp per pagare!"));
                timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        acceptableTypes);
                timerCostrainedEventHandler.start();
            } else if (!curPlayer.checkForAmmos(stillToPay, curPlayer.getAmmos())) {
                for (Ammo a : curPlayer.getAmmos()) {
                    if (stillToPay.remove(a))
                        curPlayer.getAmmos().remove(a);
                }
                //ask to discard for missing ammos
            } else {
                for (Ammo a : stillToPay)
                    curPlayer.getAmmos().remove(a);
                startEffect();
            }
        } else {
            throw new IncorrectEvent("Effetto non presente!");
        }
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        powerUps.forEach(p -> curPlayer.discardPowerUp(p, true));
        for (Ammo a : curPlayer.getAmmos()) {
            if (stillToPay.remove(a))
                curPlayer.getAmmos().remove(a);
        }
        if (stillToPay.isEmpty()) {
            startEffect();
        } else {
            //ask for missing ammos
        }
    }

    public void startEffect() {
        curEffect++;
        selectedEffect.setActivated(true);
        lastUsedIndex = weapon.getEffects().indexOf(selectedEffect) + 1;
        effectController = new EffectController(selectedEffect, weapon, match, curPlayer, originalPlayers, this);
        effectController.nextStep();
    }

    public void updateOnConclusion() {
        effectController = null;
        boolean modalWeaponActivated = weapon.getEffects().get(0).getAbsolutePriority() == 1
                && weapon.getEffects().get(0).getAbsolutePriority() == weapon.getEffects().get(1).getAbsolutePriority()
                && (weapon.getEffects().get(0).getActivated() || weapon.getEffects().get(1).getActivated());
        if (getUsableEffects().isEmpty() && (weapon.getEffects().get(0).getActivated() || modalWeaponActivated)) {
            actionController.updateOnConclusion();
        }
    }

    @Override
    public void updateOnStopSelection(ThreeState skip) {
        if (skip.toBoolean() || acceptableTypes.isReverse()) {
            actionController.updateOnStopSelection(skip.compare(acceptableTypes.isReverse()));
        } else {
            boolean modalWeaponActivated = weapon.getEffects().get(0).getAbsolutePriority() == 1
                    && weapon.getEffects().get(0).getAbsolutePriority() == weapon.getEffects().get(1).getAbsolutePriority()
                    && (weapon.getEffects().get(0).getActivated() || weapon.getEffects().get(1).getActivated());
            if (weapon.getEffects().get(0).getActivated() || modalWeaponActivated) {
                actionController.updateOnConclusion();
            } else {
                //tell the player he must use the main effect or takeback the action
            }
        }
    }


    public void setMatch(Match match) {
        this.match = match;
    }

    public void setOriginalPlayers(List<Player> originalPlayers) {
        this.originalPlayers = originalPlayers;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public EffectController getEffectController() {
        return effectController;
    }
}
