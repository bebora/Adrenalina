package it.polimi.se2019.controller;

/**
 * Types that define what the RequestDispatcher can accept.
 */
public enum ReceivingType {
    ACTION,
    DIRECTION,
    EFFECT,
    PLAYERS,
    POWERUP,
    RESET, //reset=stop in other parts of the code
    ROOM,
    TILES,
    WEAPON,
}
