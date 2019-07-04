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

/**
 * Represents a generic board.
 */
public class BoardFX extends StackPane {
    @FXML
    private VBox yellowWeaponsBox;
    @FXML
    private VBox redWeaponsBox;
    @FXML
    private HBox blueWeaponsBox;
    @FXML
    private HBox killshotDamageBox;
    @FXML
    private ImageView powerUpsDeck;
    @FXML
    private ImageView weaponDeck;
    @FXML
    private GridPane tileBoard;
    @FXML
    private ImageView boardView;
    @FXML private GridPane selectionBoard;
    @FXML private  AnchorPane anchorPane;

    private EventUpdater eventUpdater;

    private SelectableOptionsWrapper selectableOptionsWrapper;
    private SenderButton senderButton;

    private ViewBoard viewBoard;
    private ChoiceDialog<String> choiceDialog;
    private List<ViewTileCoords> selectedCoords;
    private List<String> selectedPlayers;
    private List<String> blueWeapons;
    private List<String> redWeapons;
    private List<String> yellowWeapons;
    private List<Shape> playersShapes;
    private Image drop;

    /**
     * Creates a new BoardFX loading static elements from the
     * corresponding fxml.The board image should be changed later to
     * the represent the correct board from the game.
     */
    BoardFX() {
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

    /**
     * Shows the player the weapons currently located in the blue spawn point
     * and memorize them to allow future selection
     * @param blueWeapons a list of weapons currently in the blue spawn point
     */
    void setBlueWeapons(List<String> blueWeapons){
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

    /**
     * Shows the player the weapons currently located in the red spawn point
     * and memorize them to allow future selection
     * @param redWeapons a list of weapons currently in the red spawn point
     */
    void setRedWeapons(List<String> redWeapons){
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

    /**
     * Shows the player the weapons currently located in the yellow spawn point
     * and memorize them to allow future selection
     * @param yellowWeapons a list of weapons currently in the yellow spawn point
     */
    void setYellowWeapons(List<String> yellowWeapons){
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

    /**
     *Sets the initial viewBoard and loads the corresponding asset.
     * This method should be used only at the beginning of the game.
     * @param viewBoard the ViewBoard used for the current match
     */
    public void setBoard(ViewBoard viewBoard){
        this.viewBoard = viewBoard;
        Image boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/boards/" + viewBoard.getName() + ".png"
        ));
        boardView.setImage(boardImage);
    }

    /**
     * Updates the viewBoard during every totalUpdate
     * and update the killshotTrack if the current game is normal
     * @param viewBoard the current viewBoard received during the latest update
     */
    void updateBoard(ViewBoard viewBoard){
        this.viewBoard = viewBoard;
        if(killshotDamageBox.isVisible())
            updateSkulls();
    }

    /**
     * Sets a senderButton used for sending selections numerically inferior
     * to the maximum allowed.
     * @param senderButton the senderButton corresponding to the player view
     */
    void setSenderButton(SenderButton senderButton){
        this.senderButton = senderButton;
    }

    /**
     * Places on the board the dominationBoard necessary for
     * the domination game mode.
     * @param dominationPane an empty dominationPane to be placed on the board
     */
    void setDominationPane(DominationBoardFX dominationPane){
        anchorPane.getChildren().addAll(dominationPane);
        dominationPane.setLayoutX(58);
        dominationPane.setLayoutY(5);
        Scale scale = new Scale();
        scale.setPivotX(58);
        scale.setPivotY(5);
        scale.setX(0.80);
        scale.setY(0.80);
        dominationPane.getTransforms().addAll(scale);
        killshotDamageBox.setVisible(false);
    }

    /**
     * Updates the positions of the shapes representing the players
     * on the map according to the latest update and memorizes
     * each player username in their corresponding shape.
     *  Each shape is filled with the corresponding player color,
     *  squares represent actual spawn points, while circles
     *  represent actual players.
     * @param players the players to be displayed on the map
     */
    void drawPlayers(List<ViewPlayer> players){
        playersShapes = new ArrayList<>();
        for(ViewPlayer player: players) {
            if(player.getTile() != null) {
                int playerX = player.getTile().getCoords().getPosx();
                int playerY = player.getTile().getCoords().getPosy();
                TilePane tile = (TilePane) GuiHelper.getNodeByIndex(tileBoard, playerX, playerY);
                Shape playerShape;
                if(!player.getDominationSpawn())
                    playerShape = new Circle(15.0, Color.valueOf(player.getColor()));
                else
                    playerShape = new Rectangle(30,30,Color.valueOf(player.getColor()));
                playerShape.setUserData(player.getUsername());
                playersShapes.add(playerShape);
                tile.getChildren().add(playerShape);
            }
        }
    }

    /**
     * Shows the ammo tiles currently present on the game map.
     * Their position inside each tiles is not fixed but change depending on
     * the number of players in the same tile.
     */
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

    /**
     * Removes player shapes from the map in order to update
     * their position and clears the board used for showing
     * selectable tiles.
     */
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

    /**
     * Applies a glowing effect to the player currently selectable according
     * to the selectableOptionsWrapper and enable actual selection by clicking.
     * @param players the players selectable during the current action
     */
    private void showPossiblePlayers(List<String> players) {
        selectedPlayers = new ArrayList<>();
        for (String p : players) {
            Shape playerShape = playersShapes.stream().filter(c->c.getUserData().equals(p)).findAny().orElse(null);
            GuiHelper.applyBorder(playerShape,30);
            playerShape.setOnMouseClicked(e->selectPlayer(e));
            tileBoard.toFront();
        }
    }


    /**
     * Adds a player to the currently selected player and change
     * the senderButton action to send the selected players.
     * If the selected players are enough it sends them automatically.
     * @param mouseEvent the mouseEvent registered by the shape representing the player
     */
    private void selectPlayer(MouseEvent mouseEvent){
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

    /**
     * Rotate and zoom weapon images on the left side of the map to make them
     * more readable by the user while the mouse is on the image.
     * @param mouseEntered event that trigger this method
     */
    @FXML
    public void rotateRightAndZoomImage(MouseEvent mouseEntered){
        ImageView zoomable = (ImageView)mouseEntered.getSource();
        zoomable.setViewOrder(-10);
        zoomable.setRotate(90);
        zoomable.setScaleX(1.5);
        zoomable.setScaleY(1.5);
        zoomable.setTranslateX(50);
    }

    /**
     * Rotate and zoom weapon images on the right side of the map to make them
     * more readable by the user while the mouse is on the image.
     * @param mouseEntered event that triggers this method
     */
    @FXML
    public void rotateLeftAndZoomImage(MouseEvent mouseEntered){
        ImageView zoomable = (ImageView)mouseEntered.getSource();
        zoomable.setViewOrder(-10);
        zoomable.setRotate(-90);
        zoomable.setScaleX(1.5);
        zoomable.setScaleY(1.5);
        zoomable.setTranslateX(-50);
    }

    /**
     * Zoom weapon images on the top of the map to make them
     * more readable by the user while the mouse is on the image.
     * @param mouseEntered event that trigger this method
     */
    @FXML
    public void zoomImage(MouseEvent mouseEntered){
        ImageView zoomable = (ImageView)mouseEntered.getSource();
        zoomable.setViewOrder(-10);
        zoomable.setScaleX(1.5);
        zoomable.setScaleY(1.5);
        zoomable.setTranslateY(50);
    }

    /**
     * Bring back the weapon images to their original state
     * after the mouse exit from the image.
     * @param mouseExited event that trigger this method
     */
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

    /**
     * Gets the coordinates of the click on the selection GridPane,
     * creates a new ViewTileCoords and add it to the selected coords,
     * or remove it if its already selected.
     * @param event the mouseClicked event of the selected tile
     */

    @FXML
    private void selectTile(MouseEvent event){
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

    /**
     * Show selectable tiles to the user by making visible the corresponding tile
     * on the selectionPane.
     * @param tileCoords list of selectable ViewTileCoords
     */
    void showPossibleTiles(List<ViewTileCoords> tileCoords){
        selectedCoords = new ArrayList<>();
        for(ViewTileCoords t: tileCoords){
            Rectangle selectionRectangle = (Rectangle)GuiHelper.getNodeByIndex(selectionBoard,t.getPosx(),t.getPosy());
            selectionRectangle.setOpacity(0.50);
            selectionRectangle.setOnMouseClicked(e->selectTile(e));
        }
        selectionBoard.toFront();
    }

    /**
     * Show selectable rooms to the user by making visible each tile that belongs
     * to that room on the selectionPane.
     * @param rooms list of selectable rooms
     */
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

    /**
     * Creates a dialog with the possible directions that the user can
     * select and sends the selected one to the server.
     * @param directions a list of the selectable directions
     */
    void showPossibleDirections(List<String> directions) {
        Map<String, String> arrows = Map.ofEntries(
                entry("NORTH", " ↑"),
                entry("SOUTH", " ↓"),
                entry("EAST", " →"),
                entry("WEST", " ←")
        );
        if(choiceDialog == null || !choiceDialog.isShowing()) {
            choiceDialog = new ChoiceDialog<>(null, directions.stream().map(d -> d+arrows.get(d)).collect(Collectors.toList()));
            Optional<String> choice = choiceDialog.showAndWait();
            choice.ifPresent(c -> eventUpdater.sendDirection(c.split(" ")[0]));
        }
    }

    /**
     * Selects the room corresponding to the mouse click and send it
     * to the server.
     * @param mouseEvent mouse click on the selectionPane
     */
    private void selectRoom(MouseEvent mouseEvent){
        Rectangle rectangle = (Rectangle)mouseEvent.getSource();
        String room = viewBoard.getTiles().get(GridPane.getRowIndex(rectangle)).get(GridPane.getColumnIndex(rectangle)).getRoom();
        eventUpdater.sendRoom(room);
    }

    /**
     * Sets the selectableOptionsWrapper and displays the possible
     * options to the user
     * @param selectableOptionsWrapper an updated selectableOptionsWrapper
     */
    void setSelectableOptionsWrapper(SelectableOptionsWrapper selectableOptionsWrapper){
        this.selectableOptionsWrapper = selectableOptionsWrapper;
        if(selectableOptionsWrapper.getAcceptedTypes().contains(ReceivingType.WEAPON))
            showPossibleWeapons();
        if(selectableOptionsWrapper.getAcceptedTypes().contains(ReceivingType.PLAYERS))
            showPossiblePlayers(selectableOptionsWrapper.getSelectablePlayers().getOptions());
    }

    /**
     * Initialize the eventUpdater used to communicate with the server
     * @param eventUpdater the eventUpdater to communicate with the server
     */
    public void setEventUpdater(EventUpdater eventUpdater){
        this.eventUpdater = eventUpdater;
    }

    /**
     * Applies a glow to the selectable weapons in the spawn point and
     * associates them with the corresponding selection function
     */
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

    /**
     * Finds the correct weapon from the mouse click and
     * send it to the server
     * @param mouseEvent mouse click on the weapon ImageView
     */
    private void selectWeaponRed(MouseEvent mouseEvent){
        ImageView imageView = (ImageView)mouseEvent.getSource();
        if(imageView.getEffect() != null) {
            int weaponIndex = redWeaponsBox.getChildren().indexOf((ImageView) mouseEvent.getSource());
            eventUpdater.sendWeapon(redWeapons.get(weaponIndex));
        }
    }

    /**
     * Finds the correct weapon from the mouse click and
     * send it to the server
     * @param mouseEvent mouse click on the weapon ImageView
     */
    private void selectWeaponBlue(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        if (imageView.getEffect() != null){
            int weaponIndex = blueWeaponsBox.getChildren().indexOf((ImageView) mouseEvent.getSource());
            eventUpdater.sendWeapon(blueWeapons.get(weaponIndex));
        }
    }

    /**
     * Finds the correct weapon from the mouse click and
     * send it to the server
     * @param mouseEvent mouse click on the weapon ImageView
     */
    private void selectWeaponYellow(MouseEvent mouseEvent){
        ImageView imageView = (ImageView)mouseEvent.getSource();
        if(imageView.getEffect() != null){
            int weaponIndex = yellowWeaponsBox.getChildren().indexOf((ImageView)mouseEvent.getSource());
            eventUpdater.sendWeapon(yellowWeapons.get(weaponIndex));
        }
    }


    /**
     * Displays the remaining skulls on the board and
     * the drops from the killshot track
     */
    private void updateSkulls(){
        for(int i = 0; i < killshotDamageBox.getChildren().size() - viewBoard.getSkulls(); i++) {
            VBox sameShot = (VBox) killshotDamageBox.getChildren().get(i);
            sameShot.getChildren().clear();
        }
        for(int i = 0; i < killshotDamageBox.getChildren().size() - viewBoard.getSkulls(); i++){
            for(int j = 0; j < 2; j++){
                VBox sameShot = (VBox)killshotDamageBox.getChildren().get(i);
                ImageView dropView = new ImageView(drop);
                dropView.setFitWidth(23);
                dropView.setFitHeight(32);
                if(viewBoard.getKillShotTrack().get(2*i + j) != null) {
                    ColorAdjust colorAdjust = new ColorAdjust();
                    GuiHelper.hueShifter(viewBoard.getKillShotTrack().get(2*i + j),colorAdjust);
                    dropView.setEffect(colorAdjust);
                    sameShot.getChildren().addAll(dropView);
                }
            }
        }
    }

}
