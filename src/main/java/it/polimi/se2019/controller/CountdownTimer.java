package it.polimi.se2019.controller;

import java.util.concurrent.atomic.AtomicBoolean;

public class CountdownTimer {
    long start;
    int time;
    AtomicBoolean active;

    public CountdownTimer(long start, int time) {
        this.start = start;
        this.time = time;
        active = new AtomicBoolean(true);
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

}
