package it.polimi.se2019.controller.events;

import it.polimi.se2019.controller.EventVisitor;
import it.polimi.se2019.view.View;

public class VisitorVirtualViewSetter extends EventVisitor {
    View vv;
    public VisitorVirtualViewSetter(View vv) {
        this.vv = vv;
    }

    public void visit(ConnectionRequest event) {
        event.setVv(vv);
    }

}
