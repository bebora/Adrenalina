package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static it.polimi.se2019.model.ThreeState.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;

class DamagedControllerTest {
    private DamagedController damagedController;
    private PowerUp p1, p2;
    private Player damaging, damaged1, damaged2;


    @BeforeEach
    void prepare() {
        //Setup the sy players and the powerups
        damaged1 = spy(new Player("d1$"));
        Mockito.doNothing().when(damaged1).discardPowerUp(any(PowerUp.class), any(boolean.class));
        damaged2 = spy(new Player("d1$"));
        Mockito.doNothing().when(damaged2).discardPowerUp(any(PowerUp.class), any(boolean.class));
        damaging = spy(new Player("foo$"));
        p1 = CardCreator.parsePowerUp("tagbackGrenade.btl", Ammo.BLUE);
        p2 = CardCreator.parsePowerUp("tagbackGrenade.btl", Ammo.BLUE);

    }
    @Test
    void testDamaged() {
        //Test marks giving with tagbackGrenade
        CountDownLatch countDownLatch = new CountDownLatch(2);
        damagedController = new DamagedController(countDownLatch, damaged1, damaging, Collections.singletonList(p1));
        damagedController.updateOnPowerUps(Collections.singletonList(p1));
        assertEquals(1, countDownLatch.getCount());
        assertEquals(1, damaging.getMarks().size());
        assertEquals(damaged1, damaging.getMarks().get(0));

        //Test updateStopSelection
        damagedController.updateOnStopSelection(TRUE);
        assertEquals(0, countDownLatch.getCount());
    }

    /**
     * Test if the concurrency and asynchronous version works.
     */
    @Test
    void testConcurrency() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        class DamagedTask implements Runnable {
            DamagedController damagedController;
            PowerUp p;

            DamagedTask(DamagedController s, PowerUp p) {
                damagedController = s;
                this.p = p;
            }

            public void run() {
                damagedController.updateOnPowerUps(Collections.singletonList(p));
            }
        }
        //Setup executor and DamagedController
        ThreadPoolExecutor eventExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        DamagedController d1 = new DamagedController(countDownLatch, damaged1, damaging, Collections.singletonList(p1));
        DamagedController d2 = new DamagedController(countDownLatch, damaged2, damaging, Collections.singletonList(p2));

        //Submit the executors
        eventExecutor.submit(new DamagedTask(d1, p1));
        eventExecutor.submit(new DamagedTask(d2, p2));

        //Wait for the countDown
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            assert false;
        }
        assertEquals(2, damaging.getMarks().size());
        assertTrue(damaging.getMarks().contains(damaged1));
        assertTrue(damaging.getMarks().contains(damaged2));
    }
}