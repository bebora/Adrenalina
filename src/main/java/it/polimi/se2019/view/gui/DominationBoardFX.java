package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.ViewPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.util.List;

/**
 * Represents a domination board used in domination mode.
 * It shows the damages of the spawn point and the skulls
 * that are still on the board.
 */
class DominationBoardFX extends Pane {
    @FXML private TilePane skullsPane;
    @FXML private HBox blueSpawnBox;
    @FXML private HBox redSpawnBox;
    @FXML private HBox yellowSpawnBox;
    @FXML private HBox excessBlueDamageBox;
    @FXML private HBox excessYellowDamageBox;
    @FXML private HBox excessRedDamageBox;
    @FXML private ImageView alternateFrenzyTrigger;
    private Image drop;

    /**
     * Creates a new DominationBoardFX by loading the corresponding
     * fxml file and assigning this as root.
     */
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
        drop = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/red_damage.png"
        ));
    }

    /**
     * Updates the skulls displayed on the domination board
     * @param skulls the number of skulls remaining on the board
     */
    void updateSkulls(int skulls){
        for(int i = 0; i < skullsPane.getChildren().size() -skulls ; i++)
            skullsPane.getChildren().get(i).setVisible(false);
    }

    /**
     * Updates the damages on each of the spawns
     * @param players a list of SpawnPlayers representing the spawns
     */
    void updateSpawns(List<ViewPlayer> players){
        int overDamagedSpawns = 0;
        for(ViewPlayer s: players){
            switch(s.getColor()){
                case "RED":
                    updateDamages(redSpawnBox,s.getDamages());
                    updateExcessDamage(excessRedDamageBox,s.getDamages());
                    break;
                case "YELLOW":
                    updateDamages(yellowSpawnBox,s.getDamages());
                    updateExcessDamage(excessYellowDamageBox,s.getDamages());
                    break;
                case "BLUE":
                    updateDamages(blueSpawnBox,s.getDamages());
                    updateExcessDamage(excessBlueDamageBox,s.getDamages());
                    break;
                default:
                    break;
            }
            if(s.getDamages().size() >= 8)
                overDamagedSpawns += 1;
        }
        if(overDamagedSpawns >= 2)
            alternateFrenzyTrigger.setVisible(false);
    }

    /**
     *Updates the damages displayed on a spawnBox.
     * The first 8 damages are already present and are just made
     * visible.
     * @param spawnBox the HBox that displays the damages
     * @param damages the damages of the corresponding spawn
     */
    private void updateDamages(HBox spawnBox, List<String> damages){
        spawnBox.getChildren().forEach(c->c.setVisible(false));
        for(int i = 0; i < damages.size() && i < spawnBox.getChildren().size(); i++){
            ColorAdjust colorAdjust = new ColorAdjust();
            GuiHelper.hueShifter(damages.get(i),colorAdjust);
            spawnBox.getChildren().get(i).setEffect(colorAdjust);
            spawnBox.getChildren().get(i).setVisible(true);
        }
    }

    /**
     * Displays the damages after the 8th damage.
     * The damages are created dynamically since they can be technically
     * infinite.
     * @param excessDamageBox the HBox used to display the damages
     * @param damages a list of the damages on the corresponding spawn
     */
    private void updateExcessDamage(HBox excessDamageBox, List<String> damages) {
        excessDamageBox.getChildren().clear();
        for (int i = 8; i < damages.size(); i++) {
            ImageView imageView = new ImageView(drop);
            imageView.setFitWidth(16);
            imageView.setFitHeight(26);
            ColorAdjust colorAdjust = new ColorAdjust();
            GuiHelper.hueShifter(damages.get(i), colorAdjust);
            imageView.setEffect(colorAdjust);
            excessDamageBox.getChildren().addAll(imageView);
        }
    }

}
