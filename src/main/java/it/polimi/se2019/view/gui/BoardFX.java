package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ChoiceDialog;
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
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class BoardFX extends StackPane {
    @FXML
    VBox yellowWeaponsBox;
    @FXML
    VBox redWeaponsBox;
    @FXML
    HBox blueWeaponsBox;
    @FXML
    HBox killshotDamageBox;
    @FXML
    ImageView powerUpsDeck;
    @FXML
    ImageView weaponDeck;
    @FXML
    GridPane tileBoard;
    @FXML
    ImageView boardView;
    @FXML GridPane selectionBoard;
    @FXML AnchorPane anchorPane;

    EventUpdater eventUpdater;

    SelectableOptionsWrapper selectableOptionsWrapper;
    SenderButton senderButton;

    ViewBoard viewBoard;
    List<ViewPlayer> players;
    ChoiceDialog<String> choiceDialog;
    List<ViewTileCoords> selectedCoords;
    List<String> selectedPlayers;
    List<String> blueWeapons;
    List<String> redWeapons;
    List<String> yellowWeapons;
    List<Shape> playersShapes;
    Image drop;

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
        drop = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/red_damage.png"
        ));
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

    public void setBoard(ViewBoard viewBoard){
        this.viewBoard = viewBoard;
        Image boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/boards/" + viewBoard.getName() + ".png"
        ));
        boardView.setImage(boardImage);
    }

    public void updateBoard(ViewBoard viewBoard){
        this.viewBoard = viewBoard;
        updateSkulls();
    }

    public void setSenderButton(SenderButton senderButton){
        this.senderButton = senderButton;
    }

    public void setDominationPane(DominationBoardFX dominationPane){
        anchorPane.getChildren().addAll(dominationPane);
        dominationPane.setLayoutX(58);
        dominationPane.setLayoutY(5);
        Scale scale = new Scale();
        scale.setPivotX(58);
        scale.setPivotY(5);
        scale.setX(0.80);
        scale.setY(0.80);
        dominationPane.getTransforms().addAll(scale);
    }

    void drawPlayers(List<ViewPlayer> players){
        this.players = players.stream().filter(v->!v.getDominationSpawn()).collect(Collectors.toList());
        playersShapes = new ArrayList<>();
        for(ViewPlayer player: players) {
            if(player.getTile() != null) {
                int playerX = player.getTile().getCoords().getPosx();
                int playerY = player.getTile().getCoords().getPosy();
                TilePane tile = (TilePane) GuiHelper.getNodeByIndex(tileBoard, playerX, playerY);
                Shape playerShape;
                if(!player.getDominationSpawn())
                    playerShape = new Circle(15.0, Paint.valueOf(GuiHelper.getColorHexValue(player.getColor())));
                else
                    playerShape = new Rectangle(30,30,Paint.valueOf(GuiHelper.getColorHexValue(player.getColor())));
                playerShape.setUserData(player.getUsername());
                playersShapes.add(playerShape);
                tile.getChildren().add(playerShape);
            }
        }
    }

    void drawAmmoCard(){
        for(ViewTile w: viewBoard.getTiles().stream().flatMap(Collection::stream).collect(Collectors.toList())) {
            if (w != null && !w.getAmmos().isEmpty()) {
                TilePane tile = (TilePane) GuiHelper.getNodeByIndex(tileBoard, w.getCoords().getPosx(), w.getCoords().getPosy());
                ImageView imageView = new ImageView();
                String ammoName = w.getAmmos().stream().map(s -> s.charAt(0)).map(String::valueOf).collect(Collectors.joining());
                if(AssetMaps.ammoCardAssets.containsKey(ammoName))
                    ammoName = AssetMaps.ammoCardAssets.get(ammoName);
                Image ammoCard = new Image(getClass().getClassLoader().
                        getResourceAsStream("assets/ammo/" + ammoName + ".png"));
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
                imageView.setImage(ammoCard);
                tile.getChildren().addAll(imageView);
            }
        }
    }

    void clearPlayers(){
        for(Node n:tileBoard.getChildren()){
            ((TilePane) n).getChildren().clear();
        }
        for(Node n: selectionBoard.getChildren()){
            n.setEffect(null);
            n.setOpacity(0);
            n.setOnMouseClicked(null);
        }
    }

    void showPossiblePlayers(List<String> players) {
        selectedPlayers = new ArrayList<>();
        for (String p : players) {
            Shape playerShape = playersShapes.stream().filter(c->c.getUserData().equals(p)).findAny().orElse(null);
            GuiHelper.applyBorder(playerShape,30);
            playerShape.setOnMouseClicked(e->selectPlayer(e));
            tileBoard.toFront();
        }
    }


    void selectPlayer(MouseEvent mouseEvent){
        Shape shape = (Shape)mouseEvent.getSource();
        String playerName = (String)shape.getUserData();
        if(selectedPlayers.contains(playerName))
            selectedPlayers.remove(playerName);
        else {
            selectedPlayers.add(playerName);
            shape.setOpacity(0.60);
        }
        if(selectableOptionsWrapper.getSelectablePlayers().checkForCoherency(selectedPlayers))
            senderButton.setPlayers(selectedPlayers);
        if(selectedPlayers.size() == selectableOptionsWrapper.getSelectablePlayers().getMaxSelectables())
            eventUpdater.sendPlayers(selectedPlayers);
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
        if(selectableOptionsWrapper.getSelectableTileCoords().checkForCoherency(selectedCoords))
            senderButton.setViewTileCoords(selectedCoords);
        if(selectedCoords.size() == selectableOptionsWrapper.getSelectableTileCoords().getMaxSelectables())
            eventUpdater.sendTiles(selectedCoords);

    }

    void showPossibleTiles(List<ViewTileCoords> tileCoords){
        selectedCoords = new ArrayList<>();
        for(ViewTileCoords t: tileCoords){
            Rectangle selectionRectangle = (Rectangle)GuiHelper.getNodeByIndex(selectionBoard,t.getPosx(),t.getPosy());
            selectionRectangle.setOpacity(0.50);
            selectionRectangle.setOnMouseClicked(e->selectTile(e));
        }
        selectionBoard.toFront();
    }

    void showPossibleRooms(List<String> rooms){
        for(String r : rooms){
            List<ViewTileCoords> roomTiles = viewBoard.getTiles().stream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(t->t.getRoom().equals(r))
                    .map(ViewTile::getCoords)
                    .collect(Collectors.toList());
            for(ViewTileCoords t: roomTiles){
                Rectangle selectionRectangle = (Rectangle)GuiHelper.getNodeByIndex(selectionBoard,t.getPosx(),t.getPosy());
                selectionRectangle.setOpacity(0.30);
                selectionRectangle.setMouseTransparent(false);
                selectionRectangle.setOnMouseClicked(e->selectRoom(e));
            }
        }
    }

    void showPossibleDirections(List<String> directions) {
        Map<String, String> arrows = Map.ofEntries(
                entry("NORTH", " ↑"),
                entry("SOUTH", " ↓"),
                entry("EAST", " →"),
                entry("WEST", " ←")
        );
        if(choiceDialog == null) {
            choiceDialog = new ChoiceDialog<>(null, directions.stream().map(d -> d+arrows.get(d)).collect(Collectors.toList()));
            Optional<String> choice = choiceDialog.showAndWait();
            choice.ifPresent(c -> eventUpdater.sendDirection(c.split(" ")[0]));
        }
    }



    void selectRoom(MouseEvent mouseEvent){
        Rectangle rectangle = (Rectangle)mouseEvent.getSource();
        String room = viewBoard.getTiles().get(GridPane.getRowIndex(rectangle)).get(GridPane.getColumnIndex(rectangle)).getRoom();
        eventUpdater.sendRoom(room);
    }

    void setSelectableOptionsWrapper(SelectableOptionsWrapper selectableOptionsWrapper){
        this.selectableOptionsWrapper = selectableOptionsWrapper;
        if(selectableOptionsWrapper.getAcceptedTypes().contains(ReceivingType.WEAPON))
            showPossibleWeapons();
        if(selectableOptionsWrapper.getAcceptedTypes().contains(ReceivingType.PLAYERS))
            showPossiblePlayers(selectableOptionsWrapper.getSelectablePlayers().getOptions());
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

    private void updateSkulls(){
        for(int i = 0; i < killshotDamageBox.getChildren().size() - viewBoard.getSkulls(); i++) {
            VBox sameShot = (VBox) killshotDamageBox.getChildren().get(i);
            sameShot.getChildren().clear();
        }
        for(int i = 0; i < viewBoard.getKillShotTrack().size(); i++){
            for(int j = 0; j < 2; j++){
                VBox sameShot = (VBox)killshotDamageBox.getChildren().get(i);
                ImageView dropView = new ImageView(drop);
                dropView.setFitWidth(23);
                dropView.setFitHeight(32);
                if(viewBoard.getKillShotTrack().get(i) != null) {
                    ColorAdjust colorAdjust = new ColorAdjust();
                    GuiHelper.hueShifter(viewBoard.getKillShotTrack().get(i),colorAdjust);
                    dropView.setEffect(colorAdjust);
                    sameShot.getChildren().addAll(dropView);
                }
            }
        }
    }
}
