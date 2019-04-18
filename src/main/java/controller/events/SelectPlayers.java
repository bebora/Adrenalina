package controller.events;

import controller.Visitable;
import controller.Visitor;

import java.util.ArrayList;

public class SelectPlayers implements Visitable {
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
