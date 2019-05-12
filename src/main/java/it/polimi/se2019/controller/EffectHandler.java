package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;

import java.rmi.RemoteException;

public class EffectHandler extends RequestHandler{

    public EffectHandler(EventHelper eventHelper, Observer observer) {
        this.observer = observer;
    }

    @Override
    void receiveEffect(String effect) throws RemoteException {
        //TODO get effect from
    }
}
