package it.polimi.se2019.model;

import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DominationMatch extends Match{

    int currentTurn = -1;
    List<Player> spawnPoints;


    public DominationMatch(List<Player> players, String boardFilename, int numSkulls) {
        super(players,boardFilename,numSkulls);
        spawnPoints = new ArrayList<>();
    }

    @Override
    public void newTurn() {
        Player currentPlayer = super.getPlayers().get(getCurrentPlayer());

        List<Tile> spawnTiles = spawnPoints.stream().map(Player::getTile).collect(Collectors.toList());


        if (spawnTiles.contains(currentPlayer.getTile())) {
            currentPlayer.receiveShot(currentPlayer,1,0);
            if (getPlayers().
                    stream().
                    filter(p -> !p.getId().equals(currentPlayer.getId()) && p.getTile() != currentPlayer.getTile()).count()==0) {
                spawnPoints.
                        stream().
                        filter(s -> s.getTile() == currentPlayer.getTile()).findFirst().
                        orElseThrow(UnsupportedOperationException::new).
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
                temp.setTile(super.getBoard().getTiles().stream().flatMap(List::stream).filter(t -> t.isSpawn() && t.getRoom().equals(color)).findFirst().orElseThrow(UnsupportedOperationException::new));
            }
            spawnPoints = getPlayers().stream().filter(Player::getDominationSpawn).collect(Collectors.toList());

        }
    }



    @Override
    public void scoreDeadShot(Player player) {
        if (player.getDamages().size() == 12)
            throw new UserInputRequest("SPAWNPLAYER", p -> p.getDamages().add(getPlayers().get(getCurrentPlayer())));
    }

    @Override
    public List<Player> getWinners() {
        for (Player p : getPlayers()) {
            if (!spawnPoints.contains(p))
                scorePlayerBoard(p);
            else {
                scoreSpawnPoint(p);
            }
        }

        return getPlayers().
                stream().
                filter(s -> s.getPoints() == getPlayers().stream().max(Comparator.comparing(Player::getPoints)).get().getPoints()).
                collect(Collectors.toList());
    }

    private void scoreSpawnPoint(Player p) {
        int currentReward = 0;
        Map<Player, Long> frequencyShots =
                p.getDamages().
                        stream().
                        filter(Objects::nonNull).
                        collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Comparator<Player> givenDamages = Comparator.comparing(frequencyShots::get);
        Comparator<Player> indices = Comparator.comparing(p.getDamages()::indexOf);

        HashSet<Player> shotGiver = new HashSet<>(p.getDamages());
        List<Player> shotOrder = shotGiver.stream().sorted(givenDamages.thenComparing(indices)).collect(Collectors.toList());
        int currentSameShots = 1;
        for (int i = 0; i < shotOrder.size(); i++) {
            if (i != 0 && frequencyShots.get(shotOrder.get(i)) ==
                    frequencyShots.get(shotOrder.get(i)) - 1)
                currentSameShots ++;
            else {
                currentReward += currentSameShots;
                currentSameShots = 1;
            }

            shotOrder.get(i).addPoints(p.getRewardPoints().get(currentReward));
        }
    }


}
