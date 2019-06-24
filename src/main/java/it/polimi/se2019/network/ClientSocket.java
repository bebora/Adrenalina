package it.polimi.se2019.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.EventSerializer;
import it.polimi.se2019.controller.updatemessage.UpdateDeserializer;
import it.polimi.se2019.controller.updatemessage.UpdateVisitable;
import it.polimi.se2019.view.UpdateVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * ClientSocket, need to be inside EventUpdaterSocket and used to send events to connected server.
 */
public class ClientSocket extends Thread{
    private Socket socket;
    private BlockingQueue<EventVisitable> queue = new LinkedBlockingDeque<>();
    private UpdateVisitor updateVisitor;
    private BufferedReader jsonReader;
    private OutputStreamWriter jsonSender;
    Gson gson;

    public void closeSocket() {
        try {
            socket.close();
        }
        catch (IOException e) {
            Logger.log(Priority.DEBUG, "Can't close the socket");
        }
    }

    public ClientSocket(String serverIP,
                        int port,
                        ConnectionRequest connectionRequest, UpdateVisitor updateVisitor) throws RemoteException{
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
        catch (IOException e) {
            throw new RemoteException();
        }
    }

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
                        Logger.log(Priority.WARNING, "Queue interrupted");
                    }
                    catch (IOException e) {
                        Logger.log(Priority.WARNING, "IOException sending event");
                    }
                } while (!socket.isClosed());
            }
    }


    private class Listener extends Thread {
        @Override
        public void run() {
            while(!socket.isClosed()) {
                String json;
                try {
                    json = jsonReader.readLine();
                    UpdateVisitable update = gson.fromJson(json, UpdateVisitable.class);
                    update.accept(updateVisitor);
                }
                catch (IOException e) {
                    Logger.log(Priority.WARNING, "IOException receiving update");
                    throw new UnsupportedOperationException();
                }
            }
        }
    }
}
