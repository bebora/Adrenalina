package it.polimi.se2019.model;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;

public class SpawnPlayer extends Player{
    private Boolean damaged;
    public SpawnPlayer(Color color) {
        super(color.toString()+ "$");
        setDominationSpawn(true);
        setColor(color);
        damaged = Boolean.FALSE;
    }

    @Override
    public void receiveShot(Player shooter, int damage, int marks, boolean convert) {
        if (!damaged)
            super.getDamages().add(shooter);
        damaged = Boolean.TRUE;
    }

    public void setDamaged(Boolean damaged) {
        this.damaged = damaged;
    }

    /**
     * Reset player so that it can be attacked on a new turn
     */
    @Override
    public void resetPlayer() {
        damaged = Boolean.FALSE;
    }


    @Override
    public boolean getDominationSpawn() {
        return true;
    }

    @Override
    public void setTile(Tile tile) {
        if (this.getTile() != null && this.getTile() != tile) {
            Logger.log(Priority.WARNING, "SPAWN TRIED TO MOVE");
        }
        else {
            super.setTile(tile);
        }
    }
}
