package it.polimi.se2019.view;

import java.io.Serializable;

public class ViewPowerUp implements Serializable {
    private String discardAward;

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
