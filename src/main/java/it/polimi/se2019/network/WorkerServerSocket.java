package it.polimi.se2019.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.EventDeserializer;
import it.polimi.se2019.model.updatemessage.UpdateSerializer;
import it.polimi.se2019.model.updatemessage.UpdateVisitable;
import it.polimi.se2019.view.VirtualView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


//TODO implement heartbeat for disconnection
public class WorkerServerSocket extends Thread {
    private Socket socket;
    private VirtualView virtualView;
    private BufferedReader jsonReader;
    private OutputStreamWriter jsonSender;
    private Gson gson;
    BlockingQueue queue = new LinkedBlockingDeque();
    private EventVisitor eventVisitor;

    public WorkerServerSocket(Socket socket, LobbyController lobbyController) {
        GsonBuilder gsonBuilder;
        this.socket = socket;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EventVisitable.class, new EventDeserializer());
        gsonBuilder.registerTypeAdapter(UpdateVisitable.class, new UpdateSerializer());
        gson = gsonBuilder.create();
        String json;
        try {
            jsonSender = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            jsonReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            json = jsonReader.readLine();
        }
        catch (IOException e) {
            //TODO LOGGER
            throw new UnsupportedOperationException();
        }
        EventVisitable event = gson.fromJson(json, EventVisitable.class);
        try {
            ConnectionRequest connection = (ConnectionRequest) event;
            virtualView = new VirtualView(lobbyController);
            ViewUpdater viewUpdater = new ViewUpdaterSocket(this);
            virtualView.setViewUpdater(viewUpdater);
            String username = connection.getUsername();
            String password = connection.getPassword();
            String mode = connection.getMode();
            boolean signingUp = connection.getExistingGame();
            if (signingUp)
                lobbyController.connectPlayer(username,password,mode, virtualView);
            else
                lobbyController.reconnectPlayer(username,password,virtualView);
            eventVisitor = new EventVisitor(virtualView.getRequestDispatcher(), lobbyController);
        }
        catch (ClassCastException e){
            event = null;
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
        String json = gson.toJson(update, UpdateVisitable.class);
        try {
            queue.put(json);
        }
        catch (InterruptedException e) {
            //TODO insert logger class to log exceptions
        }
    }

    private class Updater extends Thread {
            @Override
            public void run() {
                try {
                    String json;
                    do {
                        try {
                            json = (String) queue.take();
                            jsonSender.write(json, 0, json.length());
                            jsonSender.flush();
                        }
                        catch (InterruptedException e) {
                            Logger.log(Priority.ERROR, e.toString());
                        }
                    }
                    //TODO edit conditions to run
                    while (!socket.isClosed());
                }
                catch (IOException e) {
                    Logger.log(Priority.ERROR, e.toString());
                }
            }
    }

    private class Listener extends Thread {
        @Override
        public void run() {
           //TODO edit conditions to run
            while(!socket.isClosed()) {
                String json;
                try {
                    json = jsonReader.readLine();
                    EventVisitable event = gson.fromJson(json, EventVisitable.class);
                    event.accept(eventVisitor);
                }
                catch (IOException e) {
                    //TODO LOGGER
                    throw new UnsupportedOperationException();
                }
            }
        }
    }
}
