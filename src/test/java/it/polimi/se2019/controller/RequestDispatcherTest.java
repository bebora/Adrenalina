package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.network.EventUpdaterRMI;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;


//TODO @simone this is the template
public class RequestDispatcherTest {
    VirtualView a;
    VirtualView b;
    VirtualView c;
    EventUpdater eventUpdater;
    @Test
    public void prepare() {
        LobbyController lobbyController = new LobbyController(Arrays.asList(Mode.NORMAL));
        List<VirtualView> views = new ArrayList<>();
        VirtualView tempA = new VirtualView(lobbyController);
        VirtualView tempB = new VirtualView(lobbyController);
        VirtualView tempC = new VirtualView(lobbyController);
        a = spy(tempA);
        b = spy(tempB);
        c = spy(tempC);
        for (VirtualView v : views) {
            doNothing().when(v).refresh();
            doNothing().when(v).disconnect();
            doNothing().when(v).printWinners(any());
        }
        eventUpdater = new EventUpdaterRMI(lobbyController);
        try {
            eventUpdater.login(a, "fabio", "rizzo", false, "NORMAL");
        } catch (RemoteException e) {
            assert false;
        }
    }
}
