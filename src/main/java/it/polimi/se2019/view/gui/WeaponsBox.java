package it.polimi.se2019.view.gui;

import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.SelectableOptions;
import it.polimi.se2019.view.ViewPowerUp;
import it.polimi.se2019.view.ViewWeapon;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a box containing all the weapons that the player
 * currently owns and contains the methods used to highlight and
 * select them.
 */
public class WeaponsBox extends HBox {
    private List<String> weapons;
    private SelectableOptions<String> selectableEffects;
    private EventUpdater eventUpdater;

    /**
     * Creates a new <code>WeaponsBox</code> with the specified eventUpdater used
     * to send selected powerUps to the server.
     * @param eventUpdater an eventUpdater used to communicate with the backend
     */
    WeaponsBox(EventUpdater eventUpdater){
        this.eventUpdater = eventUpdater;
    }
    public void setWeapons(List<ViewWeapon> viewWeapons){
        weapons= viewWeapons.stream().map(ViewWeapon::getName).collect(Collectors.toList());
        this.getChildren().clear();
        for(ViewWeapon w: viewWeapons){
            Image weapon = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/"+
                            AssetMaps.weaponsAssetsMap.get(w.getName())
            ));
            ImageView weaponView = new ImageView();
            weaponView.setFitHeight(136);
            weaponView.setFitWidth(90);
            weaponView.setImage(weapon);
            this.getChildren().addAll(weaponView);
        }
    }

    /**
     * Highlight the weapons that the player can currently select according to
     * the current selectableOptionWrapper. Associates each <code>ImageView<\code> with
     * selectWeapon to allow selection.
     * @param selectableOptions options about the selectable weapons
     */
    void highlightSelectableWeapons(SelectableOptions<String> selectableOptions){
        List<String> selectableWeapons = selectableOptions.getOptions();
        for(String w: selectableWeapons){
            for(int i = 0; i < weapons.size(); i++){
                if(weapons.get(i).equals(w)){
                    ImageView weaponView = (ImageView)this.getChildren().get(i);
                    DropShadow borderGlow = new DropShadow();
                    borderGlow.setColor(Color.RED);
                    borderGlow.setWidth(30);
                    borderGlow.setHeight(30);
                    weaponView.setEffect(borderGlow);
                    weaponView.setOnMouseClicked(e->selectWeapon(e));
                }
            }
        }
    }

    /**
     * Selects the weapon corresponding to the clicked <code>ImageView</code>
     * and send it to the server.
     * @param mouseEvent the mouse click registered by the weapon <code>ImageView</code>
     */
    private void selectWeapon(MouseEvent mouseEvent){
        ImageView selectedView = (ImageView)mouseEvent.getSource();
        selectedView.setScaleX(0.75);
        selectedView.setScaleY(0.75);
        int selectedIndex = this.getChildren().indexOf(selectedView);
        eventUpdater.sendWeapon(weapons.get(selectedIndex));
    }

    /**
     * Set the <code>SelectableOptions</code> related to the effects after a weapon selection
     * and shows them to the player.
     * @param selectableEffects options about the effects that can be selected
     */
    public void setSelectableEffects(SelectableOptions<String> selectableEffects) {
        if(this.selectableEffects == null || !this.selectableEffects.equals(selectableEffects)) {
            this.selectableEffects = selectableEffects;
            showSelectableEffects();
        }
    }

    /**
     * Create a <code>ChoiceDialog</code> with the effects that the player can
     * select and sends the selected one to the server using the eventUpdater.
     */
    private void showSelectableEffects(){
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null,selectableEffects.getOptions());
        dialog.setTitle("Select effect:");
        dialog.setHeaderText("Select the effect that you want to use:");
        dialog.setContentText("Selectable effects:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(e->eventUpdater.sendEffect(e));
        dialog.close();
    }

}
