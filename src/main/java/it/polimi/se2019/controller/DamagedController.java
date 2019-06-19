package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.cards.PowerUp;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DamagedController extends Observer {
    private Player damagedPlayer;
    private Player damagingPlayer;
    private List<PowerUp> applicable;
    private CountDownLatch countDownLatch;

    public DamagedController(CountDownLatch countDownLatch, Player damaged, Player damaging, List<PowerUp> applicable) {
        this.damagedPlayer = damaged;
        this.damagingPlayer = damaging;
        this.applicable = applicable;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps, boolean discard) {
        damagedPlayer.getVirtualView().getRequestDispatcher().clear();
        for (PowerUp p : powerUps) {
            if (applicable.contains(p)) {
                damagedPlayer.discardPowerUp(p, false);
                damagingPlayer.receiveShot(damagedPlayer,p.getEffect().getDamages().get(0).getDamagesAmount(),p.getEffect().getDamages().get(0).getMarksAmount(), false);
            }
        }
        countDownLatch.countDown();
    }

    @Override
    public void updateOnStopSelection(ThreeState skip) {
        damagedPlayer.getVirtualView().getRequestDispatcher().clear();
        countDownLatch.countDown();
    }
}
