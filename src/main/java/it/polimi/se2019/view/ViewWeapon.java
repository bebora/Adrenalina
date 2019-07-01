package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplified weapon used by the view, with basic info to display
 */
public class ViewWeapon implements Serializable {
    /**
     * Name of the weapon
     */
    private String name;

    /**
     * Ammo cost of the basic effect of the weapon
     */
    private ArrayList<String> cost;

    /**
     * ViewEffects of the weapon
     */
    private ArrayList<ViewEffect> effects;

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(ArrayList<String> cost) {
        this.cost = cost;
    }

    public void setEffects(ArrayList<ViewEffect> effects) {
        this.effects = effects;
    }

    public String getName() { return name; }

    public String getColor() { return cost.get(0); }

    public List<ViewEffect> getEffects() { return effects; }

    public List<String> getCost() {
        return cost;
    }
}
