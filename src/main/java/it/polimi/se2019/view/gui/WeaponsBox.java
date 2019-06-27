package it.polimi.se2019.view.gui;

import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.SelectableOptions;
import it.polimi.se2019.view.ViewPowerUp;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import java.util.List;

public class WeaponsBox extends HBox {
    List<String> weapons;
    SelectableOptions<String> selectableOptions;
    SelectableOptions<String> selectableEffects;
    EventUpdater eventUpdater;

    public WeaponsBox(EventUpdater eventUpdater){
        this.eventUpdater = eventUpdater;
    }
    public void setWeapons(List<String> weapons){
        this.weapons = weapons;
        this.getChildren().clear();
        for(String w: weapons){
            Image weapon = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/"+
                            AssetMaps.weaponsAssetsMap.get(w)
            ));
            ImageView weaponView = new ImageView();
            weaponView.setFitHeight(136);
            weaponView.setFitWidth(90);
            weaponView.setImage(weapon);
            this.getChildren().addAll(weaponView);
        }
    }

    public void highlightSelectableWeapons(SelectableOptions<String> selectableOptions){
        this.selectableOptions = selectableOptions;
        List<String> selectableWeapons = selectableOptions.getOptions();
        for(String w: selectableWeapons){
            for(int i = 0; i < weapons.size(); i++){
                if(weapons.get(i).equals(w)){
                    ImageView weaponView = (ImageView)this.getChildren().get(i);
                    DropShadow borderGlow= new DropShadow();
                    borderGlow.setColor(Color.RED);
                    borderGlow.setWidth(30);
                    borderGlow.setHeight(30);
                    weaponView.setEffect(borderGlow);
                    weaponView.setOnMouseClicked(e->selectWeapon(e));
                }
            }
        }
    }

    private void selectWeapon(MouseEvent mouseEvent){
        ImageView selectedView = (ImageView)mouseEvent.getSource();
        selectedView.setScaleX(0.75);
        selectedView.setScaleY(0.75);
        int selectedIndex = this.getChildren().indexOf(selectedView);
        eventUpdater.sendWeapon(weapons.get(selectedIndex));
        selectedView.setOnMouseClicked(e->showSelectableEffects());
    }

    public void setSelectableEffects(SelectableOptions<String> selectableEffects) {
        this.selectableEffects = selectableEffects;
        showSelectableEffects();
    }

    private void showSelectableEffects(){
        PopupControl popup = new PopupControl();
        VBox effectBox = new VBox();
        effectBox.setSpacing(15);
        for(String effect: selectableEffects.getOptions()){
            Button button = new Button();
            button.setText(effect);
            button.setOnMouseClicked(e->eventUpdater.sendEffect(button.getText()));
        }
        popup.getScene().setRoot(effectBox);
        popup.show(this,100,100);
        popup.setAutoHide(true);
    }

}
