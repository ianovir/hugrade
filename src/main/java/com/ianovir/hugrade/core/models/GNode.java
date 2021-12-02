package com.ianovir.hugrade.core.models;

/**
 * Graph-Node, the core type of a node belonging to a {@link Graph}.
 */
public class GNode {

    private String name;
    private String description;
    private double x;
    private double y;
    double[] color;
    float value;
    public GNode(){}

    public GNode(String name, double posX, double posY) {
        this.name = name;
        this.x = posX;
        this.y = posY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setColor(double red, double green, double blue) {
        this.color = new double[]{red, green, blue};
    }

    public double[] getColor() {
        return color;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getColorString(){
        return String.format("#%02X%02X%02X",
                (int)(color[0] * 255.0),
                (int)(color[1] * 255.0),
                (int)(color[2] * 255.0));
    }

    @Override
    public String toString() {
        return  name ;
    }

}
