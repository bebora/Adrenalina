package it.polimi.se2019.view.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

public class InfoAlert extends Alert {
    private static InfoAlert infoAlert;

    private InfoAlert(AlertType alertType, String content, ButtonType buttonType){
        super(alertType,content,buttonType);
        this.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        this.getDialogPane().lookupButton(ButtonType.OK).setOnMouseClicked(e->removeAlert());
        this.show();
    }

    static void handleAlert(String content){
        if(infoAlert != null) {
            infoAlert.close();
        }
        infoAlert = new InfoAlert(AlertType.INFORMATION,content,ButtonType.OK);
    }

    private static void removeAlert(){
        if(infoAlert != null){
            infoAlert.close();
            infoAlert = null;
        }
    }
}
