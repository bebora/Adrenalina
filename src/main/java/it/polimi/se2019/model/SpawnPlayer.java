package it.polimi.se2019.model;
import it.polimi.se2019.model.board.Color;

public class SpawnPlayer extends Player{
    private Boolean damaged;
    public SpawnPlayer(Color color) {
        super(color.toString());
        damaged  = Boolean.FALSE;

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
    public Boolean getDominationSpawn() {
        return Boolean.TRUE;
    }


}
