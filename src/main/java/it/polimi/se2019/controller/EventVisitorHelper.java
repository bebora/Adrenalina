package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.model.updatemessage.PopupMessageUpdate;
import it.polimi.se2019.view.ViewTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class with useful methods to parse the content of event messages.
 */
public class EventVisitorHelper {
    private Match match;

    /**
     * Return a Player with the given id.
     * If there is no corresponding player in the current match return null.
     * @param id a String representing a Player Id to be parsed
     * @return a Player with the corresponding id if exists, null otherwise.
     */
    private Player getSinglePlayerFromId(String id){
        return match.getPlayers().stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    /**
     * Return a list of Player corresponding to the given ids.
     * This method is used to parse the player from a SelectPlayers event.
     * If there is no valid id signals the problem to the client.
     * @param id a list of Player Id to be parsed
     * @return  a list of Player with the corresponding ids,
     * an empty list if there are no valid players
     */
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

    /**
     * Return the corresponding Weapon if the current player can grab it or already has it,
     * otherwise signals the problem to the client. This method is used to parse the content from
     * a SelectWeapon event.
     * @param weapon the name of the weapon to be parsed
     * @return the selected Weapon or null if the Weapon is not accessible
     */
    public Weapon getWeaponFromString(String weapon){
        Player curPlayer = match.getPlayers().get(match.getCurrentPlayer());
        Weapon realWeapon = curPlayer.getWeapons().stream()
                .filter(w -> w.getName().equals(weapon))
                .findAny().orElse(
                        curPlayer.getTile().getWeapons().stream()
                        .filter(w -> w.getName().equals(weapon))
                        .findAny().orElse(null));
        if(realWeapon == null){
            //mandare effettivamente il messaggio alla view
            PopupMessageUpdate error = new PopupMessageUpdate("The player should not have access to this weapon");
        }
        return realWeapon;
    }


}
