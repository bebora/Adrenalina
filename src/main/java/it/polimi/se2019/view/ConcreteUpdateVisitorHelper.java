package it.polimi.se2019.view;

import java.util.List;

/**
 * Helper functions class to prevent code duplication in ConcreteUpdateVisitor
 */
public class ConcreteUpdateVisitorHelper {
    private ClientView view;

    public ConcreteUpdateVisitorHelper(ClientView linkedView) {
        this.view = linkedView;
    }

    /**
     * Find and return ViewPlayer in view by its id
     * @param id
     * @return
     */
    public ViewPlayer getPlayerFromId(String id) {
        return view.getPlayers().stream().
            filter(m -> m.getId().equals(id)).
            findAny().
            orElseThrow(()-> new InvalidUpdateException("Player with given id does not exist"));
    }

    /**
     * Find and return ViewTile in view by its coordinates
     * @param posx
     * @param posy
     * @return
     */
    public ViewTile getTileFromCoords(int posy, int posx) {
        try {
            return view.getBoard().getTiles().get(posy).get(posx);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidUpdateException("Tile with given coords does not exist");
        }
    }
}
