package it.polimi.se2019.view;

import java.io.Serializable;

/**
 * Simplified powerup used by the view, with basic info to display
 */
public class ViewPowerUp implements Serializable {
    /**
     * Color of the ammo you would get by discarding this powerup
     */
    private String discardAward;

    /**
     * Name of the powerup
     */
    private String name;

    public String getDiscardAward() {
        return discardAward;
    }

    public String getName() {
        return name;
    }

    public void setDiscardAward(String discardAward) {
        this.discardAward = discardAward;
    }

    public void setName(String name) {
        this.name = name;
    }
}
