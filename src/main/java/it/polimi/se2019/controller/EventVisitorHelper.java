package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventVisitorHelper {
    private Match match;

    public Player getSinglePlayerFromId(String id){
        return match.getPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public List<Player> getPlayersFromId(List<String> id){
        List<Player> temp = new ArrayList<>();
        id.forEach(i -> temp.add(getSinglePlayerFromId(i)));
        temp.removeAll(Collections.singleton(null));
        if(temp.isEmpty()){
            //TODO:tell the player that he didn't send valid targets
        }
        return temp;
    }



}
