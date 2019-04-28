package it.polimi.se2019.model.updatemessage;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.view.UpdateVisitor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent list of other players the player can choose from.
 * Should be used to let the player choose another player during
 * its move or attack action
 */
public class SelectFromPlayersUpdate implements UpdateVisitable {
    private List<String> players;
    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public List<String> getPlayers() {
        return players;
    }

    public SelectFromPlayersUpdate(List<Player> players) {
        this.players = players.stream().
                map(Player::getId).
                collect(Collectors.toList());
    }
}
