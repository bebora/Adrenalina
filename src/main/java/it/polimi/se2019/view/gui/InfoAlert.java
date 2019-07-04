package it.polimi.se2019.view.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/**
 * An alert of which only and instance can be shown on screen at any time.
 */
class InfoAlert extends Alert {
    private static InfoAlert infoAlert;

    /**
     * Creates a new Alert of the given AlertType
     * containing the given content.
     * @param alertType the AlertType of the Alert
     * @param content the content of the Alert
     * @param buttonType the ButtonType contained in the Alert
     */
    private InfoAlert(AlertType alertType, String content, ButtonType buttonType){
        super(alertType,content,buttonType);
        this.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        this.getDialogPane().lookupButton(ButtonType.OK).setOnMouseClicked(e->removeAlert());
        this.show();
    }

    /**
     * Creates a new InfoAlert with the given content.
     * If an Alert already exists closes it and replaces it
     * with the one.
     * @param content content to be displayed through the Alert
     */
    static void handleAlert(String content){
        if(infoAlert != null) {
            infoAlert.close();
        }
        infoAlert = new InfoAlert(AlertType.INFORMATION,content,ButtonType.OK);
    }

    /**
     * Close the currently displayed Alert and sets it
     * to null after the associated button is clicked.
     */
    private static void removeAlert(){
        if(infoAlert != null){
            infoAlert.close();
            infoAlert = null;
        }
    }
}
