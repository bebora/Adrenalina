package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.view.SelectableOptions;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.se2019.model.ThreeState.TRUE;

/**
 * Handles the payment of ammos.
 * Supports paying using ammos + powerups, or just powerups.
 * If the payment can't be processed because too few powerUps were sent, it notifies the observer to stop.
 * If the payment is processed, it notifies the observer to process the action after the payment.
 */
public class PaymentController extends Observer{
    private Observer observer;
    private List<Ammo> stillToPay;
    private Player curPlayer;
    private AcceptableTypes acceptableTypes;
    private TimerConstrainedEventHandler timerConstrainedEventHandler;

    @Override
    public Match getMatch() {
        return observer.getMatch();
    }

    public PaymentController(Observer observer, List<Ammo> toPay, Player curPlayer) {
        this.observer = observer;
        this.stillToPay = toPay;
        this.curPlayer = curPlayer;
    }


    /**
     * Set up the payment.
     * <li>If no ammos need to be payed, it ends.</li>
     * <li>If ammos can be payed without the use of PowerUps, it removes ammo from the player and proceeds.</li>
     * <li>If ammos can be payed with the use (needed or optional) of powerUps, it prompt the user with the needed powerUps.</li>
     */
    public void startPaying() {
        String prompt;
        //Conclude and notify observer of done payment if empty
        if (stillToPay.isEmpty()) {
            observer.concludePayment();
        }
        //Handles the payment using powerups and/or ammos.
        else {
            Set<Ammo> toPay = new HashSet<>(stillToPay);
            if (curPlayer.canDiscardPowerUp(new ArrayList<>(toPay))) {
                List<PowerUp> selectablePowerUps = curPlayer.
                        getPowerUps().
                        stream().
                        filter(p -> toPay.contains(p.getDiscardAward())).collect(Collectors.toList());
                List<ReceivingType> receivingTypes = new ArrayList<>(Arrays.asList(ReceivingType.POWERUP));
                if (curPlayer.checkForAmmos(stillToPay)) {
                    prompt = "Select a powerUp to discard if you want!";
                }
                else
                    prompt = "Select the powerUps you need to pay the remaining cost!";
                acceptableTypes = new AcceptableTypes(receivingTypes);
                acceptableTypes.setSelectablePowerUps(new SelectableOptions<>(selectablePowerUps,Math.min(selectablePowerUps.size(), stillToPay.size()), 0, prompt));

                timerConstrainedEventHandler = new TimerConstrainedEventHandler(
                        this,
                        curPlayer.getVirtualView().getRequestDispatcher(),
                        acceptableTypes);
                timerConstrainedEventHandler.start();
            }
            else {
                stillToPay.removeIf(a -> curPlayer.getAmmos().remove(a));
                if (stillToPay.isEmpty()) {
                    observer.concludePayment();
                }
                else {
                    assert false;
                }
            }
        }
    }

    /**
     * After checking that the powerUps are compatible, it concludes the payment.
     * Otherwise, the timer restarts prompting the user with the related message.
     * @param powerUps
     *
     */
    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps) {
        if (PowerUp.checkCompatibility(curPlayer, powerUps, stillToPay)) {
            powerUps.forEach(p -> curPlayer.discardPowerUp(p, true));
            stillToPay.removeIf(a -> curPlayer.getAmmos().remove(a));
            if (stillToPay.isEmpty()) {
                observer.concludePayment();
            }
            else
                updateOnStopSelection(TRUE);
        } else {
            updateOnStopSelection(TRUE);
        }
    }

    /**
     * Handles the stop for elapsed time.
     * It notifies the upper observer with a stop.
     * @param skip
     */
    @Override
    public void updateOnStopSelection(ThreeState skip) {
        observer.updateOnStopSelection(TRUE);
    }
}
