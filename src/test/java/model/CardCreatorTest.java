package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardCreatorTest {

    /*@Test
    void parsePowerUp() {

    }*/

    @Test
    void parseWeapon() {
        Weapon test = CardCreator.parseWeapon("weapons/lanciarazzi.btl");

        // Test various elements of the parser
        assertEquals(test.getEffects().get(2).getDamages().get(1).getDamagesAmount(), 1);
        assertEquals(test.getEffects().get(2).getDamages().get(1).getTarget().getMaxDistance(), 0);
        assertEquals(test.getEffects().get(2).getDamages().get(1).getDamagesAmount(), 1);
    }
}