package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.List;

public class ViewEffect implements Serializable {

    private List<String> cost;

    private String desc;

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
