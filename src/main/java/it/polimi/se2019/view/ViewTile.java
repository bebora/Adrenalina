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

    public String getRoom(){return room;}

    public ViewTile(Tile tile) {
        this.room = tile.getRoom().name();
        this.coords = new ViewTileCoords(tile.getPosy(), tile.getPosx());
        this.weapons = tile.getWeapons().stream().
                map(Weapon::getName).collect(Collectors.toList());
        //Spawn tiles have a null ammoCard, so getting their ammoCard name would throw a NullPointerException
        this.ammos = tile.isSpawn() ? new ArrayList<>() : tile.getAmmoCard().getAmmos().stream().
                map(Ammo::name).collect(Collectors.toList());
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
        this.weapons = weapons;
        this.ammos = ammos;
    }

}
