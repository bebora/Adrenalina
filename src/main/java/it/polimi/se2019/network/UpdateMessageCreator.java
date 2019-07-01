package it.polimi.se2019.network;

import it.polimi.se2019.controller.ModelToViewConverter;
import it.polimi.se2019.network.updatemessage.TotalUpdate;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.InvalidUpdateException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateMessageCreator {
    /**
     * Build the total upgrade with all the required attributes that
     * the player must have to start or resume the game
     * Perspective is calculated from username and players:
     * if no player has that username, an exception will be thrown
     * @param username username of receiving player
     * @param board current match board
     * @param players all players in the receiving player's match
     * @param points points of the receiving player
     * @param powerUps powerups of the receiving player
     * @param loadedWeapons loaded weapons of the receiving player
     * @param currentPlayer player who is currently playing its turn in the match
     */
    public static TotalUpdate totalUpdate(String username, Board board, List<Player> players,
                int points, List<PowerUp> powerUps,
                List<Weapon> loadedWeapons, Player currentPlayer) {
        TotalUpdate ret = new TotalUpdate();
        ret.setUsername(username);
        ret.setBoard(ModelToViewConverter.fromBoard(board));
        ret.setPlayers(players.stream().map(ModelToViewConverter::fromPlayer).collect(Collectors.toCollection(ArrayList::new)));
        Player receivingPlayer = players.stream().
                filter(p-> p.getUsername().equals(username)).
                findFirst().orElseThrow(()-> new InvalidUpdateException("No player has the given username"));

        if(receivingPlayer.getTile() != null)
            ret.setPerspective(ModelToViewConverter.fromTileToViewTileCoords(receivingPlayer.getTile()));
        else
            ret.setPerspective(null);
        ret.setPoints(points);
        ret.setPowerUps(powerUps.stream().
                map(ModelToViewConverter::fromPowerUp).collect(Collectors.toCollection(ArrayList::new)));
        ret.setLoadedWeapons(loadedWeapons.stream().
                map(ModelToViewConverter::fromWeapon).collect(Collectors.toCollection(ArrayList::new)));
        ret.setCurrentPlayerId(currentPlayer.getId());
        return ret;
    }
}
