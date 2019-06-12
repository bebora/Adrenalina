package it.polimi.se2019.view.gui;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.view.*;
import javafx.geometry.Rectangle2D;
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
    SelectableOptionsWrapper selectableOptionsWrapper;

    public BoardScreen(View GUIView){
        boardFX = new BoardFX();
        VBox playerBoardZone = new VBox();
        VBox boardZone = new VBox();
        boardZone.setSpacing(20);
        actionButtons = new ActionButtons(GUIView.getEventUpdater());
        boardZone.getChildren().addAll(boardFX,actionButtons);
        clientPlayer = new PlayerBoardFX();
        clientPlayer.updateImage(GUIView.getSelf().getColor());
        playerBoardZone.getChildren().addAll(clientPlayer);
        for(ViewPlayer p: GUIView.getPlayers()){
            if(p != GUIView.getSelf()) {
                PlayerBoardFX temp = new PlayerBoardFX();
                temp.updateImage(p.getColor());
                playerBoardZone.getChildren().addAll(temp);
            }
        }
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
        boardFX.setBlueWeapons(getWeaponsFromColor("RED",viewBoard));
        boardFX.setRedWeapons(getWeaponsFromColor("RED",viewBoard));
        boardFX.clearPlayers();
        for(ViewPlayer p: players){
            if(p.getTile()!=null)
                boardFX.drawPlayer(p);
        }

    }

    public void setSelectableOptionsWrapper(SelectableOptionsWrapper selectableOptionsWrapper) {
        this.selectableOptionsWrapper = selectableOptionsWrapper;
        actionButtons.clearPossibleActions();
        for(ReceivingType ac: selectableOptionsWrapper.getAcceptedTypes()){
            switch (ac.name()){
                case "ACTION":
                    actionButtons.setPossibleActions(selectableOptionsWrapper.getSelectableActions().getOptions());
                    break;
                case "TILES":
                    boardFX.showPossibleTiles(selectableOptionsWrapper.getSelectableTileCoords().getOptions());
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

}
