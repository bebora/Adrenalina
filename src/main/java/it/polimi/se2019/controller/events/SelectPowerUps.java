package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.ViewPowerUp;

import java.util.List;
/**
 * Event used by view after choosing at least one powerup to
 * use its effects or to pay a cost
 */
public class SelectPowerUps implements EventVisitable {
    /**
     * List of chosen powerups the player uses for their effects or to pay a cost
     * ViewPowerUp is better than String because chosen color may be useful
     */
    private List<ViewPowerUp> powerUps;
    //TODO discarded may be useless because updateOnPowerUps implementation already knows the context
    private boolean discarded;

    public SelectPowerUps(List<ViewPowerUp> powerUps, boolean discarded){
        this.powerUps = powerUps;
        this.discarded = discarded;
    }

    public List<ViewPowerUp> getPowerUps(){return  powerUps; }
    public boolean isDiscarded(){return discarded; }

    @Override
    public void accept(EventVisitor visitor) {visitor.visit(this);}
}
