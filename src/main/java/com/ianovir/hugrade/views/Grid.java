package com.ianovir.hugrade.views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Grid extends Pane {

    private final double spacing;
    private final float lineWidth;
    private boolean enabledDraw;

    public Grid(double spacing, float lineWidth){
        this.spacing = spacing;
        this.lineWidth = lineWidth;
        bindGridLayout();
    }

    private void bindGridLayout() {
        widthProperty().addListener((observable, oldValue, newValue) -> forceRefresh());
        heightProperty().addListener((observable, oldValue, newValue) -> forceRefresh());
    }

    private void refreshCanvas(Canvas c) {
        clear();
        if(c!=null) getChildren().add(c);
    }

    private Canvas drawCanvas(double width, double height) {
        if(!enabledDraw) return null;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gContext = canvas.getGraphicsContext2D() ;
        gContext.setLineWidth(lineWidth);
        gContext.setStroke(getGridColor());
        drawHorizontalLines(width, height, gContext);
        drawVerticalLines(width, height, gContext);
        return canvas ;
    }

    private Color getGridColor() {
        double tone = 0.9;
        return new Color(tone, tone, tone, 1);
    }

    private void drawHorizontalLines(double width, double height, GraphicsContext gContext) {
        for (double y = 0.0; y < height; y+=spacing) {
            gContext.moveTo(0, y);
            gContext.lineTo(width, y);
            gContext.stroke();
        }
    }

    private void drawVerticalLines(double width, double height, GraphicsContext gContext) {
        for (double x = 0.0; x < width; x+=spacing) {
            gContext.moveTo(x, 0);
            gContext.lineTo(x, height);
            gContext.stroke();
        }
    }

    public void enable(boolean enabled) {
        this.enabledDraw = enabled;
    }

    public void refresh() {
        if(enabledDraw){
            forceRefresh();
        }else{
            clear();
        }
    }

    private void forceRefresh() {
        Canvas c = drawCanvas(getWidth(), getHeight());
        refreshCanvas(c);
    }

    private void clear() {
        this.getChildren().clear();
    }
}
