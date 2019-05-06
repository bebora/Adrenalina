package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.ViewTileCoords;

import java.util.List;

public class SelectTiles implements EventVisitable {
    private List<ViewTileCoords> selectedTiles;

    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}
    public SelectTiles(List<ViewTileCoords> selectedTiles){this.selectedTiles = selectedTiles;}
    public List<ViewTileCoords> getSelectedTiles() { return selectedTiles; }

    public String toString() {
        return "tiles";
    }
}
