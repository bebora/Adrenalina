package it.polimi.se2019.view;

import it.polimi.se2019.model.updatemessage.UpdateVisitable;

public class ClientView extends View {

   public ClientView() {
        super();
        this.receiver = new ConcreteViewReceiver(this);
   }
}
