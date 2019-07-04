package it.polimi.se2019.view;

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
     * @param id id of the player to retrieve the id
     * @return the related viewPlayer from the send {@code id}
     */
    public ViewPlayer getPlayerFromId(String id) {
        return view.getPlayers().stream().
            filter(m -> m.getId().equals(id)).
            findAny().
            orElseThrow(()-> new InvalidUpdateException("Player with given id does not exist"));
    }


    /**
     * Find and return ViewTile given a ViewTileCoords object
     * @param coords coordinates of the viewTile to retrieve the viewTile from the board
     * @return related viewTile from the sent {@code coods}
     */
    public ViewTile getTileFromCoords(ViewTileCoords coords) {
        try {
            if(coords!=null)
                return view.getBoard().getTiles().get(coords.getPosy()).get(coords.getPosx());
            else
                return null;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidUpdateException("Tile with given coords does not exist");
        }
    }
}
