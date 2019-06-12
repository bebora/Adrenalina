package it.polimi.se2019.view.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class GuiHelper {

     static Image rotateImage(Double angle,Image image){
        ImageView temp = new ImageView(image);
        temp.setRotate(angle);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return temp.snapshot(params, null);
    }


    static Node getNodeByIndex(GridPane grid, int xIndex,int yIndex){
         Node result = null;
         ObservableList<Node> childrens = grid.getChildren();

         for(Node n: childrens){
             if(GridPane.getColumnIndex(n) == xIndex && GridPane.getRowIndex(n) == yIndex){
                 result = n;
                 break;
             }
         }
        return result;
    }

    static String getColorHexValue(String color){
         switch (color.toLowerCase().charAt(0)){
            case 'r':
            return "#FF0000";
            case 'b':
            return "#0000FF";
            case 'p':
            return "#800080";
            case 'g':
            return "#008000";
            case 'y':
            return "#FFFF00";
            case 'w':
            return "#FFFFFF";
            default:
            return null;
        }
    }

}