package it.polimi.se2019.view.gui;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;

/**
 * An utility class containing static methods
 * used repeatedly in the GUI.
 */
 class GuiHelper {

    /**
     * A private constructor to prevent the
     * creation of an instance of this class.
     */
    private GuiHelper(){}

    /**
     * Receives an image and returns the same image
     * rotated by the given angle.
     * @param angle the angle to which the image should be rotated
     * @param image the imaged to be rotated
     * @return the rotated Image
     */
     static Image rotateImage(Double angle,Image image){
        ImageView temp = new ImageView(image);
        temp.setRotate(angle);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return temp.snapshot(params, null);
    }

    /**
     * Gets a node having the specified column and row index
     * from a GridPane
     * @param grid the GridPane from which the node should be retrieved
     * @param xIndex the ColumnIndex of the desired node
     * @param yIndex the RowIndex of the desired node
     * @return the node contained in GridPane having the specified parameters
     */
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

    /**
     * Shifts the hue of a red image to obtain the given color.
     * It modifies the given ColorAdjust that should be applied to the ImageView.
     * @param color the desired color that the image should have after the ColorAdjust
     * @param colorAdjust the ColorAdjust to be applied to the ImageView
     */
    static void hueShifter(String color, ColorAdjust colorAdjust){
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

    /**
     * Applies an InnerShadow to the given node in order
     * to highlight the node.
     * @param n the node to be highlighted
     * @param size the desired width and height of the InnerShadow
     */
     static void applyBorder(Node n, int size){
         InnerShadow borderGlow= new InnerShadow();
         borderGlow.setColor(Color.ORANGE);
         borderGlow.setWidth(size);
         borderGlow.setHeight(size);
         n.setEffect(borderGlow);
     }

    /**
     * Resize the given Pane to fit the exact size of
     * the screen, in order to support different resolution correctly.
     * @param pane the pane to be resized
     */
     static void resizeToScreenSize(Pane pane){
         Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
         Scale scale = new Scale();
         scale.setPivotX(0);
         scale.setPivotY(0);
         scale.setX(primaryScreenBounds.getMaxX()/pane.getPrefWidth());
         scale.setY(primaryScreenBounds.getMaxY()/pane.getPrefHeight());
         pane.getTransforms().addAll(scale);
     }
}