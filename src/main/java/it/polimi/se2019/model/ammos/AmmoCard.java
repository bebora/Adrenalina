package it.polimi.se2019.model.ammos;
import java.util.*;

/**
 * Represents an AmmoCard, made by {@link Ammo}, following Adrenalina's rules.
 */
public class AmmoCard {
    private List<Ammo> ammos;

    public AmmoCard(Ammo a, Ammo b, Ammo c) {
        ammos = new ArrayList<>(Arrays.asList(a,b,c));
    }

    public List<Ammo> getAmmos() {
        return ammos;
    }
}
