package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.ViewTile;

import java.util.List;

public class SelectTiles implements EventVisitable {
    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}
    private List<ViewTile> selectedTiles;
    public SelectTiles(List<ViewTile> selectedTiles){this.selectedTiles = selectedTiles;}
    public List<ViewTile> getSelectedTiles() { return selectedTiles; }
}
