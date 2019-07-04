package it.polimi.se2019.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.network.events.EventVisitable;
import it.polimi.se2019.network.events.ConnectionRequest;
import it.polimi.se2019.network.events.EventSerializer;
import it.polimi.se2019.network.updatemessage.UpdateDeserializer;
import it.polimi.se2019.network.updatemessage.UpdateVisitable;
import it.polimi.se2019.view.UpdateVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ClientSocket, need to be inside EventUpdaterSocket and used to send events to connected server.
 */
public class ClientSocket extends Thread{
    private Socket socket;
    private BlockingQueue<EventVisitable> queue = new LinkedBlockingDeque<>();
    private UpdateVisitor updateVisitor;
    private BufferedReader jsonReader;
    private OutputStreamWriter jsonSender;
    private ThreadPoolExecutor updateExecutor;
    private Gson gson;

    /**
     * Creates the socket, sends a {@code connectionRequest}.
     * It then waits for the response,
     * @param serverIP for the socket
     * @param port for the socket
     * @param connectionRequest event containing username and password
     * @param updateVisitor visitor used for the computing of info from the server
     * @throws RemoteException if the socket doesn't answer with a success signal
     */
    public ClientSocket(String serverIP,
                        int port,
                        ConnectionRequest connectionRequest, UpdateVisitor updateVisitor) throws RemoteException{
        updateExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        this.updateVisitor = updateVisitor;
        GsonBuilder gsonBuilder;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EventVisitable.class, new EventSerializer());
        gsonBuilder.registerTypeAdapter(UpdateVisitable.class, new UpdateDeserializer());
        gson = gsonBuilder.create();
        String json;
        try {
            socket = new Socket(serverIP, port);
            jsonSender = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            jsonReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            json = gson.toJson(connectionRequest, EventVisitable.class) + "\n";
            jsonSender.write(json, 0, json.length());
            jsonSender.flush();
            json = jsonReader.readLine();
            UpdateVisitable update = gson.fromJson(json, UpdateVisitable.class);
            update.accept(updateVisitor);
        }
        catch (IOException | JsonParseException e) {
            throw new RemoteException();
        }
    }

    /**
     * Run the listener and the updater
     * */
    @Override
    public void run() {
        Listener listener = new Listener();
        Updater updater = new Updater();
        listener.start();
        updater.start();
    }

    public void addEventToQueue(EventVisitable event) {
        this.queue.add(event);
    }

    /**
     * Utility class to listen for {@link EventVisitable} added to the queue, and sending them.
     */
    private class Updater extends Thread {
        @Override
        public void run() {
            String json;
            do {
                try {
                    EventVisitable event = queue.take();
                    json = gson.toJson(event, EventVisitable.class) + "\n";
                    jsonSender.write(json, 0 ,json.length());
                    jsonSender.flush();
                }
                catch (InterruptedException e) {
                    Logger.log(Priority.DEBUG, "Queue interrupted");
                    Thread.currentThread().interrupt();
                }
                catch (IOException e) {
                    Logger.log(Priority.DEBUG, "IOException sending event");
                }
            } while (!socket.isClosed());
        }
    }


    /**
     * Utility class for listening for events received.
     * They are then accepted using {@link #updateVisitor}.
     */
    private class Listener extends Thread {
        @Override
        public void run() {
            while(!socket.isClosed()) {
                String json;
                try {
                    json = jsonReader.readLine();
                    Runnable runnable = () -> {
                        UpdateVisitable update = gson.fromJson(json, UpdateVisitable.class);
                        update.accept(updateVisitor);
                    };
                    updateExecutor.submit(runnable);
                }
                catch (IOException e) {
                    Logger.log(Priority.WARNING, "IOException receiving update");
                    throw new UnsupportedOperationException();
                }
            }
        }
    }
}
