package it.polimi.se2019.view.gui;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.BoardCreator;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.view.ViewPlayer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.stream.Collectors;


public class BoardScreen extends Application {
    @FXML
    BorderPane borderPane;

    //just for testing, yet to implement
    @Override
    public void start(Stage stage) throws Exception {
        BoardFX root = new BoardFX();
        Board testBoard = BoardCreator.parseBoard("board3.btlb",5);
        root.setBlueWeapons(null);
        root.setRedWeapons(null);
        root.setYellowWeapons(null);
        root.setBoard("B3");
        Player test = new Player("Paoletto");
        test.setColor(Color.RED);
        for(Tile t: testBoard.getTiles().stream().flatMap(Collection::stream).collect(Collectors.toList())) {
            if(t!=null) {
                test.setTile(t);
                root.drawPlayer(new ViewPlayer(test));
                root.drawPlayer(new ViewPlayer(test));
                root.drawPlayer(new ViewPlayer(test));
                root.drawPlayer(new ViewPlayer(test));
                root.drawPlayer(new ViewPlayer(test));
            }
        }

        Scene scene = new Scene(root);
        stage.setTitle("BoardScreen");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
