package it.polimi.se2019.view;

public class ClientView extends View {

   public ClientView() {
        super();
        this.receiver = new ConcreteViewReceiver(this);
   }
}
