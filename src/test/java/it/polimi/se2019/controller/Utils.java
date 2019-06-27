package it.polimi.se2019.controller;

import static java.lang.Thread.sleep;

public class Utils {
    public static void waitABit() {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            assert false;
        }
    }
}
