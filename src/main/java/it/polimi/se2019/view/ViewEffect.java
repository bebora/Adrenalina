package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.List;

/**
 * Represent an effect of a {@link ViewWeapon}
 */
public class ViewEffect implements Serializable {

    /**
     * List of ammos to pay to use the effect
     */
    private List<String> cost;

    /**
     * Description of the effect
     */
    private String desc;

    /**
     * Name of the effect
     */
    private String name;

    public List<String> getCost() {
        return cost;
    }

    public void setCost(List<String> cost) {
        this.cost = cost;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
