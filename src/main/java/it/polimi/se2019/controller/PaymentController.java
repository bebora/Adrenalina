package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.view.SelectableOptions;

import java.util.*;
import java.util.stream.Collectors;

public class PaymentController extends Observer{
    private Observer observer;
    private List<Ammo> stillToPay;
    private Set<Ammo> ammoTypes;

    private Player curPlayer;
    private AcceptableTypes acceptableTypes;
    private TimerCostrainedEventHandler timerCostrainedEventHandler;

    public PaymentController(Observer observer, List<Ammo> toPay, Player curPlayer) {
        this.observer = observer;
        this.stillToPay = toPay;
        this.curPlayer = curPlayer;
    }


    public void startPaying() {
        String prompt;
        if (stillToPay.isEmpty()) {
            observer.concludePayment();
        }
        else {
            Set<Ammo> toPay = new HashSet<>(stillToPay);
            stillToPay.removeIf(a -> curPlayer.getAmmos().remove(a));
            if (curPlayer.canDiscardPowerUp(new ArrayList<>(toPay))) {
                List<PowerUp> selectablePowerUps = curPlayer.
                        getPowerUps().
                        stream().
                        filter(p -> toPay.contains(p.getDiscardAward())).collect(Collectors.toList());
                List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP, ReceivingType.STOP));
                if (stillToPay.isEmpty()) {
                    prompt = "Select a powerUp to discard if you want!";
                }
                else
                    prompt = "Select the powerUps you need to pay the remaining cost!";
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps,selectablePowerUps.size(), 0, prompt));

                timerCostrainedEventHandler = new TimerCostrainedEventHandler(
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        acceptableTypes);
                timerCostrainedEventHandler.start();
            }
            else if (stillToPay.isEmpty()) {
                observer.concludePayment();
            }
            else {
                assert false;
            }
        }
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        if (PowerUp.checkCompatibility(powerUps, stillToPay)) {
            curPlayer.getVirtualView().getRequestDispatcher().clear();
            powerUps.forEach(p -> curPlayer.discardPowerUp(p, true));
            curPlayer.getAmmos().removeIf(a -> stillToPay.remove(a));
            if (stillToPay.isEmpty()) {
                observer.concludePayment();
            }
        } else {
            throw new IncorrectEvent("Error! Not enough ammos to pay!");
        }
    }
}
