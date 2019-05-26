package it.polimi.se2019.view;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewWeapon implements Serializable {
    private String name;
    private boolean loaded;
    private ArrayList<String> cost;
    private ArrayList<ViewEffect> effects;


    public ViewWeapon(Weapon weapon){
        this.name = weapon.getName();
        this.loaded = weapon.getLoaded();
        this.effects = weapon.getEffects().stream()
                .map(ViewEffect::new)
                .collect(Collectors.toCollection(ArrayList::new));
        this.cost = weapon.getCost().stream()
                .map(Ammo::name)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String getName() { return name; }

    public String getColor() { return cost.get(0); }

    public List<ViewEffect> getEffects() { return effects; }

    public List<String> getCost() {
        return cost;
    }

    public boolean getLoaded(){
        return loaded;
    }
}
