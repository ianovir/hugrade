package com.ianovir.hugrade.core.models;

/**
 * Graph-Edge, the core type of an edge belonging to a {@link Graph}. A g-edge connects two {@link GNode}s (source and
 * destination).
 */
public class GEdge {

    private float weight;

    private int source;

    private int destination;

    public GEdge(){}

    public GEdge(float weight, int source, int destination) {
        this.weight = weight;
        this.source = source;
        this.destination = destination;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public void updateNodeId(int oldId, int id) {
        if(destination==oldId) destination = id;
        if(source==oldId) source = id;
    }

    @Override
    public String toString() {
        return  source +  "->" + destination+
                " [w" + weight + "]";
    }
}
