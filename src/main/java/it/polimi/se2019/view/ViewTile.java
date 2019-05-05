package it.polimi.se2019.view;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewTile {
    private String room;
    private ViewTileCoords coords;
    private List<String> weapons;
    private List<String> ammos;
    public ViewTileCoords getCoords() {
        return coords;
    }

    public List<String> getAmmos() {
        return ammos;
    }

    public List<String> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<String> weapons) {
        this.weapons = weapons;
    }

    public void setAmmos(List<String> ammos) {
        this.ammos = ammos;
    }

    public ViewTile(Tile tile) {
        this.room = tile.getRoom().name();
        this.coords = new ViewTileCoords(tile.getPosy(), tile.getPosx());
        this.weapons = tile.getWeapons().stream().
                map(Weapon::getName).collect(Collectors.toList());
        //Spawn tiles have a null ammoCard, so getting their ammoCard name would throw a NullPointerException
        this.ammos = tile.isSpawn() ? new ArrayList<>() : tile.getAmmoCard().getAmmos().stream().
                map(Ammo::name).collect(Collectors.toList());
    }

}
