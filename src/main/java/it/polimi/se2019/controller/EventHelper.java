package it.polimi.se2019.controller;

import it.polimi.se2019.controller.events.IncorrectEvent;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class with useful methods to parse the content of event messages.
 */
public class EventHelper {
    private Match match;


    public EventHelper(Match match) {
        this.match = match;
    }
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
        if(temp.isEmpty())
            throw new IncorrectEvent("Player ID not correct");
        return temp;
    }

    public List<Tile> getTilesFromViewTiles(List<ViewTileCoords> viewTiles){
        Board board = match.getBoard();
        return viewTiles.stream()
                .map(v -> board.getTile(v.getPosy(),v.getPosx()))
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
            throw new IncorrectEvent("Arma non presente");
        }
        return realWeapon;
    }

    public Color getRoomFromString(String room) {
        Set<Color> colors = match.getBoard().getTiles().stream().flatMap(List::stream).map(t -> t.getRoom()).collect(Collectors.toSet());
        try {
            Color relatedColor = Color.valueOf(room);
            if (colors.contains(relatedColor))
                return relatedColor;
            else throw new IncorrectEvent("Il colore non è presente nella mappa!");
        }
        catch (IllegalArgumentException e) {
            throw new IncorrectEvent("Il colore è sbagliato!");
        }
    }

    public Action getActionFromString(String action) {
        List<Action> actions = match.getPlayers().get(match.getCurrentPlayer()).getActions();
        return  actions.stream().
                filter(a -> a.toString() == action).findFirst().orElseThrow(() -> new IncorrectEvent("Azione non presente!"));
    }

    /**
     * Get related powerup from the current Player
     * @param powerup
     * @return
     */
    public PowerUp getPowerUpFromViewPowerUp(ViewPowerUp powerup) {
       return match.getPlayers().
               get(match.getCurrentPlayer()).
               getPowerUps().
               stream().
                filter(p -> p.getName() == powerup.getName() && p.getDiscardAward().toString() == powerup.getDiscardAward()).
               findAny().orElse(null);
    }

    public Effect getEffectFromString(String effect) {
        //TODO add a way to find the current weapon used by the player
        //return match.getPlayers().
                //get(match.getCurrentPlayer()).
        return null;
    }
}
