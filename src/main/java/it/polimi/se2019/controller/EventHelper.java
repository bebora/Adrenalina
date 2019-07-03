package it.polimi.se2019.controller;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.network.events.IncorrectEventException;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
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
 * Helper class with useful methods to parse the content of event messages.
 */
public class EventHelper {
    private Match match;
    private Player player;


    public EventHelper(Match match, Player player) {
        this.match = match;
        this.player = player;
    }

    /**
     * Return a Player with the given id.
     * If there is no corresponding player in the current match return null.
     * @param username a String representing a Player Id to be parsed
     * @return a Player with the corresponding id if exists, null otherwise.
     */
    private Player getSinglePlayerFromUsername(String username){
        return match.getPlayers().stream()
                .filter(p -> p.getUsername().equals(username))
                .findAny()
                .orElse(null);
    }

    /**
     * @param ammo chosen ammos
     * @return the Ammo related to the sent string
     */
    public static Ammo getAmmoFromString(String ammo) {
        try {
            return Ammo.valueOf(ammo);
        }
        catch (IllegalArgumentException e) {
            throw new IncorrectEventException("Ammo doesn't exist!");
        }
    }

    /**
     * Return a list of Player corresponding to the given ids.
     * This method is used to parse the player from a SelectPlayers event.
     * If there is no valid id signals the problem to the client.
     * @param usernames a list of Player username to be parsed
     * @return  a list of Player with the corresponding ids,
     * an empty list if there are no valid players
     */
    public List<Player> getPlayersFromUsername(List<String> usernames){
        List<Player> temp = new ArrayList<>();
        usernames.forEach(i -> temp.add(getSinglePlayerFromUsername(i)));
        temp.removeAll(Collections.singleton(null));
        if(temp.size() != usernames.size() || temp.contains(null))
            throw new IncorrectEventException("Player ID not correct");
        return temp;
    }

    /**
     * Return a list of {@link Tile} from a list of {@link ViewTileCoords}.
     * @param viewTiles list to convert
     * @return converted list
     */
    public List<Tile> getTilesFromViewTiles(List<ViewTileCoords> viewTiles){
        Board board = match.getBoard();
        List<Tile> tiles = viewTiles.stream()
                .map(v -> board.getTile(v.getPosy(),v.getPosx()))
                .collect(Collectors.toList());
        if (tiles.size() != viewTiles.size() || tiles.contains(null))
            throw new IncorrectEventException("Wrong tiles!");
        else {
            return tiles;
        }
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
                        .findFirst().orElse(null));
        if(realWeapon == null){
            throw new IncorrectEventException("Weapon is not present!");
        }
        return realWeapon;
    }

    /**
     * Return the corresponding room if the room is in the map,
     * otherwise signals the problem to the client.
     * @param room
     * @return
     */
    public Color getRoomFromString(String room) {
        Set<Color> colors = match.getBoard().getTiles().stream().
                flatMap(List::stream).filter(t -> t != null).map(Tile::getRoom).collect(Collectors.toSet());
        try {
            Logger.log(Priority.DEBUG, "Color choice is " + room);
            Color relatedColor = Color.valueOf(room);
            Logger.log(Priority.DEBUG, relatedColor.toString());
            if (colors.contains(relatedColor))
                return relatedColor;
            else throw new IncorrectEventException("Color is not present in the map!");
        }
        catch (IllegalArgumentException e) {
            throw new IncorrectEventException("Color is wrong!");
        }
    }

    /**
     * Get the corresponding action from a string, parsing it from the currentPlayer's action
     * @param action
     * @return
     */
    public Action getActionFromString(String action) {
        List<Action> actions = match.getPlayers().get(match.getCurrentPlayer()).getActions();
        return  actions.stream().
                filter(a -> a.toString().equals(action)).findFirst().orElseThrow(() -> new IncorrectEventException("Action doesn't exist!"));
    }

    /**
     * Get the current direction, parsing it from a string.
     * @param direction
     * @return
     */
    public Direction getDirectionFromString(String direction) {
        try {
            return Direction.valueOf(direction.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IncorrectEventException("Direction doesn't exist!");
        }
    }

    /**
     * Get related powerup from the current Player
     * @param powerup
     * @return
     */
    public PowerUp getPowerUpFromViewPowerUp(ViewPowerUp powerup) {
       return player.
               getPowerUps().
               stream().
                filter(p -> p.getName().equals(powerup.getName()) && p.getDiscardAward().toString().equals(powerup.getDiscardAward())).
               findAny().orElse(null);
    }
}
