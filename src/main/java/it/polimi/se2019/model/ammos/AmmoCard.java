package it.polimi.se2019.model.ammos;
import java.util.*;

public class AmmoCard {
    private List<Ammo> ammos;

    public AmmoCard(Ammo a, Ammo b, Ammo c) {
        ammos = new ArrayList<>(Arrays.asList(a,b,c));
    }
}
