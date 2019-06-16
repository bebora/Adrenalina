package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardFX extends StackPane {
    @FXML
    VBox yellowWeaponsBox;
    @FXML
    VBox redWeaponsBox;
    @FXML
    HBox blueWeaponsBox;
    @FXML
    ImageView powerUpsDeck;
    @FXML
    ImageView weaponDeck;
    @FXML
    GridPane tileBoard;
    @FXML
    ImageView boardView;
    @FXML GridPane selectionBoard;

    EventUpdater eventUpdater;

    SelectableOptionsWrapper selectableOptionsWrapper;

    List<ViewTileCoords> selectedCoords;
    List<String> blueWeapons;
    List<String> redWeapons;
    List<String> yellowWeapons;

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


    public void setBlueWeapons(List<String> blueWeapons){
        int i = 0;
        this.blueWeapons = blueWeapons;
        for(Node n: blueWeaponsBox.getChildren()) {
            n.setVisible(false);
            n.setEffect(null);
        }
        try{
            for(String w: blueWeapons) {
                Image blueWeapon = new Image(getClass().getClassLoader().getResourceAsStream(
                        "assets/cards/" + AssetMaps.weaponsAssetsMap.get(w)));
                ImageView blueWeaponView = (ImageView)blueWeaponsBox.getChildren().get(i);
                blueWeaponView.setImage(blueWeapon);
                blueWeaponView.setVisible(true);
                i++;
            }
        }catch (Exception e){
            Logger.log(Priority.DEBUG,e.getMessage());
        }
    }

    public void setRedWeapons(List<String> redWeapons){
        int i = 0;
        this.redWeapons = redWeapons;
        for(Node n: redWeaponsBox.getChildren()) {
            n.setVisible(false);
            n.setEffect(null);
        }
        try{
            for(String w: redWeapons){
                Image redWeapon = new Image(getClass().getClassLoader().getResourceAsStream(
                        "assets/cards/" + AssetMaps.weaponsAssetsMap.get(w)));
                ImageView redWeaponView = (ImageView)redWeaponsBox.getChildren().get(i);
                redWeaponView.setImage(GuiHelper.rotateImage(-90.0,redWeapon));
                redWeaponView.setVisible(true);
                i++;
            }
        }catch (Exception e){
            Logger.log(Priority.DEBUG,e.getMessage());
        }

    }

    public void setYellowWeapons(List<String> yellowWeapons){
        int i = 0;
        this.yellowWeapons = yellowWeapons;
        for(Node n: yellowWeaponsBox.getChildren()) {
            n.setVisible(false);
            n.setEffect(null);
        }
        try{
            for(String w: yellowWeapons){
                Image yellowWeapon = new Image(getClass().getClassLoader().getResourceAsStream(
                        "assets/cards/" + AssetMaps.weaponsAssetsMap.get(w)));
                ImageView yellowWeaponView = (ImageView)yellowWeaponsBox.getChildren().get(i);
                yellowWeaponView.setImage(GuiHelper.rotateImage(90.0,yellowWeapon));
                yellowWeaponView.setVisible(true);
                i++;
            }
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

    public void drawPlayer(ViewPlayer player){
        int playerX = player.getTile().getCoords().getPosx();
        int playerY = player.getTile().getCoords().getPosy();
        TilePane tile = (TilePane)GuiHelper.getNodeByIndex(tileBoard,playerX,playerY);
        tile.getChildren().add(new Circle(15.0,Paint.valueOf(GuiHelper.getColorHexValue(player.getColor()))));
    }

    public void clearPlayers(){
        for(Node n:tileBoard.getChildren()){
            ((TilePane) n).getChildren().clear();
        }
        for(Node n: selectionBoard.getChildren()){
            n.setEffect(null);
            n.setOpacity(0);
        }
    }

    @FXML
    public void rotateRightAndZoomImage(MouseEvent mouseEntered){
        ImageView zoomable = (ImageView)mouseEntered.getSource();
        zoomable.setViewOrder(-10);
        zoomable.setRotate(90);
        zoomable.setScaleX(1.5);
        zoomable.setScaleY(1.5);
        zoomable.setTranslateX(50);
    }

    @FXML
    public void rotateLeftAndZoomImage(MouseEvent mouseEntered){
        ImageView zoomable = (ImageView)mouseEntered.getSource();
        zoomable.setViewOrder(-10);
        zoomable.setRotate(-90);
        zoomable.setScaleX(1.5);
        zoomable.setScaleY(1.5);
        zoomable.setTranslateX(-50);
    }

    @FXML
    public void zoomImage(MouseEvent mouseEntered){
        ImageView zoomable = (ImageView)mouseEntered.getSource();
        zoomable.setViewOrder(-10);
        zoomable.setScaleX(1.5);
        zoomable.setScaleY(1.5);
        zoomable.setTranslateY(50);
    }

    @FXML
    public void deZoom(MouseEvent mouseExited){
        ImageView zoomable = (ImageView)mouseExited.getSource();
        zoomable.setViewOrder(0);
        zoomable.setRotate(0);
        zoomable.setScaleX(1);
        zoomable.setScaleY(1);
        zoomable.setTranslateX(0);
        zoomable.setTranslateY(0);
    }

    @FXML
    public void selectTile(MouseEvent event){
        Rectangle selectable = (Rectangle)event.getSource();
        ViewTileCoords selectedCoord = new ViewTileCoords(GridPane.getRowIndex(selectable),GridPane.getColumnIndex(selectable));
        if(selectable.getEffect() != null){
            selectedCoords.remove(selectedCoord);
            selectable.setEffect(null);
        }else {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(-0.30);
            selectable.setEffect(colorAdjust);
            selectedCoords.add(selectedCoord);
        }
        if(selectedCoords.size() == selectableOptionsWrapper.getSelectableTileCoords().getMaxSelectables())
            eventUpdater.sendTiles(selectedCoords);

    }

    public void showPossibleTiles(List<ViewTileCoords> tileCoords){
        selectedCoords = new ArrayList<>();
        for(ViewTileCoords t: tileCoords){
            Rectangle selectionRectangle = (Rectangle)GuiHelper.getNodeByIndex(selectionBoard,t.getPosx(),t.getPosy());
            selectionRectangle.setOpacity(0.50);
            selectionRectangle.setOnMouseClicked(e->selectTile(e));
        }
    }

    public void setSelectableOptionsWrapper(SelectableOptionsWrapper selectableOptionsWrapper){
        this.selectableOptionsWrapper = selectableOptionsWrapper;
        if(selectableOptionsWrapper.getAcceptedTypes().contains(ReceivingType.WEAPON))
            showPossibleWeapons();
    }

    public void setEventUpdater(EventUpdater eventUpdater){
        this.eventUpdater = eventUpdater;
    }

    private void showPossibleWeapons(){
        SelectableOptions<String> selectableOptions = selectableOptionsWrapper.getSelectableWeapons();
        DropShadow borderGlow= new DropShadow();
        borderGlow.setColor(Color.RED);
        borderGlow.setWidth(50);
        borderGlow.setHeight(50);
        for(String weaponName: selectableOptions.getOptions()){
            if(blueWeapons.contains(weaponName)){
                blueWeaponsBox.getChildren().get(blueWeapons.indexOf(weaponName)).setEffect(borderGlow);
                blueWeaponsBox.getChildren().get(blueWeapons.indexOf(weaponName)).setOnMouseClicked(e->selectWeaponBlue(e));
            }else if(redWeapons.contains(weaponName)){
                redWeaponsBox.getChildren().get(redWeapons.indexOf(weaponName)).setEffect(borderGlow);
                redWeaponsBox.getChildren().get(redWeapons.indexOf(weaponName)).setOnMouseClicked(e->selectWeaponRed(e));
            }else if(yellowWeapons.contains(weaponName)){
                yellowWeaponsBox.getChildren().get(yellowWeapons.indexOf(weaponName)).setEffect(borderGlow);
                yellowWeaponsBox.getChildren().get(yellowWeapons.indexOf(weaponName)).setOnMouseClicked(e->selectWeaponYellow(e));
            }
        }
    }

    private void selectWeaponRed(MouseEvent mouseEvent){
        ImageView imageView = (ImageView)mouseEvent.getSource();
        if(imageView.getEffect() != null) {
            int weaponIndex = redWeaponsBox.getChildren().indexOf((ImageView) mouseEvent.getSource());
            eventUpdater.sendWeapon(redWeapons.get(weaponIndex));
        }
    }

    private void selectWeaponBlue(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        if (imageView.getEffect() != null){
            int weaponIndex = blueWeaponsBox.getChildren().indexOf((ImageView) mouseEvent.getSource());
            eventUpdater.sendWeapon(blueWeapons.get(weaponIndex));
        }
    }

    private void selectWeaponYellow(MouseEvent mouseEvent){
        ImageView imageView = (ImageView)mouseEvent.getSource();
        if(imageView.getEffect() != null){
            int weaponIndex = yellowWeaponsBox.getChildren().indexOf((ImageView)mouseEvent.getSource());
            eventUpdater.sendWeapon(yellowWeapons.get(weaponIndex));
        }
    }
}
