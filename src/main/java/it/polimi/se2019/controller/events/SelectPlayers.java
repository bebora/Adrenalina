package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitable;
import it.polimi.se2019.controller.EventVisitor;

import java.util.*;

public class SelectPlayers implements EventVisitable {
    List<String> players;
    public void accept(EventVisitor visitor){
        visitor.visit(this);
    }

}
