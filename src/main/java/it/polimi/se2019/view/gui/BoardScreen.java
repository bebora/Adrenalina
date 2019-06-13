package it.polimi.se2019.view.gui;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.view.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
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
    VBox playerBoardZone;
    SelectableOptionsWrapper selectableOptionsWrapper;

    public BoardScreen(View GUIView){
        boardFX = new BoardFX();
        boardFX.setEventUpdater(GUIView.getEventUpdater());
        playerBoardZone = new VBox();
        VBox boardZone = new VBox();
        boardZone.setSpacing(20);
        actionButtons = new ActionButtons(GUIView.getEventUpdater());
        Label powerUpsLabel = new Label("PowerUps:");
        powerUpsBox = new PowerUpsBox(GUIView.getEventUpdater());
        boardZone.getChildren().addAll(boardFX,actionButtons);
        clientPlayer = new PlayerBoardFX();
        for(ViewPlayer p: GUIView.getPlayers()){
            PlayerBoardFX temp = new PlayerBoardFX();
            temp.updatePlayerInfo(p);
            playerBoardZone.getChildren().addAll(temp);
        }
        playerBoardZone.getChildren().addAll(powerUpsLabel,powerUpsBox);
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
    }

    public void updateBoard(ViewBoard viewBoard,List<ViewPlayer> players){
        boardFX.setYellowWeapons(getWeaponsFromColor("YELLOW",viewBoard));
        boardFX.setBlueWeapons(getWeaponsFromColor("BLUE",viewBoard));
        boardFX.setRedWeapons(getWeaponsFromColor("RED",viewBoard));
        boardFX.clearPlayers();
        int i = 0;
        for(ViewPlayer p: players){
            if(p.getTile()!=null)
                boardFX.drawPlayer(p);
            PlayerBoardFX playerBoardFX = (PlayerBoardFX)playerBoardZone.getChildren().get(i);
            playerBoardFX.updatePlayerInfo(p);
            i++;
        }
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
                    break;
                default:
                    break;
            }
        }
    }

    public List<String> getWeaponsFromColor(String color, ViewBoard viewBoard){
        return viewBoard.getTiles().stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(ViewTile::isSpawn)
                .filter(t->t.getRoom().equals(color))
                .map(ViewTile::getWeapons)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void updatePowerUps(List<ViewPowerUp> viewPowerUps){
        powerUpsBox.setPowerUps(viewPowerUps);
    }

}
