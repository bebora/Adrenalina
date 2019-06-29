package it.polimi.se2019.view.gui;

import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.network.EventUpdater;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.List;

public class ActionButtons extends HBox {
    Button attack;
    Button move;
    Button grab;
    Button reload;
    Button stop;
    Button info;
    SenderButton send;

    public List<String> possibleActions;
    EventUpdater eventUpdater;
    ReceivingType receivingType;

    public ActionButtons(EventUpdater eventUpdater) {
        this.eventUpdater = eventUpdater;
        attack = new Button();
        attack.setText("ATTACK");
        attack.setOnMouseClicked(e->onClick(e));
        move = new Button();
        move.setText("MOVE");
        move.setOnMouseClicked(e->onClick(e));
        grab = new Button();
        grab.setText("GRAB");
        grab.setOnMouseClicked(e->onClick(e));
        reload = new Button();
        reload.setText("RELOAD");
        reload.setOnMouseClicked(e->onClick(e));
        send = new SenderButton(eventUpdater);
        stop = new Button();
        stop.setText("STOP");
        info = new Button();
        info.setText("INFO");
        this.getChildren().addAll(attack,move,grab,reload,send,stop);
        clearPossibleActions();
    }

    private void onClick(MouseEvent mouseEvent){
        Button source = (Button)mouseEvent.getSource();
        if(possibleActions.contains(source.getText())){
            eventUpdater.sendAction(source.getText());
        }
    }

    public void clearPossibleActions(){
        for(Node n: this.getChildren()){
            n.setDisable(true);
        }
    }

    public void setPossibleActions(List<String> possibleActions) {
        this.possibleActions = possibleActions;
        for (String s : possibleActions) {
            switch (s) {
                case "ATTACK":
                    attack.setDisable(false);
                    break;
                case "MOVE":
                    move.setDisable(false);
                    break;
                case "GRAB":
                    grab.setDisable(false);
                    break;
                case "RELOAD":
                    reload.setDisable(false);
                    break;
                default:
                    break;
            }
        }
    }

    public void enableStop(){
        stop.setDisable(false);
        stop.setOnMouseClicked(e->eventUpdater.sendStop());
    }

    public SenderButton getSenderButton(){
        return send;
    }
}
