package it.polimi.se2019.model;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.cards.PowerUp;

public class SpawnPlayer extends Player{

    private Boolean damaged;
    public SpawnPlayer(Color color) {
        super(color.toString(), color);
        damaged  = Boolean.FALSE;

    }

    @Override
    public void receiveShot(Player shooter, int damage, int marks) {
        if (!damaged)
            super.getDamages().add(shooter);
        damaged = Boolean.TRUE;
    }

    @Override
    public void refreshPlayer() {
        damaged = Boolean.TRUE;
    }


    @Override
    public Boolean getDominationSpawn() {
        return Boolean.TRUE;
    }


}
