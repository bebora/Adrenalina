package it.polimi.se2019.model;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Moment;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardCreatorTest {
    @Test
    void parsePowerUp() {
        PowerUp test = CardCreator.parsePowerUp("targetingScope.btl", Ammo.RED);

        // Test various elements of the parser
        assertEquals(test.getEffect().getDamages().get(0).getMarksAmount(), 1);
        assertEquals(test.getEffect().getCost().get(0), Ammo.ANY);
        assertEquals(test.getApplicability(), Moment.DAMAGING);
        assertEquals(test.getName(),"targeting scope");
        assertEquals(test.getDiscardAward(),Ammo.RED);
    }

    @Test
    void parseWeapon() {
        Weapon test = CardCreator.parseWeapon("rocketLauncher.btl");

        // Test various elements of the parser
        assertEquals(test.getEffects().get(2).getDamages().get(1).getDamagesAmount(), 1);
        assertEquals(test.getEffects().get(2).getDamages().get(1).getTarget().getMaxDistance(), 0);
        assertEquals(test.getEffects().get(2).getDamages().get(1).getDamagesAmount(), 1);
    }
}