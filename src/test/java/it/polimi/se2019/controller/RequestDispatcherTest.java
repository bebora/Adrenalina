package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.network.EventUpdaterRMI;
import it.polimi.se2019.network.ViewUpdaterRMI;
import it.polimi.se2019.view.ConcreteViewReceiver;
import it.polimi.se2019.view.SelectableOptions;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RequestDispatcherTest {
    VirtualView a;
    EventUpdater eventUpdaterA;
    RequestDispatcher requestDispatcher;

    @BeforeEach
    public void prepare() {
        LobbyController lobbyController = new LobbyController(Arrays.asList(Mode.NORMAL));
        List<VirtualView> views = new ArrayList<>();
        VirtualView tempA = new VirtualView(lobbyController);
        a = spy(tempA);
        doNothing().when(a).refresh();
        doNothing().when(a).disconnect();
        doNothing().when(a).printWinners(any());
        eventUpdaterA = new EventUpdaterRMI(lobbyController);
        try {
            a.setViewUpdater(new ViewUpdaterRMI(new ConcreteViewReceiver(a), a), false);
            eventUpdaterA.login(a, "fabio", "rizzo", false, "NORMAL");
        } catch (RemoteException e) {
            assert false;
        }
        requestDispatcher = lobbyController.getRequestHandler("fabio$"+"rizzo".hashCode());
    }

    @Test
    public void testSelectableOptions() {
        AcceptableTypes acceptableTypes = new AcceptableTypes(Collections.singletonList(ReceivingType.AMMO));
        List<Ammo> ammos = Arrays.asList(Ammo.RED, Ammo.BLUE, Ammo.YELLOW);
        acceptableTypes.setSelectableAmmos(new SelectableOptions<>(ammos, 1, 1, "Select ammo!"));
        a.getViewUpdater().sendAcceptableType(acceptableTypes);
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e){
            assert false;
        }
        assertTrue(a.getSelectableOptionsWrapper().getAcceptedTypes().contains(ReceivingType.AMMO));
        assertEquals(a.getSelectableOptionsWrapper().getSelectableAmmos().getOptions(), ammos.stream().map(Enum::toString).collect(Collectors.toList()));
    }

    @Test
    public void testEventSend() throws RemoteException {
        //Verify that not supported eventHandlers can't be called
        requestDispatcher.receiveAmmo("RED");
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e){
            assert false;
        }
        assertFalse(a.getMessages().isEmpty());

        //Verify that eventHandler get called once supported
        EventHandler observer = mock(EventHandler.class);
        doNothing().when(observer).receiveAmmo(any());
        requestDispatcher.addReceivingType(Collections.singletonList(ReceivingType.AMMO), observer);
        requestDispatcher.receiveAmmo("RED");
        verify(observer, times(1)).receiveAmmo(any());

        //Test wrong ammo name
        requestDispatcher.receiveAmmo("WRONG");
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e){
            assert false;
        }
        assertEquals(2, a.getMessages().size());
    }

}
