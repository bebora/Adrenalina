package it.polimi.se2019.view.gui;

import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewTileCoords;
import javafx.scene.control.Button;

import java.util.List;

public class SenderButton extends Button {
    List<ViewPowerUp> powerUps;
    List<String> players;
    List<ViewTileCoords> viewTileCoords;
    EventUpdater eventUpdater;

    SenderButton(EventUpdater eventUpdater){
        this.eventUpdater = eventUpdater;
        this.setText("SEND");
    }

    public void setPowerUps(List<ViewPowerUp> powerUps) {
        this.powerUps = powerUps;
        this.setOnMouseClicked(e->eventUpdater.sendPowerUp(powerUps,false));
        this.setDisable(false);
    }

    public void setViewTileCoords(List<ViewTileCoords> viewTileCoords) {
        this.viewTileCoords = viewTileCoords;
        this.setOnMouseClicked(e->eventUpdater.sendTiles(viewTileCoords));
        this.setDisable(false);
    }

    public void setPlayers(List<String> players) {
        this.players = players;
        this.setOnMouseClicked(e->eventUpdater.sendPlayers(players));
        this.setDisable(false);
    }


}
