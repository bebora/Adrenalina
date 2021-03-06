package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.cards.PowerUp;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Handles the use of a {@link PowerUp} with {@link PowerUp#applicability} equal to DAMAGED
 * Handles effects with an arbitrary amount of marks and/or damages.
 * Notifies the controller once it's ended thanks to the countDownLatch.
 */
public class DamagedController extends Observer {
    private Player damagedPlayer;
    private Player damagingPlayer;
    private List<PowerUp> applicable;
    private CountDownLatch countDownLatch;

    @Override
    public Match getMatch() {
        return damagingPlayer.getMatch();
    }

    /**
     * @param countDownLatch latch notified when the DamagedController finishes its actions
     * @param damaged player damaged that is activating the powerUp
     * @param damaging player damaging that is gonna receive the shot from the damaged player
     * @param applicable powerUps that can be used by the {@link #damagedPlayer}
     */
    public DamagedController(CountDownLatch countDownLatch, Player damaged, Player damaging, List<PowerUp> applicable) {
        this.damagedPlayer = damaged;
        this.damagingPlayer = damaging;
        this.applicable = applicable;
        this.countDownLatch = countDownLatch;
    }

    /**
     * Handles receiving powerUps from the client, checking if valid and damaging the related player.
     * @param powerUps chosen cards to use
     */
    @Override
    public void updateOnPowerUps(List<PowerUp> powerUps) {
        for (PowerUp p : powerUps) {
            if (applicable.contains(p)) {
                if (getMatch() != null)
                    getMatch().updatePopupViews(String.format("%s use %s against %s!",
                        damagedPlayer.getUsername(),
                        p.getName(),
                        damagingPlayer.getUsername()));
                damagedPlayer.discardPowerUp(p, false);
                damagingPlayer.receiveShot(damagedPlayer,p.getEffect().getDamages().get(0).getDamagesAmount(),p.getEffect().getDamages().get(0).getMarksAmount(), false);
            }
        }
        countDownLatch.countDown();
    }

    /**
     * Handles receiving a stop in case of end of turn.
     * @param skip
     */
    @Override
    public void updateOnStopSelection(ThreeState skip) {
        countDownLatch.countDown();
    }
}
