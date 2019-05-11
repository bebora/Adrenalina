package it.polimi.se2019.controller;

/**
 * Interface exposed by the RMI server to the client so that it can request to connect
 */
public interface ConnectInterface {
    void connect(String username, String salt, String password, boolean signingUp, String mode);
}
