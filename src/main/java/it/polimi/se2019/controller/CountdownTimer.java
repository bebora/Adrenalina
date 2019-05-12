package it.polimi.se2019.controller;

import it.polimi.se2019.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class CountdownTimer {
    long start;
    int time;
    AtomicBoolean active;
    Observer observer;

    public CountdownTimer(long start, int time, Observer observer) {
        this.start = start;
        this.time = time;
        active = new AtomicBoolean(true);
        this.observer = observer;
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= start + time*1000;
    }

    public AtomicBoolean getActive() {
        return active;
    }

    public void stop() {
        active.set(false);
    }

    //TODO method to do next turn from here and run this synchronized in the while of the observers

}
