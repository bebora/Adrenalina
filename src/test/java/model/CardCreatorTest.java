package model;

import model.ammos.Ammo;
import model.cards.CardCreator;
import model.cards.Moment;
import model.cards.PowerUp;
import model.cards.Weapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardCreatorTest {
    @Test
    void parsePowerUp() {
        PowerUp test = CardCreator.parsePowerUp("mirino.btl", Ammo.RED);

        // Test various elements of the parser
        assertEquals(test.getEffect().getDamages().get(0).getDamagesAmount(), 1);
        assertEquals(test.getEffect().getCost().get(0), Ammo.ANY);
        assertEquals(test.getApplicability(), Moment.DAMAGING);
        assertEquals(test.getName(),"mirino");
        assertEquals(test.getDiscardAward(),Ammo.RED);
    }

    @Test
    void parseWeapon() {
        Weapon test = CardCreator.parseWeapon("lanciarazzi.btl");

        // Test various elements of the parser
        assertEquals(test.getEffects().get(2).getDamages().get(1).getDamagesAmount(), 1);
        assertEquals(test.getEffects().get(2).getDamages().get(1).getTarget().getMaxDistance(), 0);
        assertEquals(test.getEffects().get(2).getDamages().get(1).getDamagesAmount(), 1);
    }
}