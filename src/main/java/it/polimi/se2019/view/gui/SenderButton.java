package it.polimi.se2019.view.gui;

import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;
import javafx.scene.control.Button;

import java.util.List;

/**
 * A button used to send various lists that could possibly
 * be empty or smaller then the maximum selectable size.
 */
public class SenderButton extends Button {
    private List<ViewPowerUp> powerUps;
    private List<String> players;
    private List<ViewTileCoords> viewTileCoords;
    private EventUpdater eventUpdater;

    SenderButton(EventUpdater eventUpdater){
        this.eventUpdater = eventUpdater;
        this.setText("SEND");
    }

    /**
     * Set the select powerUps list and make
     * the button send them to the server on click.
     * @param powerUps a list of selected powerUps
     */
    public void setPowerUps(List<ViewPowerUp> powerUps) {
        this.powerUps = powerUps;
        this.setOnMouseClicked(e->eventUpdater.sendPowerUp(powerUps,false));
        this.setDisable(false);
    }

    /**
     * Set the select tile coords list and make
     * the button send them to the server on click.
     * @param viewTileCoords a list of selected coords
     */
    void setViewTileCoords(List<ViewTileCoords> viewTileCoords) {
        this.viewTileCoords = viewTileCoords;
        this.setOnMouseClicked(e->eventUpdater.sendTiles(viewTileCoords));
        this.setDisable(false);
    }

    /**
     * Set the select players list and make
     * the button send them to the server on click.
     * @param players a list of selected players
     */
    public void setPlayers(List<String> players) {
        this.players = players;
        this.setOnMouseClicked(e->eventUpdater.sendPlayers(players));
        this.setDisable(false);
    }


}
