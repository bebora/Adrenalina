package it.polimi.se2019.network.events;

import it.polimi.se2019.network.EventVisitor;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.List;

/**
 * Event used by view after choosing at least one tile,
 * when it can attack every player in a tile or when it
 * can move someone to the sent tiles (selectedTiles shouldn't
 * have more than one tile in this case)
 */
public class SelectTiles implements EventVisitable {
    private List<ViewTileCoords> selectedTiles;
    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}
    public SelectTiles(List<ViewTileCoords> selectedTiles){
        this.selectedTiles = selectedTiles;
    }
    public List<ViewTileCoords> getSelectedTiles() { return selectedTiles; }
}
