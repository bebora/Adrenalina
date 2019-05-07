package it.polimi.se2019.model.updatemessage;

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
    private List<String> loadedWeapons;

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

    public List<String> getLoadedWeapons() {
        return loadedWeapons;
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
     * @param username
     * @param board
     * @param players
     * @param idView
     * @param points
     * @param powerUps
     * @param loadedWeapons
     */
    public TotalUpdate(String username, Board board, List<Player> players,
                       String idView, int points, List<PowerUp> powerUps,
                       List<Weapon> loadedWeapons) {
        this.username = username;
        this.board = new ViewBoard(board);
        this.players = players.stream().map(ViewPlayer::new).collect(Collectors.toList());
        Player receivingPlayer = players.stream().
                filter(p-> p.getToken().equals(username)).
                findFirst().orElseThrow(()-> new InvalidUpdateException("No player has the given username"));
        this.perspective = new ViewTileCoords(
                receivingPlayer.getTile().getPosy(),
                receivingPlayer.getTile().getPosx());
        this.idView = idView;
        this.points = points;
        this.powerUps = powerUps.stream().map(ViewPowerUp::new).collect(Collectors.toList());
        this.loadedWeapons = loadedWeapons.stream().map(Weapon::getName).collect(Collectors.toList());
    }

}
