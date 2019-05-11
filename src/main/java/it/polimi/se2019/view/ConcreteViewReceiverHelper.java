package it.polimi.se2019.view;

import java.util.List;

/**
 * Helper functions class to prevent code duplication in ConcreteViewReceiver
 */
public class ConcreteViewReceiverHelper {
    private View view;

    public ConcreteViewReceiverHelper(View linkedView) {
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
     * Find and return ViewTile given a ViewTileCoords object
     * @param coords
     * @return
     */
    public ViewTile getTileFromCoords(ViewTileCoords coords) {
        try {
            return view.getBoard().getTiles().get(coords.getPosy()).get(coords.getPosx());
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidUpdateException("Tile with given coords does not exist");
        }
    }
}
