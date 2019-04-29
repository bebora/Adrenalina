package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;


import java.util.*;
import java.util.stream.Collectors;

public class DominationMatch extends Match{

    int currentTurn = -1;

    public DominationMatch(List<Player> players, String boardFilename, int numSkulls) {
        super(players,boardFilename,numSkulls);
    }

    @Override
    public void newTurn() {
        Player currentPlayer = super.getPlayers().get(getCurrentPlayer());

        List<Player> spawnPoints = super.getPlayers().stream().filter(Player::getDominationSpawn).collect(Collectors.toList());

        List<Tile> spawnTiles = spawnPoints.stream().map(Player::getTile).collect(Collectors.toList());


        if (spawnTiles.contains(currentPlayer.getTile())) {
            currentPlayer.receiveShot(currentPlayer,1,0);
            if (getPlayers().
                    stream().
                    filter(p -> !p.getId().equals(currentPlayer.getId()) && p.getTile() != currentPlayer.getTile()).count()==0) {
                spawnPoints.
                        stream().
                        filter(s -> s.getTile() == currentPlayer.getTile()).findFirst().
                        orElseThrow(() -> new UnsupportedOperationException()).
                        receiveShot(currentPlayer, 1, 0);
            }

        }
        super.newTurn();

        if (super.getCurrentPlayer() == super.getFirstPlayer()) {
            currentTurn++;
        }

        if (currentTurn == 2) {
            List<Color> colors = new ArrayList<>(Arrays.asList(Color.RED, Color.YELLOW, Color.BLUE));
            for (Color color : colors) {
                Player temp = new SpawnPlayer(color);
                temp.setTile(super.getBoard().getTiles().stream().flatMap(List::stream).filter(t -> t.isSpawn() && t.getRoom().equals(color)).findFirst().orElseThrow(() -> new UnsupportedOperationException()));
            }
        }
    }



    @Override
    public void scoreDeadShot(Player player) {
        if (player.getDamages().size() == 12)
            throw new UserInputRequest("SPAWNPLAYER", (p) -> p.getDamages().add(getPlayers().get(getCurrentPlayer())));
    }

    @Override
    public List<Player> getWinners() {
        //TODO get players that win
        return null;

    }
}
