package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ThreeState;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.network.ViewUpdaterRMI;
import it.polimi.se2019.view.ConcreteViewReceiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.se2019.model.ammos.Ammo.BLUE;
import static it.polimi.se2019.model.ammos.Ammo.RED;
import static it.polimi.se2019.model.ammos.Ammo.YELLOW;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class PaymentControllerTest {
    private Player testPlayer;
    private PaymentTest paymentTest = spy(new PaymentTest());
    private RequestDispatcher requestDispatcher;

    class PaymentTest extends Observer {
        @Override
        public Match getMatch() {
            return mock(Match.class);
        }

        @Override
        public void concludePayment() {}

        @Override
        public void updateOnStopSelection(ThreeState skip) {}
    }

    @BeforeEach
    void setUp() throws RemoteException {
        testPlayer = new Player("cavodiRAME");
        requestDispatcher = new RequestDispatcher(mock(ViewUpdater.class), mock(VirtualView.class));
        requestDispatcher = spy(requestDispatcher);
        VirtualView view = new VirtualView();
        ViewUpdater viewUpdater = null;
        try {
            viewUpdater = new ViewUpdaterRMI(new ConcreteViewReceiver(view), view);
        }
        catch (RemoteException e) {
            System.out.println("Unable to create ViewReceiver");
        }
        testPlayer.setVirtualView(new VirtualView(new LobbyController(new ArrayList<>(Arrays.asList(Mode.NORMAL)))));
        testPlayer.getVirtualView().setViewUpdater(viewUpdater, false);
        testPlayer.getVirtualView().setRequestDispatcher(requestDispatcher);
        GameController gameController = new GameController(Arrays.asList(testPlayer),"board1" +".btlb",5,false, null);
    }

    @Test
    void payWithOnlyPowerUp() {
        testPlayer.getPowerUps().clear();
        //Add powerup
        PowerUp testPowerUp = CardCreator.parsePowerUp("newton.btl", RED);
        testPlayer.addPowerUp(testPowerUp, true);
        List<Ammo> toPay = new ArrayList<>();
        toPay.add(RED);
        PaymentController paymentController = new PaymentController(paymentTest, toPay, testPlayer);
        paymentController.startPaying();
        Utils.waitABit();
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.POWERUP);
        eventHandler.receivePowerUps(Arrays.asList(testPowerUp));
        Utils.waitABit();
        assertEquals(0, testPlayer.getPowerUps().size());
        Mockito.verify(paymentTest).concludePayment();
    }

    @Test
    void payWithAmmos() {
        List<Ammo> toPay = new ArrayList<>(Arrays.asList(RED, BLUE, YELLOW));
        PaymentController paymentController = new PaymentController(paymentTest, toPay, testPlayer);
        paymentController.startPaying();
        Utils.waitABit();
        Utils.waitABit();
        assertEquals(0, testPlayer.getPowerUps().size());
        Mockito.verify(paymentTest).concludePayment();
    }

    @Test
    void testPayWithBoth() {
        //Test if you need to
        List<Ammo> toPay = new ArrayList<>(Arrays.asList(RED, RED));
        PowerUp testPowerUp = CardCreator.parsePowerUp("teleporter.btl", RED);
        testPlayer.addPowerUp(testPowerUp, true);
        PaymentController paymentController = new PaymentController(paymentTest, toPay, testPlayer);
        paymentController.startPaying();
        Utils.waitABit();
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.POWERUP);
        eventHandler.receivePowerUps(Arrays.asList(testPowerUp));
        Utils.waitABit();
        assertEquals(0, testPlayer.getPowerUps().size());
        Mockito.verify(paymentTest).concludePayment();
    }

    @Test
    void testOptionalBoth() {
        List<Ammo> toPay = new ArrayList<>(Arrays.asList(RED));
        PowerUp testPowerUp = CardCreator.parsePowerUp("tagbackGrenade.btl", RED);
        testPlayer.addPowerUp(testPowerUp, true);
        PaymentController paymentController = new PaymentController(paymentTest, toPay, testPlayer);
        paymentController.startPaying();
        Utils.waitABit();
        EventHandler eventHandler = requestDispatcher.getObserverTypes().get(ReceivingType.POWERUP);
        eventHandler.receivePowerUps(new ArrayList<>());
        Utils.waitABit();
        assertEquals(1, testPlayer.getPowerUps().size());
        Mockito.verify(paymentTest).concludePayment();
    }
}