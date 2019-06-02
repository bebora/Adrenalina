package it.polimi.se2019.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.events.ConnectionRequest;
import it.polimi.se2019.controller.events.EventSerializer;
import it.polimi.se2019.model.updatemessage.UpdateDeserializer;
import it.polimi.se2019.model.updatemessage.UpdateVisitable;
import it.polimi.se2019.view.UpdateVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * ClientSocket, need to be inside EventUpdaterSocket and used to send events to connected server.
 */
public class ClientSocket extends Thread{
    private Socket socket;
    private boolean keepAlive;
    private BlockingQueue<EventVisitable> queue = new SynchronousQueue<>();
    private UpdateVisitor updateVisitor;
    private BufferedReader jsonReader;
    private OutputStreamWriter jsonSender;
    Gson gson;

    public ClientSocket(String serverIP,
                        int port,
                        ConnectionRequest connectionRequest, UpdateVisitor updateVisitor) {
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
            //TODO change to remote exception
            throw new UnsupportedOperationException();

        }
    }

    @Override
    public void run() {
        Listener listener = new Listener();
        Updater updater = new Updater();
        listener.run();
        updater.run();
    }

    public void addEventToQueue(EventVisitable event) {
        this.queue.add(event);
    }

    private class Updater extends Thread {
        @Override
        public void run() {
            try {
                String json;
                do {
                    try {
                        json = gson.toJson(queue.take(), EventVisitable.class);
                        jsonSender.write(json, 0 ,json.length());
                        jsonSender.flush();
                    }
                    catch (InterruptedException e) {
                        //TODO exception for queue taking
                    }
                } while (!socket.isClosed());
            }
            catch (IOException e) {
                //TODO exception for socket IO, consider throwing remote
            }
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
                    //TODO LOGGER
                    throw new UnsupportedOperationException();
                }
            }
        }
    }
}
