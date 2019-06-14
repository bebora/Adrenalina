package it.polimi.se2019.controller.updatemessage;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent whole View when the game starts or when a player reconnects
 */
public class TotalUpdate implements UpdateVisitable {
    private String username;
    private ViewBoard board;
    private ViewTileCoords perspective;
    private List<ViewPlayer> players;
    private String idView;
    private int points;
    private List<ViewPowerUp> powerUps;
    private List<ViewWeapon> loadedWeapons;
    private String currentPlayerId;

    public String getUsername() {
        return username;
    }

    public ViewBoard getBoard() {
        return board;
    }

    public ViewTileCoords getPerspective() {
        return perspective;
    }

    public List<ViewPlayer> getPlayers() {
        return players;
    }

    public String getIdView() {
        return idView;
    }

    public int getPoints() {
        return points;
    }

    public List<ViewPowerUp> getPowerUps() {
        return powerUps;
    }

    public List<ViewWeapon> getLoadedWeapons() {
        return loadedWeapons;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    @Override
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Build the total upgrade with all the required attributes that
     * the player must have to start or resume the game
     * Perspective is calculated from username and players:
     * if no player has that username, an exception will be thrown
     * @param username username of receiving player
     * @param board current match board
     * @param players all players in the receiving player's match
     * @param idView
     * @param points points of the receiving player
     * @param powerUps powerups of the receiving player
     * @param loadedWeapons loaded weapons of the receiving player
     * @param currentPlayer player who is currently playing its turn in the match
     */
    public TotalUpdate(String username, Board board, List<Player> players,
                       String idView, int points, List<PowerUp> powerUps,
                       List<Weapon> loadedWeapons, Player currentPlayer) {
        this.username = username;
        this.board = new ViewBoard(board);
        this.players = players.stream().map(ViewPlayer::new).collect(Collectors.toList());
        Player receivingPlayer = players.stream().
                filter(p-> p.getUsername().equals(username)).
                findFirst().orElseThrow(()-> new InvalidUpdateException("No player has the given username"));

        if(receivingPlayer.getTile() != null)
            this.perspective = new ViewTileCoords(receivingPlayer.getTile());
        else
            this.perspective = null;
        this.idView = idView;
        this.points = points;
        this.powerUps = powerUps.stream().map(ViewPowerUp::new).collect(Collectors.toList());
        this.loadedWeapons = loadedWeapons.stream().map(ViewWeapon::new).collect(Collectors.toList());
        this.currentPlayerId = currentPlayer.getId();
    }

}
