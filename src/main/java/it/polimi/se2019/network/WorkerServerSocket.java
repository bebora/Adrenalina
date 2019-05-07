package it.polimi.se2019.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.MessageHandler;
import it.polimi.se2019.controller.events.EventDeserializer;
import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.controller.events.VisitorVirtualViewSetter;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.updatemessage.UpdateVisitable;
import it.polimi.se2019.view.View;
import it.polimi.se2019.view.VirtualView;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class WorkerServerSocket extends Thread implements ServerInterface {
    private Socket socket;
    private ObjectOutputStream oos;

    private ObjectInputStream ois;
    private View VirtualView;
    private Player player;
    private LobbyController lobbyController;
    private MessageHandler messageHandler;
    private GsonBuilder gsonBuilder;
    BlockingQueue queue = new LinkedBlockingDeque();

    public WorkerServerSocket(Socket socket, LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        this.socket = socket;
        this.gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EventVisitable.class, new EventDeserializer());
        Gson gson = gsonBuilder.create();
        String json;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());
            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            json = jsonReader.readLine();
        }
        catch (IOException e) {
            //TODO LOGGER
            throw new UnsupportedOperationException();
        }
        EventVisitable event = gson.fromJson(json, EventVisitable.class);
        try {
            VirtualView virtualView = new VirtualView(this);
            EventVisitor virtualViewSetter = new VisitorVirtualViewSetter(virtualView);
            event.accept(virtualViewSetter);
            event.accept(lobbyController);
        }
        catch (IncorrectEvent e){
            //TODO LOG INCORRECT EVENT
        }
        if (event == null) {
            //TODO send update popup, wrong message
            try {
                socket.close();
            }
            catch (IOException e) {
                //TODO LOGGER
                throw new AuthenticationErrorException();
            }
        }
    }


    @Override
    public void run() {
        //TODO run listener and sender
    }

    public void update(UpdateVisitable update) {
        //TODO serialize update

        /*try {
            //queue.put(serializedUpdate);
        }
        catch (InterruptedException e) {
            //TODO insert logger class to log exceptions
        }
    }*/
    }

    private class Updater extends Thread {
            @Override
            public void run() {
                //TODO take message from queue and send them using oos
            }
    }

    private class Listener extends Thread {
        @Override
        public void run() {
            //TODO Get messages, parse them and use messageHandler relative visitor
        }
    }


}
