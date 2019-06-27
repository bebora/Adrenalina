package it.polimi.se2019.view.gui;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.view.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class BoardScreen extends HBox {
    BoardFX boardFX;
    PlayerBoardFX clientPlayer;
    ActionButtons actionButtons;
    PowerUpsBox powerUpsBox;
    WeaponsBox weaponsBox;
    VBox playerBoardZone;
    VBox playerBoardBox;
    DominationBoardFX dominationBoardFX;
    ScrollPane playerBoardScroller;
    SelectableOptionsWrapper selectableOptionsWrapper;
    View guiView;

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
        boardZone.setSpacing(20);
        actionButtons = new ActionButtons(GUIView.getEventUpdater());
        powerUpsBox = new PowerUpsBox(GUIView.getEventUpdater(),actionButtons.getSenderButton());
        boardFX.setSenderButton(actionButtons.getSenderButton());
        weaponsBox = new WeaponsBox(GUIView.getEventUpdater());
        boardZone.getChildren().addAll(boardFX,actionButtons);
        clientPlayer = new PlayerBoardFX();
        for(ViewPlayer p: GUIView.getPlayers()) {
            if (!p.getDominationSpawn() && !p.getUsername().equals(GUIView.getSelf().getUsername())) {
                PlayerBoardFX temp = new PlayerBoardFX();
                temp.updatePlayerInfo(p);
                playerBoardBox.getChildren().addAll(temp);
            }
        }
        playerBoardScroller.setContent(playerBoardBox);
        if(GUIView.getGameMode().equals("DOMINATION")) {
            dominationBoardFX = new DominationBoardFX();
            boardFX.setDominationPane(dominationBoardFX);
        }
        playerBoardZone.getChildren().addAll(clientPlayer,playerBoardScroller,powerUpsBox,weaponsBox);
        playerBoardZone.setSpacing(15);
        updateBoard(GUIView.getBoard(),GUIView.getPlayers());
        this.getChildren().addAll(boardZone,playerBoardZone);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        Scale scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        scale.setX(primaryScreenBounds.getMaxX()/(boardFX.getPrefWidth() + clientPlayer.getPrefWidth()));
        scale.setY(primaryScreenBounds.getMaxY()/(boardFX.getPrefHeight() + clientPlayer.getPrefHeight()));
        this.getTransforms().addAll(scale);
        this.setStyle("-fx-background-color: black");
    }

    void updateBoard(ViewBoard viewBoard,List<ViewPlayer> players){
        boardFX.clearPlayers();
        boardFX.drawPlayers(players);
        boardFX.drawAmmoCard();
        boardFX.setYellowWeapons(getWeaponsFromColor("YELLOW",viewBoard));
        boardFX.setBlueWeapons(getWeaponsFromColor("BLUE",viewBoard));
        boardFX.setRedWeapons(getWeaponsFromColor("RED",viewBoard));
        int i = 0;
        for(ViewPlayer p: players){
            if(!p.getDominationSpawn()){
                if(!p.getUsername().equals(guiView.getSelf().getUsername())){
                    PlayerBoardFX playerBoardFX = (PlayerBoardFX)playerBoardBox.getChildren().get(i);
                    playerBoardFX.updatePlayerInfo(p);
                    if(p == guiView.getCurrentPlayer())
                        GuiHelper.applyBorder(playerBoardFX,50);
                    else
                        playerBoardFX.setEffect(null);
                    i++;
                }else {
                    clientPlayer.updatePlayerInfo(p);
                    if(p == guiView.getCurrentPlayer())
                        GuiHelper.applyBorder(clientPlayer,50);
                    else
                        clientPlayer.setEffect(null);
                }
            }
        }
        List<ViewPlayer> dominationPlayers = players.stream()
                .filter(ViewPlayer::getDominationSpawn)
                .collect(Collectors.toList());
        if(guiView.getGameMode().equals("DOMINATION")) {
            dominationBoardFX.updateSkulls(viewBoard.getSkulls());
            dominationBoardFX.updateSpawns(dominationPlayers);
        }
    }

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
                    break;
                case WEAPON:
                    boardFX.setSelectableOptionsWrapper(selectableOptionsWrapper);
                    weaponsBox.highlightSelectableWeapons(selectableOptionsWrapper.getSelectableWeapons());
                    break;
                case PLAYERS:
                    boardFX.setSelectableOptionsWrapper(selectableOptionsWrapper);
                    break;
                case ROOM:
                    boardFX.showPossibleRooms(selectableOptionsWrapper.getSelectableRooms().getOptions());
                    break;
                case DIRECTION:
                    boardFX.showPossibleDirections(Arrays.asList("NORTH", "SOUTH", "WEST", "EAST"));
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
    }

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

    void updatePowerUps(List<ViewPowerUp> viewPowerUps){
        powerUpsBox.setPowerUps(viewPowerUps);
    }

    void updateWeapons(List<ViewWeapon> weapons){
        List<String> weaponsNames = weapons.stream().map(ViewWeapon::getName).collect(Collectors.toList());
        weaponsBox.setWeapons(weaponsNames);
    }



}
