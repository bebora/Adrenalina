package it.polimi.se2019.view.gui;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.view.*;
import javafx.collections.FXCollections;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represent the main screen visualized during the game
 * and allow communication between the view and the various graphical
 * elements that compose this screen.
 */
class BoardScreen extends HBox {
    private BoardFX boardFX;
    private PlayerBoardFX clientPlayer;
    private ActionButtons actionButtons;
    private PowerUpsBox powerUpsBox;
    private WeaponsBox weaponsBox;
    private VBox playerBoardZone;
    private VBox playerBoardBox;
    private DominationBoardFX dominationBoardFX;
    private ScrollPane playerBoardScroller;
    private SelectableOptionsWrapper selectableOptionsWrapper;
    private View guiView;
    private ListView<String> messageBox;
    private ChoiceDialog<String> ammoChoice;
    private Text points;

    /**
     * Creates a BoardScreen with the elements that the user of the view
     * can visualize. Uses BoardFX to represent the board and
     * PlayerBoardFX to represent the players boards.
     * The elements are placed dynamically and the result is
     * scaled to fit the whole screen.
     * @param GUIView the view containing all the information about the player
     */
    BoardScreen(View GUIView){
        guiView = GUIView;
        boardFX = new BoardFX();
        boardFX.setEventUpdater(GUIView.getEventUpdater());
        boardFX.setBoard(GUIView.getBoard());
        playerBoardZone = new VBox();
        playerBoardBox = new VBox();
        playerBoardScroller = new ScrollPane();
        playerBoardScroller.setPannable(true);
        playerBoardScroller.setPrefSize(492,240);
        playerBoardScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playerBoardScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox boardZone = new VBox();
        boardZone.setSpacing(2);
        actionButtons = new ActionButtons(GUIView.getEventUpdater());
        powerUpsBox = new PowerUpsBox(GUIView.getEventUpdater());
        boardFX.setSenderButton(actionButtons.getSenderButton());
        weaponsBox = new WeaponsBox(GUIView.getEventUpdater());
        messageBox = new ListView<>();
        messageBox.setMinHeight(130);
        messageBox.setPrefSize(200,130);
        ScrollPane messageScroll = new ScrollPane(messageBox);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setPannable(true);
        messageScroll.setMinHeight(130);
        messageScroll.setMinViewportHeight(130);
        messageScroll.setFitToWidth(true);
        boardZone.getChildren().addAll(boardFX,actionButtons,messageScroll);
        clientPlayer = new PlayerBoardFX(guiView.getSelf());
        //Set points shadow and style
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(1.0);
        dropShadow.setOffsetY(1.0);
        dropShadow.setColor(Color.BLUE);
        String family = "sans-serif";
        double size = 25;
        points = new Text();
        points.setFont(Font.font(family, size));
        //Player starts with 0 points
        points.setText("You have 0 points");
        points.setFill(Color.WHITE);
        points.setStrokeWidth(0.5);
        points.setStroke(Color.WHITE);
        points.setEffect(dropShadow);
        for(ViewPlayer p: GUIView.getPlayers()) {
            if (!p.getDominationSpawn() && !p.getUsername().equals(GUIView.getSelf().getUsername())) {
                PlayerBoardFX temp = new PlayerBoardFX(p);
                temp.updatePlayerInfo(p);
                playerBoardBox.getChildren().addAll(temp);
            }
        }
        playerBoardScroller.setContent(playerBoardBox);
        if(GUIView.getGameMode().equals("DOMINATION")) {
            dominationBoardFX = new DominationBoardFX();
            boardFX.setDominationPane(dominationBoardFX);
        }
        playerBoardZone.getChildren().addAll(clientPlayer, playerBoardScroller, points , powerUpsBox, weaponsBox);
        playerBoardZone.setSpacing(15);
        updateBoard(GUIView.getBoard(),GUIView.getPlayers());
        this.getChildren().addAll(boardZone,playerBoardZone);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        Scale scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        scale.setX(primaryScreenBounds.getMaxX()/(boardFX.getPrefWidth() + clientPlayer.getPrefWidth()));
        scale.setY(primaryScreenBounds.getMaxY()/(boardFX.getPrefHeight() + 154));
        this.getTransforms().addAll(scale);
        this.setStyle("-fx-background-color: black");
    }

    /**
     * Updates all the PlayerBoardFXs and the BoardFX after a total update.
     * @param viewBoard the updated board received in the totalUpdate
     * @param players the players of the match received in the totalupdate
     */
    void updateBoard(ViewBoard viewBoard,List<ViewPlayer> players){
        boardFX.updateBoard(viewBoard);
        boardFX.clearPlayers();
        boardFX.drawPlayers(players);
        boardFX.drawAmmoCard();
        boardFX.setYellowWeapons(getWeaponsFromColor("YELLOW",viewBoard));
        boardFX.setBlueWeapons(getWeaponsFromColor("BLUE",viewBoard));
        boardFX.setRedWeapons(getWeaponsFromColor("RED",viewBoard));
        int i = 0;
        for(ViewPlayer p: players){
            if (updatePlayer(p,i))
                i++;
        }
        List<ViewPlayer> dominationPlayers = players.stream()
                .filter(ViewPlayer::getDominationSpawn)
                .collect(Collectors.toList());
        if(guiView.getGameMode().equals("DOMINATION")) {
            dominationBoardFX.updateSkulls(viewBoard.getSkulls());
            dominationBoardFX.updateSpawns(dominationPlayers);
        }
    }

    /**
     * Updates a playerBoardFX after a totalUpdate.
     * @param p the player corresponding to the playerBoard
     * @param i the index of the player in the match's player list
     * @return <code>true</code> if the player is not a spawn and not the user
     *         <code>false</code> otherwise
     */
    private boolean updatePlayer(ViewPlayer p,int i){
        if(!p.getDominationSpawn()){
            if(!p.getUsername().equals(guiView.getSelf().getUsername())){
                PlayerBoardFX playerBoardFX = (PlayerBoardFX)playerBoardBox.getChildren().get(i);
                playerBoardFX.updatePlayerInfo(p);
                if(p == guiView.getCurrentPlayer())
                    GuiHelper.applyBorder(playerBoardFX,50);
                else
                    playerBoardFX.setEffect(null);
                return true;
            }else {
                clientPlayer.updatePlayerInfo(p);
                if(p == guiView.getCurrentPlayer())
                    GuiHelper.applyBorder(clientPlayer,50);
                else
                    clientPlayer.setEffect(null);
            }
        }
        return false;
    }

    /**
     * Sets the SelectableOptionsWrapper and displays to the player
     * the possible options that can be selected.
     * @param selectableOptionsWrapper a SelectableOptionsWrapper containing the current selectable options
     */
    void setSelectableOptionsWrapper(SelectableOptionsWrapper selectableOptionsWrapper) {
        this.selectableOptionsWrapper = selectableOptionsWrapper;
        actionButtons.clearPossibleActions();
        for(ReceivingType ac: selectableOptionsWrapper.getAcceptedTypes()){
            switch (ac){
                case ACTION:
                    actionButtons.setPossibleActions(selectableOptionsWrapper.getSelectableActions().getOptions());
                    break;
                case TILES:
                    boardFX.setSelectableOptionsWrapper(selectableOptionsWrapper);
                    boardFX.showPossibleTiles(selectableOptionsWrapper.getSelectableTileCoords().getOptions());
                    break;
                case POWERUP:
                    powerUpsBox.highlightSelectablePowerUps(selectableOptionsWrapper.getSelectablePowerUps());
                    if(selectableOptionsWrapper.getSelectablePowerUps().getMinSelectables() == 0)
                        actionButtons.getSenderButton().setPowerUps(new ArrayList<>());
                    break;
                case WEAPON:
                    boardFX.setSelectableOptionsWrapper(selectableOptionsWrapper);
                    weaponsBox.highlightSelectableWeapons(selectableOptionsWrapper.getSelectableWeapons());
                    break;
                case PLAYERS:
                    boardFX.setSelectableOptionsWrapper(selectableOptionsWrapper);
                    if(selectableOptionsWrapper.getSelectablePlayers().getMinSelectables() == 0)
                        actionButtons.getSenderButton().setPlayers(new ArrayList<>());
                    break;
                case AMMO:
                    showAmmoChoice(selectableOptionsWrapper.getSelectableAmmos().getOptions());
                    break;
                case ROOM:
                    boardFX.showPossibleRooms(selectableOptionsWrapper.getSelectableRooms().getOptions());
                    break;
                case DIRECTION:
                    boardFX.showPossibleDirections(selectableOptionsWrapper.getSelectableDirections().getOptions());
                    break;
                case EFFECT:
                    weaponsBox.setSelectableEffects(selectableOptionsWrapper.getSelectableEffects());
                    break;
                case STOP:
                    actionButtons.enableStop();
                    break;
                default:
                    break;
            }
        }
        actionButtons.getInfo().setOnMouseClicked(e->InfoAlert.handleAlert(formatSelectableOptions()));
        actionButtons.getInfo().setDisable(false);
    }

    /**
     * Returns a list of the weapons belonging to a spawn point
     * of a specific color.
     * @param color the color of the spawn point to get the weapon from
     * @param viewBoard the board containing the spawn point
     * @return a list of the weapons in the spawnpoint
     */

    private List<String> getWeaponsFromColor(String color, ViewBoard viewBoard){
        return viewBoard.getTiles().stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(ViewTile::isSpawn)
                .filter(t->t.getRoom().equals(color))
                .map(ViewTile::getWeapons)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Updates the ImageViews of the powerUps
     * in the powerUpsBox after a totalUpdate.
     * @param viewPowerUps a list of ViewPowerups that the user owns
     */
    void updatePowerUps(List<ViewPowerUp> viewPowerUps){
        powerUpsBox.setPowerUps(viewPowerUps);
    }

    /**
     * Updates the ImageViews of the weapons in the weaponsBox
     * after a totalUpdate
     * @param weapons the ViewWeapons that the user currently owns
     */
    void updateWeapons(List<ViewWeapon> weapons){
        weaponsBox.setWeapons(weapons);
    }

    /**
     * Updates the messages in the messages ListView
     * after receiving a new message
     * @param messages the new messages that must be displayed to the player
     */
    void updateMessages(List<String> messages){
        messageBox.setItems(FXCollections.observableList(messages));
    }

    /**
     * Updates the TextField that displays the points that the user
     * currently have
     * @param pts the point that the user currently haves
     */
    void updatePoints(int pts) {
        points.setText(String.format(" You have %s points", pts));
    }

    /**
     * Displays a dialog with the possible ammo that the
     * user cas choose
     * @param ammoChoiceOptions a list of the possible ammo that the user can choose
     */
    private void showAmmoChoice(List<String> ammoChoiceOptions){
        if(ammoChoice == null || !ammoChoice.isShowing()) {
            ammoChoice = new ChoiceDialog<>(null, ammoChoiceOptions);
            ammoChoice.setContentText("Selectable ammos:");
            ammoChoice.setHeaderText("Select an ammo. If you prefer paying with a powerUp close this message and click on it:");
            Optional<String> ammo = ammoChoice.showAndWait();
            ammo.ifPresent(a -> {
                guiView.getEventUpdater().sendAmmo(a);
                ammoChoice = null;
            });
        }
    }


    /**
     * Transforms the SelectableOptions into a string that can be displayed
     * to the user through a dialog.
     * @return a String containing all the SelectableOptions informations
     */
    private String formatSelectableOptions(){
        String result = "";
        for(ReceivingType r: selectableOptionsWrapper.getAcceptedTypes()){
            result = result.concat(r.name() + "\n");
            switch (r){
                case POWERUP:
                    for(ViewPowerUp w: selectableOptionsWrapper.getSelectablePowerUps().getOptions())
                        result = result.concat(w.getName() + " ");
                    result = result.concat("\n");
                    result = result.concat(selectableOptionsWrapper.getSelectablePowerUps().getPrompt() + "\n");
                    result = result.concat(selectableOptionsWrapper.getSelectablePowerUps().getNumericalCostraints() + "\n");
                    break;
                case TILES:
                    for(ViewTileCoords v: selectableOptionsWrapper.getSelectableTileCoords().getOptions())
                        result = result.concat(v.toString() + " ");
                    result = result.concat("\n");
                    result = result.concat(selectableOptionsWrapper.getSelectableTileCoords().getPrompt() + "\n");
                    result = result.concat(selectableOptionsWrapper.getSelectableTileCoords().getNumericalCostraints() + "\n");
                    break;
                case STOP:
                    result = result.concat(selectableOptionsWrapper.getStopPrompt());
                    break;
                default:
                    for(String s: selectableOptionsWrapper.getSelectableStringOptions(r).getOptions())
                        result = result.concat(s + " ");
                    result = result.concat("\n");
                    result = result.concat(selectableOptionsWrapper.getSelectableStringOptions(r).getPrompt() + "\n");
                    result = result.concat(selectableOptionsWrapper.getSelectableStringOptions(r).getNumericalCostraints() + "\n");
                    break;
            }
        }
        return result;
    }
}
