package it.polimi.se2019.model;
import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;

/**
 * Represents a SpawnPlayer, used in Domination mode.
 * Supports restoring and cloning, and receiving shots.
 */
public class SpawnPlayer extends Player{
    /**
     * Indicates whether the spawn is already damaged in the current turn
     */
    private Boolean damaged;
    public SpawnPlayer(Color color) {
        super(color.toString()+ "$");
        setDominationSpawn(true);
        setColor(color);
        damaged = Boolean.FALSE;
    }

    public SpawnPlayer(SpawnPlayer player) {
        super(player);
        setDominationSpawn(true);
        damaged = player.getDamaged();
    }

    /**
     * Restores the spawn, using the usual parameters and restoring the damaged state.
     * @param oldPlayer old spawn player
     */
    @Override
    void restorePlayer(Player oldPlayer) {
        super.restorePlayer(oldPlayer);
        SpawnPlayer temp = (SpawnPlayer) oldPlayer;
        temp.setDamaged(damaged);
    }

    /**
     * Damages the spawn if:
     * <li>The spawn isn't already damaged</li>
     * <li>{@code convert} is true</li>
     * @param shooter player who shoots
     * @param damage number of damages to add
     * @param marks number of marks to add after converting the existing marks
     * @param convert
     */
    @Override
    public void receiveShot(Player shooter, int damage, int marks, boolean convert) {
        if (!damaged)
            super.getDamages().add(shooter);
        damaged = Boolean.TRUE;
        getMatch().updateViews();
    }

    public Boolean getDamaged() {
        return damaged;
    }

    public void setDamaged(Boolean damaged) {
        this.damaged = damaged;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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

    /**
     * Issues a warning if the SPAWN tried to change his tile.
     * The behaviour is not supported by the controller
     * @param tile
     */
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
