package it.polimi.se2019.model;
import it.polimi.se2019.model.board.Color;

public class SpawnPlayer extends Player{
    private Boolean damaged;
    public SpawnPlayer(Color color) {
        super(color.toString()+ "$");
        setColor(color);
        damaged  = Boolean.FALSE;
        setDominationSpawn(true);
    }

    @Override
    public void receiveShot(Player shooter, int damage, int marks) {
        if (!damaged)
            super.getDamages().add(shooter);
        damaged = Boolean.TRUE;
    }

    public void setDamaged(Boolean damaged) {
        this.damaged = damaged;
    }

    @Override
    public void resetPlayer() {
        damaged = Boolean.TRUE;
    }


    @Override
    public boolean getDominationSpawn() {
        return true;
    }


}
