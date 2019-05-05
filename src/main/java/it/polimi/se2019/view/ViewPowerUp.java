package it.polimi.se2019.view;

import it.polimi.se2019.model.cards.PowerUp;

public class ViewPowerUp {
    private String discardAward;
    private String name;

    public String getDiscardAward() {
        return discardAward;
    }

    public String getName() {
        return name;
    }

    public ViewPowerUp(PowerUp powerUp) {
        this.discardAward = powerUp.getDiscardAward().name();
        this.name = powerUp.getName();
    }
}
