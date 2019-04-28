package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.ViewTile;

import java.util.List;
import java.util.stream.Collectors;

public class TileUpdate {
    private List<String> ammos;
    private List<String> weapons;
    private int posx;
    private int posy;
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public List<String> getAmmos() {
        return ammos;
    }

    public List<String> getWeapons() {
        return weapons;
    }

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }
    //TODO one of the constructor may never be used depending on further implementaion
    public TileUpdate(ViewTile tile) {
        this.ammos = tile.getAmmos();
        this.weapons = tile.getWeapons();
        this.posx = tile.getCoords().getPosx();
        this.posy = tile.getCoords().getPosy();
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
        this.posx = tile.getPosx();
        this.posy = tile.getPosy();
    }
}
