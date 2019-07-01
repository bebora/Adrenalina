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

    /**
     * Buttons that allow choosing actions or requesting info during the game.
     * The parameter is the eventUpdater of the corresponding view and
     * its used to send the selected actions.
     * @param eventUpdater the eventUpdter of the corresponding view
     */
    public ActionButtons(EventUpdater eventUpdater) {
        this.eventUpdater = eventUpdater;
        attack = new Button();
        attack.setText("ATTACK");
        attack.setOnMouseClicked(this::onClick);
        attack.setMaxHeight(40);
        move = new Button();
        move.setText("MOVE");
        move.setOnMouseClicked(this::onClick);
        move.setMaxHeight(40);
        grab = new Button();
        grab.setText("GRAB");
        grab.setOnMouseClicked(this::onClick);
        grab.setMaxHeight(40);
        reload = new Button();
        reload.setText("RELOAD");
        reload.setOnMouseClicked(this::onClick);
        reload.setMaxHeight(40);
        send = new SenderButton(eventUpdater);
        send.setMaxHeight(40);
        stop = new Button();
        stop.setText("STOP");
        stop.setMaxHeight(40);
        info = new Button();
        info.setText("INFO");
        info.setMaxHeight(40);
        this.getChildren().addAll(attack,move,grab,reload,send,info,stop);
        clearPossibleActions();
    }

    /**
     * Gets the corresponding action from the button.
     * Each button contains the corresponding action name as text.
     * @param mouseEvent
     */
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

    /**
     * Enables only the buttons corresponding to the actions
     * that the player can currently use
     * @param possibleActions a list of actions that the player can use at the moment
     */
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

    public Button getInfo() {
        return info;
    }
}
