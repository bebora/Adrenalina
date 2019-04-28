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

    public List<ViewTileCoords> getCoords() {
        return coords;
    }

    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public SelectFromTilesUpdate(List<Tile> tiles) {
        this.coords = tiles.stream().
                map(m->new ViewTileCoords(m.getPosx(), m.getPosy())).
                collect(Collectors.toList());
    }

}
