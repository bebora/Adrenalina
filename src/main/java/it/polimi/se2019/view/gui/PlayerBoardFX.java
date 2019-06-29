package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.ViewPlayer;
import it.polimi.se2019.view.ViewWeapon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class PlayerBoardFX extends AnchorPane {
    @FXML HBox damagesList;
    @FXML ImageView playerBoardView;
    @FXML TilePane ammoPane;
    @FXML HBox marksBox;
    @FXML Text username;
    @FXML ImageView frenzyActions;
    @FXML HBox skullBox;
    @FXML HBox weaponBox;

    Image boardImage;
    Image skull;
    ViewPlayer viewPlayer;
    int originalRewardSize;


    PlayerBoardFX(ViewPlayer viewPlayer) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "fxml/PlayerBoardFX.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            Logger.log(Priority.DEBUG, exception.getMessage());
        }
        this.username.setText(viewPlayer.getUsername());
        this.originalRewardSize = viewPlayer.getRewardPoints().size();

    }

    private void updateImage(String color){
        boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/player_board/" + AssetMaps.colorToPlayerBoard.get(color)));
        Image frenzyActionImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/player_board/" + AssetMaps.colorToFrenzyActions.get(color)));
        skull = new Image(getClass().getClassLoader().getResourceAsStream("assets/skull.png"));
        playerBoardView.setImage(boardImage);
        frenzyActions.setImage(frenzyActionImage);
        frenzyActions.setVisible(false);
    }


    private void displayDamages(List<String> damages) {
        int i = 0;
        for (String color : damages) {
            ImageView imageView = (ImageView)damagesList.getChildren().get(i);

            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0);
            imageView.effectProperty().setValue(colorAdjust);
            GuiHelper.hueShifter(color,colorAdjust);
            for(int j = i; j<damagesList.getChildren().size(); j++){
                damagesList.getChildren().get(j).setOpacity(0.0);
            }

            imageView.setOpacity(1.0);
            imageView.effectProperty().setValue(colorAdjust);

            i++;

        }
    }

    private void displayAmmos(List<String> ammos){
        ammoPane.getChildren().clear();
        for(String a: ammos){
            Rectangle ammo = new Rectangle(10,10, Color.valueOf(a));
            ammoPane.getChildren().addAll(ammo);
        }
    }

    private void displayMarks(List<String> marks){
        marksBox.getChildren().forEach(e->e.setVisible(false));
        int i = 0;
        for (String color : marks) {
            ImageView imageView = (ImageView)marksBox.getChildren().get(i);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0);
            imageView.effectProperty().setValue(colorAdjust);
            GuiHelper.hueShifter(color,colorAdjust);
            imageView.setVisible(true);
            imageView.effectProperty().setValue(colorAdjust);
            i++;

        }
    }

    void updatePlayerInfo(ViewPlayer viewPlayer){
        this.viewPlayer = viewPlayer;
        updateImage(viewPlayer.getColor());
        displayDamages(viewPlayer.getDamages());
        displayAmmos(viewPlayer.getAmmos());
        displayMarks(viewPlayer.getMarks());
        triggerFrenzy();
        drawSkulls();
        drawWeapons();
    }

    private void triggerFrenzy(){
        if(viewPlayer.isFrenzyActions())
            frenzyActions.setVisible(true);
        if(viewPlayer.isFrenzyBoard()){
            boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/player_board/" + viewPlayer.getColor().toLowerCase() + "_player_board_back.png"));
            playerBoardView.setImage(boardImage);
            damagesList.setSpacing(11);
            damagesList.setPadding(new Insets(0,0,0,2));
            damagesList.setLayoutX(45);
            skullBox.setLayoutX(130);
            originalRewardSize = viewPlayer.getRewardPoints().size();
        }
    }

    private void drawSkulls(){
        skullBox.getChildren().clear();
        for(int i = 0; i < originalRewardSize - viewPlayer.getRewardPoints().size(); i++){
            ImageView imageView = new ImageView(skull);
            imageView.setFitHeight(32);
            imageView.setFitWidth(23);
            skullBox.getChildren().addAll(imageView);
        }

    }

    private void drawWeapons(){
        weaponBox.getChildren().clear();
        for(ViewWeapon w: viewPlayer.getUnloadedWeapons()){
            Image weapon = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/" + AssetMaps.weaponsAssetsMap.get(w.getName())
            ));
            ImageView weaponView = new ImageView(weapon);
            weaponView.setFitWidth(40);
            weaponView.setFitHeight(63);
            weaponBox.getChildren().addAll(weaponView);
        }
    }


}
