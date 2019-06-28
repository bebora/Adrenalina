package it.polimi.se2019.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.controller.LobbyController;
import it.polimi.se2019.controller.VirtualView;
import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.EventDeserializer;
import it.polimi.se2019.controller.updatemessage.PingUpdate;
import it.polimi.se2019.controller.updatemessage.UpdateSerializer;
import it.polimi.se2019.controller.updatemessage.UpdateVisitable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static it.polimi.se2019.Priority.DEBUG;
import static it.polimi.se2019.Priority.WARNING;

/**
 * Handles each client, containing information needed to:
 * <li>Receiving events and applying them to the related Game</li>
 * <li>Sending updates to them, converted in json files.</li>
 */
public class WorkerServerSocket extends Thread {
    private Socket socket;
    private VirtualView virtualView;
    private BufferedReader jsonReader;
    private OutputStreamWriter jsonSender;
    private Gson gson;
    BlockingQueue queue = new LinkedBlockingDeque();
    private EventVisitor eventVisitor;

    /**
     * Creates a Worker for a client.
     * Setup the Serializer and Deserializer.
     * Setup the virtualView, and asks to {@code lobbyController} to process the infos.
     * @param socket
     * @param lobbyController
     */
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
            Logger.log(WARNING, "Can't get the message");
            throw new AuthenticationErrorException();
        }
        EventVisitable event;
        try {
            event = gson.fromJson(json, EventVisitable.class);
        }catch (JsonParseException e) {
            throw new AuthenticationErrorException();
        }
        try {
            //Expects a connection event
            ConnectionRequest connection = (ConnectionRequest) event;
            //Create the VirtualView and the related ViewUpdater
            virtualView = new VirtualView(lobbyController);
            ViewUpdater viewUpdater = new ViewUpdaterSocket(this);
            String username = connection.getUsername();
            virtualView.setUsername(username);
            String password = connection.getPassword();
            String mode = connection.getMode();
            boolean existingGame = connection.getExistingGame();
            virtualView.setViewUpdater(viewUpdater, existingGame);
            //Notifies the lobbyController with a new connection.
            if (!existingGame)
                lobbyController.connectPlayer(username, password, mode, virtualView);
            else
                lobbyController.reconnectPlayer(username,password,virtualView);
            virtualView.setOnline(true);
            eventVisitor = new EventVisitor(virtualView.getRequestDispatcher(), lobbyController);
        }
        catch (ClassCastException e){
            Logger.log(WARNING, "Wrong message");
            try {
                socket.close();
            }
            catch (IOException socketClosing) {
                Logger.log(WARNING, "Socket closing");
                throw new AuthenticationErrorException();
            }
            throw new AuthenticationErrorException();
        }
    }

    /**
     * Runs the updater and the listener.
     * It runs a Pinger to send it to the client.
     */
    @Override
    public void run() {
        Updater updater = new Updater();
        updater.start();
        Listener listener = new Listener();
        listener.start();
        Ping ping = new Ping();
        ping.start();

    }

    public synchronized void update(UpdateVisitable update) {
        String json = gson.toJson(update, UpdateVisitable.class);
        try {
            queue.put(json);
        }
        catch (InterruptedException e) {
            Logger.log(DEBUG, "Can't update Queue");
        }
    }

    /**
     * Utility class to send updates to the view, polling them from the queue.
     */
    private class Updater extends Thread {
            @Override
            public void run() {
                try {
                    do {
                        update();
                    }
                    while (virtualView.isOnline());
                }
                catch (IOException e) {
                    Logger.log(Priority.ERROR, e.toString());
                    Logger.log(Priority.ERROR, "PLAYER DISCONNECTED (socket)" + virtualView.getUsername());
                    virtualView.setOnline(false);
                }
            }

            public void update() throws IOException {
                try {
                    String json = queue.take() + "\n";
                    jsonSender.write(json, 0, json.length());
                    jsonSender.flush();
                }
                catch (InterruptedException e) {
                    Logger.log(Priority.ERROR, e.toString());
                }
            }
    }

    /**
     * Utility class listening for new events sent by the client.
     * They are visited using the {@link #eventVisitor}.
     */
    private class Listener extends Thread {
        private String json;
        private EventVisitable event;
        @Override
        public void run() {
            while(!socket.isClosed() && virtualView.isOnline()) {
                try {
                    json = jsonReader.readLine();
                    if (json != null) {
                        event = gson.fromJson(json, EventVisitable.class);
                        event.accept(eventVisitor);
                    }
                }
                catch (IOException | ClassCastException | NullPointerException e) {
                    if (!e.getMessage().equals("Connection reset"))
                        Logger.log(DEBUG, "Can't read, wrong event: " + e.getMessage());
                }
            }
        }
    }

    private class Ping extends Thread {
        @Override
        public void run() {
            UpdateVisitable ping = new PingUpdate();
            while (!socket.isClosed()) {
                update(ping);
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    Logger.log(WARNING,"Interrupted");
                    virtualView.setOnline(false);
                }
            }
        }
    }
}
