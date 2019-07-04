package it.polimi.se2019.view.gui;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.view.ViewPlayer;
import it.polimi.se2019.view.ViewWeapon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

/**
 * Represents a player board displaying all the player's ammunition,
 * damages, marks and the weapons that aren't loaded.
 * It also shows the player reward points.
 * It is not resizable by default since certain elements
 * have fixed position, so it needs external scaling.
 */
class PlayerBoardFX extends AnchorPane {
    @FXML private HBox damagesList;
    @FXML private ImageView playerBoardView;
    @FXML private TilePane ammoPane;
    @FXML private HBox marksBox;
    @FXML private Text username;
    @FXML private ImageView frenzyActions;
    @FXML private HBox skullBox;
    @FXML private HBox weaponBox;

    private Image boardImage;
    private Image skull;
    private ViewPlayer viewPlayer;
    private int originalRewardSize;

    /**
     * Creates a new PlayerBoardFX with the default player board
     * and sets the players username.
     * @param viewPlayer the ViewPlayer associated with this player board
     */
    PlayerBoardFX(ViewPlayer viewPlayer) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(
                "fxml/PlayerBoardFX.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            Logger.log(Priority.DEBUG, exception.getMessage());
        }
        this.username.setText(viewPlayer.getUsername());
        this.originalRewardSize = viewPlayer.getRewardPoints().size();

    }

    /**
     * Loads and shows the correct player board image and the image of the
     * frenzy actions to be displayed later.
     * @param color the color of the correct player board
     */
    private void updateImage(String color){
        boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/player_board/" + AssetMaps.colorToPlayerBoard.get(color)));
        Image frenzyActionImage = new Image(getClass().getClassLoader().getResourceAsStream(
                "assets/player_board/" + AssetMaps.colorToFrenzyActions.get(color)));
        skull = new Image(getClass().getClassLoader().getResourceAsStream("assets/skull.png"));
        playerBoardView.setImage(boardImage);
        frenzyActions.setImage(frenzyActionImage);
        frenzyActions.setVisible(false);
    }


    /**
     * Updates the damages displayed by the player board.
     * The damages ImageViews are already present on the
     * player board, they are just set visible and the color
     * is shifted to match the corresponding player color.
     * @param damages the list of the damages that the corresponding player currently has
     */
    private void displayDamages(List<String> damages) {
        int i = 0;
        for(Node n: damagesList.getChildren()){
           n.setOpacity(0.0);
        }
        for (String color : damages) {
            ImageView imageView = (ImageView)damagesList.getChildren().get(i);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0);
            imageView.effectProperty().setValue(colorAdjust);
            GuiHelper.hueShifter(color,colorAdjust);
            imageView.setOpacity(1.0);
            imageView.effectProperty().setValue(colorAdjust);
            i++;
        }
    }

    /**
     * Displays the ammo that the player currently has.
     * Ammo are represented as Rectangle.
     * @param ammos a list of the ammo owned by the player
     */
    private void displayAmmos(List<String> ammos){
        ammoPane.getChildren().clear();
        for(String a: ammos){
            Rectangle ammo = new Rectangle(10,10, Color.valueOf(a));
            ammoPane.getChildren().addAll(ammo);
        }
    }

    /**
     * Displays the marks that the player currently has.
     * The same mechanism used for displaying damages is applied here.
     * @param marks the marks that the player currently has
     */
    private void displayMarks(List<String> marks){
        marksBox.getChildren().forEach(e->e.setVisible(false));
        int i = 0;
        for (String color : marks) {
            ImageView imageView = (ImageView)marksBox.getChildren().get(i);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(0);
            imageView.effectProperty().setValue(colorAdjust);
            GuiHelper.hueShifter(color,colorAdjust);
            imageView.setVisible(true);
            imageView.effectProperty().setValue(colorAdjust);
            i++;

        }
    }

    /**
     * It updates all the players info.
     * @param viewPlayer the player represented in this player board
     */
    void updatePlayerInfo(ViewPlayer viewPlayer){
        this.viewPlayer = viewPlayer;
        updateImage(viewPlayer.getColor());
        displayDamages(viewPlayer.getDamages());
        displayAmmos(viewPlayer.getAmmos());
        displayMarks(viewPlayer.getMarks());
        triggerFrenzy();
        drawSkulls();
        drawWeapons();
    }

    /**
     * Shows the frenzy actions image if the player
     * has received enough damage.
     */
    private void triggerFrenzy(){
        if(viewPlayer.isFrenzyActions())
            frenzyActions.setVisible(true);
        if(viewPlayer.isFrenzyBoard()){
            boardImage = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/player_board/" + viewPlayer.getColor().toLowerCase() + "_player_board_back.png"));
            playerBoardView.setImage(boardImage);
            damagesList.setSpacing(11);
            damagesList.setPadding(new Insets(0,0,0,2));
            damagesList.setLayoutX(45);
            skullBox.setLayoutX(130);
            originalRewardSize = viewPlayer.getRewardPoints().size();
        }
    }

    /**
     * Draws the skulls over the rewards that have already been taken.
     */
    private void drawSkulls(){
        skullBox.getChildren().clear();
        for(int i = 0; i < originalRewardSize - viewPlayer.getRewardPoints().size(); i++){
            ImageView imageView = new ImageView(skull);
            imageView.setFitHeight(32);
            imageView.setFitWidth(23);
            skullBox.getChildren().addAll(imageView);
        }

    }

    /**
     * Draws the unloaded weapons that can be seen by all players
     * on the right side of the player board.
     */

    private void drawWeapons(){
        weaponBox.getChildren().clear();
        for(ViewWeapon w: viewPlayer.getUnloadedWeapons()){
            Image weapon = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/" + AssetMaps.weaponsAssetsMap.get(w.getName())
            ));
            ImageView weaponView = new ImageView(weapon);
            weaponView.setFitWidth(40);
            weaponView.setFitHeight(63);
            weaponBox.getChildren().addAll(weaponView);
        }
    }


}
