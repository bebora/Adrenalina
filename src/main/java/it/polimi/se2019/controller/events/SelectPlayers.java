package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.Visitable;
import it.polimi.se2019.controller.Visitor;

import java.util.*;

public class SelectPlayers implements Visitable {
    List<String> players;
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
