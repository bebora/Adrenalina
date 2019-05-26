package it.polimi.se2019.view;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewTile implements Serializable {
    private String room;
    private ViewTileCoords coords;
    private ArrayList<String> weapons;
    private ArrayList<String> ammos;
    private boolean spawn;
    public ViewTileCoords getCoords() {
        return coords;
    }

    public ArrayList<String> getAmmos() {
        return ammos;
    }

    public ArrayList<String> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<String> weapons) {
        this.weapons = new ArrayList<>(weapons);
    }

    public void setAmmos(List<String> ammos) {
        this.ammos = new ArrayList<>(ammos);
    }

    public String getRoom(){return room;}

    public boolean isSpawn(){return spawn;}

    public ViewTile(Tile tile) {
        this.room = tile.getRoom().name();
        this.coords = new ViewTileCoords(tile.getPosy(), tile.getPosx());
        this.weapons = tile.getWeapons().stream().
                map(Weapon::getName).
                collect(Collectors.toCollection(ArrayList::new));
        this.spawn = tile.isSpawn();
        //Spawn tiles have a null ammoCard, so getting their ammoCard name would throw a NullPointerException
        this.ammos = tile.isSpawn() ? new ArrayList<>() : tile.getAmmoCard().getAmmos().stream().
                map(Ammo::name).
                collect(Collectors.toCollection(ArrayList::new));
    }
    public static int cabDistance(ViewTile tile1, ViewTile tile2) {
        return Math.abs(tile1.getCoords().getPosx() - tile2.getCoords().getPosx()) + Math.abs(tile1.getCoords().getPosy() - tile2.getCoords().getPosy());
    }

    @Override
    public boolean equals(Object p) {
        boolean returnValue = false;
        if (p instanceof ViewTile) {
            ViewTile tile = (ViewTile) p;
            returnValue = (tile.getCoords().getPosx() == this.getCoords().getPosx() && tile.getCoords().getPosy() == this.getCoords().getPosy());
        }
        return returnValue;
    }


    /**
     * Constructor used in visitor, missing color because it won't be used: will get the correspondent tile using helper, from coords.
     * @param ammos
     * @param weapons
     * @param coords
     */
    public ViewTile(List<String> ammos, List<String> weapons, ViewTileCoords coords) {
        this.coords = coords;
        this.weapons = new ArrayList<>(weapons);
        this.ammos = new ArrayList<>(ammos);
    }

}
