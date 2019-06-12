package it.polimi.se2019.view.gui;
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

    public BoardScreen(View GUIView){
        boardFX = new BoardFX();
        VBox playerBoardZone = new VBox();
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
        updateBoard(GUIView.getBoard());
        this.getChildren().add(boardFX);
        this.getChildren().add(playerBoardZone);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        Scale scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        scale.setX(primaryScreenBounds.getMaxX()/(boardFX.getPrefWidth() + clientPlayer.getPrefWidth()));
        scale.setY(primaryScreenBounds.getMaxY()/(boardFX.getPrefHeight() + clientPlayer.getPrefHeight()));
        this.getTransforms().addAll(scale);
    }

    public void updateBoard(ViewBoard viewBoard){
        boardFX.setYellowWeapons(getWeaponsFromColor("YELLOW",viewBoard));
        boardFX.setBlueWeapons(getWeaponsFromColor("BLUE",viewBoard));
        boardFX.setRedWeapons(getWeaponsFromColor("RED",viewBoard));

    }

    public List<String> getWeaponsFromColor(String color,ViewBoard viewBoard){
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
