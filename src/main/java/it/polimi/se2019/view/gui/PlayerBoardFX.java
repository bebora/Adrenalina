package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.ViewPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.List;

public class PlayerBoardFX extends AnchorPane {
    @FXML
    HBox damagesList;
    @FXML
    ImageView playerBoardView;
    @FXML
    TilePane ammoPane;
    @FXML HBox marksBox;

    Image boardImage;

    PlayerBoardFX() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "fxml/PlayerBoardFX.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            Logger.log(Priority.DEBUG, exception.getMessage());
        }
    }

    private void updateImage(String color){
        boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/player_board/" + AssetMaps.colorToPlayerBoard.get(color)));
        playerBoardView.setImage(boardImage);
    }

    private void displayDamages(List<String> damages) {
        int i = 0;
        for (String color : damages) {
            ImageView imageView = (ImageView)damagesList.getChildren().get(i);

            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0);
            imageView.effectProperty().setValue(colorAdjust);

            switch(color.toUpperCase()){
                case "RED":
                    colorAdjust.setHue(0);
                    break;
                case "BLUE":
                    colorAdjust.setHue(-0.70);
                    break;
                case "PURPLE":
                    colorAdjust.setHue(-0.30);
                    break;
                case "GREEN":
                    colorAdjust.setHue(0.6);
                    break;
                case "WHITE":
                    colorAdjust.setBrightness(1.0);
                    break;
                case "YELLOW":
                    colorAdjust.setHue(0.30);
                    break;
                default:
                    break;
            }
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
        marksBox.getChildren().clear();
        for(String m:marks){
            Circle circle = new Circle(5,Color.valueOf(m));
            marksBox.getChildren().addAll(circle);
        }
    }

    void updatePlayerInfo(ViewPlayer viewPlayer){
        updateImage(viewPlayer.getColor());
        displayDamages(viewPlayer.getDamages());
        displayAmmos(viewPlayer.getAmmos());
        displayMarks(viewPlayer.getMarks());
    }
}
