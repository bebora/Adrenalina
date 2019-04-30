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
    private List<ViewPlayer> players;
    private String idView;
    private int points;
    private List<String> powerUps;
    private List<String> loadedWeapons;

    public String getUsername() {
        return username;
    }

    public ViewBoard getBoard() {
        return board;
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

    public List<String> getPowerUps() {
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
        for (ViewPlayer p: this.players) {
            Player realPlayer = players.stream().filter(m->m.getId().equals(p.getId())).findFirst().
                    orElseThrow(()->new InvalidUpdateException("Real player missing"));
            p.setDamages(UpdateHelper.playersToViewPlayers(realPlayer.getDamages(), this.players));
            p.setMarks(UpdateHelper.playersToViewPlayers(realPlayer.getMarks(), this.players));

        }
        this.idView = idView;
        this.points = points;
        this.powerUps = powerUps.stream().map(PowerUp::getName).collect(Collectors.toList());
        this.loadedWeapons = loadedWeapons.stream().map(Weapon::getName).collect(Collectors.toList());
    }

}
