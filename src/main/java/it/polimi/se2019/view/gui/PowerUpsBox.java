package it.polimi.se2019.view.gui;

import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.SelectableOptions;
import it.polimi.se2019.view.ViewPowerUp;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a box containing all the powerUps that the player
 * currently owns and contains the methods used to highlight and
 * select them.
 */
public class PowerUpsBox extends HBox {
    private List<ViewPowerUp> powerUps;
    private List<ViewPowerUp> selectedPowerUp;
    private SelectableOptions<ViewPowerUp> powerUpSelectableOptions;
    private EventUpdater eventUpdater;

    /**
     * Creates a new PowerUpsBox with the specified eventUpdater used
     * to send selected powerUps to the server.
     * @param eventUpdater an eventUpdater used to communicate with the backend
     */
    PowerUpsBox(EventUpdater eventUpdater){
        this.eventUpdater = eventUpdater;
    }

    /**
     * Updates the displayed powerUps to show the powerUp that the player
     * currently has.
     * @param powerUps a list of <code>ViewPowerUp</code> owned by the player
     */
    public void setPowerUps(List<ViewPowerUp> powerUps){
        this.powerUps = powerUps;
        this.getChildren().clear();
        for(ViewPowerUp w: powerUps){
            Image powerUp = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/"+
                    AssetMaps.powerUpsAssets.get(w.getName()+w.getDiscardAward())
            ));
            ImageView powerUpsView = new ImageView();
            powerUpsView.setFitHeight(136);
            powerUpsView.setFitWidth(90);
            powerUpsView.setImage(powerUp);
            this.getChildren().addAll(powerUpsView);
        }
    }

    /**
     * Highlight the powerUps that the player can currently select according to
     * the current selectableOptionWrapper. Associates each <code>ImageView<\code> with selectPowerUp
     * correct method to allow selection.
     * @param powerUpSelectableOptions the SelectableOptions regarding powerUps
     */
    void highlightSelectablePowerUps(SelectableOptions<ViewPowerUp> powerUpSelectableOptions){
        this.powerUpSelectableOptions = powerUpSelectableOptions;
        List<ViewPowerUp> selectablePowerUps = powerUpSelectableOptions.getOptions();
        selectedPowerUp = new ArrayList<>();
        for(ViewPowerUp w: selectablePowerUps){
            for(int i = 0; i < powerUps.size(); i++){
                if(powerUps.get(i).getName().equals(w.getName()) && powerUps.get(i).getDiscardAward().equals(w.getDiscardAward())){
                    ImageView powerUpView = (ImageView)this.getChildren().get(i);
                    DropShadow borderGlow= new DropShadow();
                    borderGlow.setColor(Color.RED);
                    borderGlow.setWidth(30);
                    borderGlow.setHeight(30);
                    powerUpView.setEffect(borderGlow);
                    powerUpView.setOnMouseClicked(e->selectPowerUp(e));
                }
            }
        }
    }

    /**
     * Selects the powerUp corresponding to the clicked <code>ImageView</code>
     * and send it to the server.
     * @param mouseEvent the mouse click registered by the powerUp <code>ImageView</code>
     */
    private void selectPowerUp(MouseEvent mouseEvent){
        ImageView selectedView = (ImageView)mouseEvent.getSource();
        selectedView.setScaleX(0.75);
        selectedView.setScaleY(0.75);
        int selectedIndex = this.getChildren().indexOf(selectedView);
        selectedPowerUp.add(powerUps.get(selectedIndex));
        if(selectedPowerUp.size() == powerUpSelectableOptions.getMaxSelectables())
                eventUpdater.sendPowerUp(selectedPowerUp,false);
    }

}
