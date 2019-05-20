package it.polimi.se2019.controller.events;

import java.util.List;

public class SelectPowerUps {
    private List<String> powerUps;
    private boolean discarded;

    public SelectPowerUps(List<String> powerUps, boolean discarded){
        this.powerUps = powerUps;
        this.discarded = discarded;
    }

    public List<String> getPowerUps(){return  powerUps; }
    public boolean isDiscarded(){return discarded; }

}
