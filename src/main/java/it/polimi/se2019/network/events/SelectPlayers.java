package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;

import java.util.*;

/**
 * Event used by view to select players it wants to select
 */
public class SelectPlayers implements EventVisitable {
    List<String> playersIds;
    public SelectPlayers(List<String> playerIds){
        this.playersIds = playerIds;
    }
    public void accept(EventVisitor visitor){
        visitor.visit(this);
    }
    public List<String> getPlayerIds(){ return playersIds;}
}
