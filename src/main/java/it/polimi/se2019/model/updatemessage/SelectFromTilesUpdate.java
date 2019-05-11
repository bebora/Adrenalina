package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.view.UpdateVisitor;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent list of other tiles the player can choose from.
 * Should be used to let the player choose a tile during
 * its move or attack action
 */
public class SelectFromTilesUpdate implements UpdateVisitable {
    private List<ViewTileCoords> coords;
    private int minTiles;
    private int maxTiles;

    public List<ViewTileCoords> getCoords() {
        return coords;
    }

    public int getMinTiles() {
        return minTiles;
    }

    public int getMaxTiles() {
        return maxTiles;
    }

    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public SelectFromTilesUpdate(List<Tile> tiles, int minTiles, int maxTiles) {
        this.coords = tiles.stream().
                map(m->new ViewTileCoords(m.getPosx(), m.getPosy())).
                collect(Collectors.toList());
        this.minTiles = minTiles;
        this.maxTiles = maxTiles;
    }

}
