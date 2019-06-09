package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.ViewWeapon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;


import java.io.IOException;
import java.util.List;

public class BoardFX extends StackPane {
    @FXML
    ImageView redWeaponView1;
    @FXML
    ImageView redWeaponView2;
    @FXML
    ImageView redWeaponView3;
    @FXML
    ImageView blueWeaponView1;
    @FXML
    ImageView blueWeaponView2;
    @FXML
    ImageView blueWeaponView3;
    @FXML
    ImageView yellowWeaponView1;
    @FXML
    ImageView yellowWeaponView2;
    @FXML
    ImageView yellowWeaponView3;
    @FXML
    ImageView powerUpsDeck;
    @FXML
    ImageView weaponDeck;
    @FXML
    GridPane tileBoard;
    @FXML
    ImageView boardView;


    public BoardFX() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "fxml/BoardFX.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            Logger.log(Priority.DEBUG, exception.getMessage());
        }
    }


    //TODO:choose the correct image instead of these test templates
    public void setBlueWeapons(List<ViewWeapon> blueWeapons){
        try{
            Image blueWeapon1 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_022.png"));
            Image blueWeapon2 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_023.png"));
            Image blueWeapon3 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_024.png"));
            blueWeaponView1.setImage(blueWeapon1);
            blueWeaponView2.setImage(blueWeapon2);
            blueWeaponView3.setImage(blueWeapon3);
        }catch (Exception e){
            Logger.log(Priority.DEBUG,e.getMessage());
        }
    }

    public void setRedWeapons(List<ViewWeapon> redWeapons){
        try{
            Image redWeapon1 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_0210.png"));
            Image redWeapon2 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_0211.png"));
            Image redWeapon3 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_0212.png"));
            redWeaponView1.setImage(Helper.rotateImage(-90.0,redWeapon1));
            redWeaponView2.setImage(Helper.rotateImage(-90.0,redWeapon2));
            redWeaponView3.setImage(Helper.rotateImage(-90.0,redWeapon3));
        }catch (Exception e){
            Logger.log(Priority.DEBUG,e.getMessage());
        }
    }

    public void setYellowWeapons(List<ViewWeapon> yellowWeapons){
        try{
            Image yellowWeapon1 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_0220.png"));
            Image yellowWeapon2 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_0221.png"));
            Image yellowWeapon3 = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/AD_weapons_IT_0222.png"));
            yellowWeaponView1.setImage(Helper.rotateImage(90.0,yellowWeapon1));
            yellowWeaponView2.setImage(Helper.rotateImage(90.0,yellowWeapon2));
            yellowWeaponView3.setImage(Helper.rotateImage(90.0,yellowWeapon3));
        }catch (Exception e){
            Logger.log(Priority.DEBUG,e.getMessage());
        }
    }
    public void setBoard(String boardName){
        Image boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/boards/" + boardName + ".png"
        ));
        boardView.setImage(boardImage);
    }

}
