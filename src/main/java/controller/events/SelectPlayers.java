package controller.events;

import controller.Visitable;
import controller.Visitor;
import model.Player;

import java.util.ArrayList;
import java.util.*;

public class SelectPlayers implements Visitable {
    List<Player> players;
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
