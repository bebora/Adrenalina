package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.updatemessage.PopupMessageUpdate;
import it.polimi.se2019.view.ViewTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventVisitorHelper {
    private Match match;

    private Player getSinglePlayerFromId(String id){
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
            PopupMessageUpdate message = new PopupMessageUpdate("The targets sent are not valid");
        }
        return temp;
    }

    public List<Tile> getTilesFromViewTiles(List<ViewTile> viewTiles, Board board){
        return viewTiles.stream()
                .map(v -> board.getTile(v.getCoords().getPosy(),v.getCoords().getPosx()))
                .collect(Collectors.toList());
    }


}
