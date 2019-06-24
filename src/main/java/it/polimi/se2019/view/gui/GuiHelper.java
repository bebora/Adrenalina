package it.polimi.se2019.view.gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
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

    public static void hueShifter(String color, ColorAdjust colorAdjust){
    switch(color.toUpperCase()){
            case "RED":
                colorAdjust.setHue(0);
                break;
            case "BLUE":
                colorAdjust.setHue(-0.70);
                break;
            case "PURPLE":
                colorAdjust.setHue(-0.30);
                break;
            case "GREEN":
                colorAdjust.setHue(0.6);
                break;
            case "WHITE":
                colorAdjust.setBrightness(1.0);
                break;
            case "YELLOW":
                colorAdjust.setHue(0.30);
                break;
            default:
                break;
        }
     }

     public static void applyBorder(Node n, int size){
         InnerShadow borderGlow= new InnerShadow();
         borderGlow.setColor(Color.ORANGE);
         borderGlow.setWidth(size);
         borderGlow.setHeight(size);
         n.setEffect(borderGlow);
     }

}