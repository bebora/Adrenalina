package it.polimi.se2019.view.gui;

import it.polimi.se2019.view.ViewPowerUp;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.stream.Collectors;

public class PowerUpsBox extends HBox {
    List<ViewPowerUp> powerUps;

    public void setPowerUps(List<ViewPowerUp> powerUps){
        System.out.println(powerUps.stream().map(ViewPowerUp::getName).collect(Collectors.joining()));
        this.powerUps = powerUps;
        this.getChildren().clear();
        for(ViewPowerUp w: powerUps){
            Image powerUp = new Image(getClass().getClassLoader().getResourceAsStream(
                    "assets/cards/"+
                    AssetMaps.powerUpsAssets.get(w.getName()+w.getDiscardAward())
            ));
            ImageView powerUpsView = new ImageView();
            powerUpsView.setFitHeight(136);
            powerUpsView.setFitWidth(90);
            powerUpsView.setImage(powerUp);
            this.getChildren().addAll(powerUpsView);
        }
    }
}
