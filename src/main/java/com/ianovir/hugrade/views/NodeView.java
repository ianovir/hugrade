package com.ianovir.hugrade.views;

import com.ianovir.hugrade.core.models.GNode;
import javafx.beans.InvalidationListener;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * The JFX element representing and wrapping a {@link GNode}.
 */
public class NodeView extends Circle {

    private GNode mNode;
    private static final Paint COLOR_SELECTED = Color.BLUE;
    public static float DEF_RAD = 25;
    public static float SEL_STROKE = 5;
    private final Text nameText;
    private final Text valueText;
    private Color color ;
    private boolean isSelected;

    public NodeView(String name, double centerX, double centerY, float value){
        super( centerX, centerY, DEF_RAD);
        this.mNode = new GNode(name, centerX, centerY);

        nameText = new Text();
        nameText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 14));

        valueText = new Text();
        valueText.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 10));

        setColor(Color.ORANGE);
        updateFillNStroke();

        addShadow();

        //binding positions to inner node:
        centerXProperty().addListener(o -> mNode.setX(getCenterX()));
        centerYProperty().addListener(o -> mNode.setY(getCenterY()));

        //binding texts' positions:
        InvalidationListener fixNameX = o -> nameText.setX(getCenterX() - nameText.getLayoutBounds().getWidth()/2);
        InvalidationListener fixNameY = o -> nameText.setY(getCenterY() + nameText.getLayoutBounds().getHeight()/4);

        InvalidationListener fixValueX = o -> valueText.setX(getCenterX() - valueText.getLayoutBounds().getWidth()/2);
        InvalidationListener fixValueY = o -> valueText.setY(getCenterY() + (valueText.getLayoutBounds().getHeight() +
                nameText.getLayoutBounds().getHeight())/2);

        centerXProperty().addListener(fixNameX);
        centerYProperty().addListener(fixNameY);

        centerXProperty().addListener(fixValueX);
        centerYProperty().addListener(fixValueY);

        nameText.textProperty().addListener((observable, oldValue, newValue) -> {
            fixNameX.invalidated(null);
            fixNameY.invalidated(null);
            mNode.setName(newValue);
        });

        valueText.textProperty().addListener((observable, oldValue, newValue) -> {
            fixValueX.invalidated(null);
            fixValueY.invalidated(null);
            try {
                NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                Number number = format.parse(newValue);
                mNode.setValue(number.floatValue());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        refresh();
        configureMouseOps();

    }

    public NodeView(String name, double centerX, double centerY){
        this(name, centerX, centerY, 0f);
    }

    private void addShadow() {
        DropShadow e = new DropShadow();
        e.setWidth(50);
        e.setHeight(50);
        e.setOffsetX(0);
        e.setOffsetY(0);
        e.setRadius(5);
        setEffect(e);
    }

    public NodeView(GNode n) {
        this(n.getName(), n.getX(), n.getY(), n.getValue());
        double[] color = n.getColor();
        if(color!=null) this.color = new Color(color[0], color[1], color[2], 1d);
        updateFillNStroke();
        this.mNode = n;
    }

    private void updateFillNStroke(){
        setFill(this.color);
        setStroke(COLOR_SELECTED);
        setStrokeWidth(0);
    }

    public NodeView(double x, double y) {
        this("", x, y);
    }

    public GNode getGNode() {
        return mNode;
    }

    public void setNode(GNode mNode) {
        this.mNode = mNode;
    }

    public Node[] getContent(boolean showValue) {
        return showValue ? new Node[]{nameText, valueText}: new Node[]{nameText};
    }

    public void setName(String c){
        this.nameText.setText(c);
    }

    public void setValue(Float c){
        this.valueText.setText(String.format("%.1f", c));
    }

    public void setPos(double mx, double my) {
        setCenterX(mx);
        setCenterY(my);
    }

    private void configureMouseOps() {
        //Drag&Drop:
        final Point dragPoint = new Point();
        setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragPoint.x = getCenterX() - mouseEvent.getX();
            dragPoint.y = getCenterY() - mouseEvent.getY();
            getScene().setCursor(Cursor.MOVE);
        });
        setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.HAND));
        setOnMouseDragged(mouseEvent -> {
            double newX = mouseEvent.getX() + dragPoint.x;
            if (newX > 0 && newX < getScene().getWidth()) {
                setCenterX(newX);
            }
            double newY = mouseEvent.getY() + dragPoint.y;
            if (newY > 0 && newY < getScene().getHeight()) {
                setCenterY(newY);
            }
        });
        setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
            }
        });
        setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        setFill(color);
        setStroke(COLOR_SELECTED);
        this.mNode.setColor(color.getRed(), color.getGreen(), color.getBlue());

        //label (opposite color):
        Color labelColor = new Color(1 -color.getRed(),
                1 - color.getGreen(),
                1- color.getBlue(), 1d);
        nameText.setFill(labelColor.darker());
    }

    public void select(boolean s){
        this.isSelected = s;
        updateAppearance();
    }

    private void updateAppearance() {
        setStrokeWidth(isSelected? SEL_STROKE : 0);
    }

    public void refresh() {
        nameText.setText(mNode.getName());
        valueText.setText(String.format("%.1f", mNode.getValue()));
    }

    private static class Point { double x, y; }

}
