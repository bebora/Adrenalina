package it.polimi.se2019.view.gui;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Helper {

     static Image rotateImage(Double angle,Image image){
        ImageView temp = new ImageView(image);
        temp.setRotate(angle);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return temp.snapshot(params, null);
    }
}
