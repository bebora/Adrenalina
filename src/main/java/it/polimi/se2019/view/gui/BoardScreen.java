package it.polimi.se2019.view.gui;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.view.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
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

    public BoardScreen(View GUIView){
        boardFX = new BoardFX();
        boardFX.setEventUpdater(GUIView.getEventUpdater());
        playerBoardZone = new VBox();
        playerBoardBox = new VBox();
        playerBoardScroller = new ScrollPane();
        playerBoardScroller.setPannable(true);
        playerBoardScroller.setPrefSize(984,240);
        playerBoardScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playerBoardScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox boardZone = new VBox();
        boardZone.setSpacing(20);
        actionButtons = new ActionButtons(GUIView.getEventUpdater());
        powerUpsBox = new PowerUpsBox(GUIView.getEventUpdater());
        weaponsBox = new WeaponsBox(GUIView.getEventUpdater());
        boardZone.getChildren().addAll(boardFX,actionButtons);
        clientPlayer = new PlayerBoardFX();
        for(ViewPlayer p: GUIView.getPlayers()){
            if(p.getDominationSpawn() != null) {
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
        playerBoardZone.getChildren().addAll(playerBoardScroller,powerUpsBox,weaponsBox);
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

    public void updateBoard(ViewBoard viewBoard,List<ViewPlayer> players){
        boardFX.setYellowWeapons(getWeaponsFromColor("YELLOW",viewBoard));
        boardFX.setBlueWeapons(getWeaponsFromColor("BLUE",viewBoard));
        boardFX.setRedWeapons(getWeaponsFromColor("RED",viewBoard));
        boardFX.clearPlayers();
        dominationBoardFX.updateSkulls(viewBoard.getSkulls());
        int i = 0;
        for(ViewPlayer p: players){
            if(!p.getDominationSpawn()){
                if(p.getTile()!=null)
                    boardFX.drawPlayer(p);
                PlayerBoardFX playerBoardFX = (PlayerBoardFX)playerBoardBox.getChildren().get(i);
                playerBoardFX.updatePlayerInfo(p);
                i++;
            }
        }
        List<ViewPlayer> dominationPlayers = players.stream()
                .filter(ViewPlayer::getDominationSpawn)
                .collect(Collectors.toList());
        dominationBoardFX.updateSpawns(dominationPlayers);
    }

    public void setSelectableOptionsWrapper(SelectableOptionsWrapper selectableOptionsWrapper) {
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
