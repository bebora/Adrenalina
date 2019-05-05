package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.view.UpdateVisitor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent list of rooms the player can choose from.
 */
public class SelectFromRoomsUpdate implements UpdateVisitable {
    private List<String> rooms;
    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public List<String> getRooms() {
        return rooms;
    }


    public SelectFromRoomsUpdate(List<Color> rooms) {
        this.rooms = rooms.stream().
                map(Color::name).
                collect(Collectors.toList());
    }
}
