package it.polimi.se2019.controller.updatemessage;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.List;
import java.util.stream.Collectors;

public class TileUpdate implements UpdateVisitable{
    private List<String> ammos;
    private List<String> weapons;
    private ViewTileCoords coords;
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public List<String> getAmmos() {
        return ammos;
    }

    public List<String> getWeapons() {
        return weapons;
    }

    public ViewTileCoords getCoords() {
        return coords;
    }

    /**
     * Constructor from Tile, which should have been updated already
     * @param tile
     */
    public TileUpdate(Tile tile) {
        this.ammos = tile.getAmmoCard().getAmmos().stream().
                map(Ammo::name).collect(Collectors.toList());
        this.weapons = tile.getWeapons().stream().
                map(Weapon::getName).collect(Collectors.toList());
        this.coords = new ViewTileCoords(tile.getPosy(), tile.getPosx());
    }
}
