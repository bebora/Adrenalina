package it.polimi.se2019.view;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.Effect;

import java.util.List;
import java.util.stream.Collectors;

public class ViewEffect {
    private String name;
    private String desc;
    private List<String> cost;

    public ViewEffect(Effect effect){
        this.name = effect.getName();
        this.desc = effect.getDesc();
        this.cost = effect.getCost().stream()
                .map(Ammo::name)
                .collect(Collectors.toList());
    }

    public String getName() { return name; }

    public String getDesc() { return desc; }

    public List<String> getCost() { return cost; }
}
