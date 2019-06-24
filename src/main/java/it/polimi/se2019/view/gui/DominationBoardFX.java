package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.ViewPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.util.List;

public class DominationBoardFX extends Pane {
    @FXML TilePane skullsPane;
    @FXML HBox blueSpawnBox;
    @FXML HBox redSpawnBox;
    @FXML HBox yellowSpawnBox;

    DominationBoardFX() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "fxml/DominationBoardFX.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            Logger.log(Priority.DEBUG, exception.getMessage());
        }
    }

    void updateSkulls(int skulls){
        skullsPane.getChildren().forEach(n->n.setVisible(false));
        for(int i =0; i < skulls; i++)
            skullsPane.getChildren().get(i).setVisible(true);
    }

    void updateSpawns(List<ViewPlayer> players){
        for(ViewPlayer s: players){
            switch(s.getColor()){
                case "RED":
                    updateDamages(redSpawnBox,s.getDamages());
                    break;
                case "YELLOW":
                    updateDamages(yellowSpawnBox,s.getDamages());
                    break;
                case "BLUE":
                    updateDamages(blueSpawnBox,s.getDamages());
                    break;
                default:
                    break;
            }
        }
    }

    private void updateDamages(HBox spawnBox, List<String> damages){
        spawnBox.getChildren().forEach(c->c.setVisible(false));
        for(int i = 0; i < damages.size() && i < spawnBox.getChildren().size(); i++){
            ColorAdjust colorAdjust = new ColorAdjust();
            GuiHelper.hueShifter(damages.get(i),colorAdjust);
            spawnBox.getChildren().get(i).setEffect(colorAdjust);
            spawnBox.getChildren().get(i).setVisible(true);
        }
    }

}
