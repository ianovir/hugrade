package com.ianovir.hugrade.core.models;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The core type of a graph, made of {@link GNode}s and {@link GEdge}s.
 */
public class Graph {

    public static final String VERSION = "1.0.0";

    protected List<GNode> nodes;

    protected List<GEdge> edges;

    private boolean nodeValuesVisible;

    private String nodeMu;

    private String edgeMu;

    public Graph(){
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
        edgeMu= nodeMu = "u";
    }

    public void addNodes(GNode... nodes) {
        Collections.addAll(this.nodes, nodes);
    }

    public void addEdges(GEdge... edges) {
        Collections.addAll(this.edges, edges);
    }

    public void removeNode(GNode node) {
        this.nodes.remove(node);
    }

    public void removeEdge(GEdge edge) {
        this.edges.remove(edge);
    }

    public List<GNode> getNodes() {
        return nodes;
    }

    public GNode getNodeById(int nodeId) {
        Optional<GNode> result = nodes.stream().filter(n -> n.getId() == nodeId).findAny();
        return result.orElse(null);
    }

    public List<GEdge> getEdges() {
        return edges;
    }

    public List<GNode> getNodesByName(String nodeName) {
        return nodes.stream()
                .filter(n-> n.getName().equals(nodeName))
                .collect(Collectors.toList());
    }

    public List<GNode> getNeighbors(GNode node) {
        return getNeighbors(node.getId());
    }

    public List<GNode> getNeighbors(int node) {
        Stream<GNode> a = Arrays.stream(getEdgesByNode(node)).map(e -> getNodeById(e.getSource()));
        Stream<GNode> b = Arrays.stream(getEdgesByNode(node)).map(e -> getNodeById(e.getDestination()));
        Stream<GNode> stream = Stream.concat(a, b);

        //TODO filter
        return stream.filter(n-> n.getId()!=node)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all edges connected to the passed node
     * @param node the reference node
     * @return All edges connected to the node
     */
    public GEdge[] getEdgesByNode(GNode node) {
        return getEdgesByNode(node.getId());
    }

    public GEdge[] getEdgesByNode(int id) {
        return edges.stream().filter(e -> e.getSource()==id ||
                e.getDestination()==id)
                .toArray(GEdge[]::new);
    }

    public GEdge[] getEdgesBySourceNode(GNode node) {
        return getEdgesBySourceNode(node.getId());
    }

    public GEdge[] getEdgesBySourceNode(int id) {
        return edges.stream().filter(e -> e.getSource()==id)
                .toArray(GEdge[]::new);
    }

    public GEdge[] getEdgesByDestinationNode(GNode node) {
        return getEdgesByDestinationNode(node.getId());
    }

    public GEdge[] getEdgesByDestinationNode(int node) {
        return edges.stream().filter(e -> e.getDestination()==node)
                .toArray(GEdge[]::new);
    }

    public GEdge getEdgesByNodeIDs(int sourceID, int destID) {
        Optional<GEdge> candidate = Arrays.stream(edges.stream().filter(e -> e.getSource() == sourceID &&
                e.getDestination() == destID)
                .toArray(GEdge[]::new)).findFirst();
        return candidate.orElse(null);
    }


    /**
     * Re-assign new ids to the nodes
     */
    public void sortNodes(){
        System.out.println("Sorting nodes' IDs...");
        GNode.resetCounter();//resetting counters
        for(GNode n : nodes){
            int oldId = n.getId();
            GEdge[] linkedEdges = getEdgesByNode(n);
            n.resetID();
            //updating linked edges
            for(GEdge e : linkedEdges)
                e.updateNodeId(oldId, n.getId());
        }
    }

    public void changeNodeID(int oldID, int newID){
        GNode nodeA = getNodeById(oldID);
        if(nodeA==null) return;

        GNode nodeB = getNodeById(newID);
        if(nodeB!=null){
            for(GEdge e: getEdgesByNode(nodeB))
                e.updateNodeId(newID, oldID);

            nodeB.setID(oldID);
        }

        for(GEdge e: getEdgesBySourceNode(nodeA))
            e.updateNodeId(oldID, newID);

        nodeA.setID(newID);

        nodes.sort(Comparator.comparingInt(GNode::getId));
    }

    public void normalizeNode(GNode node) {
        GEdge[] nEdges = getEdgesBySourceNode(node);
        float weightSum = 0;
        for(GEdge e : nEdges) weightSum+= Math.abs(e.getWeight());
        if(weightSum!=0){
            for(GEdge e : nEdges) e.setWeight(e.getWeight()/weightSum);
        }
    }

    public boolean showNodeValues() {
        return nodeValuesVisible;
    }

    public void setNodeValuesVisible(boolean nodeValuesVisible) {
        this.nodeValuesVisible = nodeValuesVisible;
    }

    public String getNodeMu() {
        return nodeMu;
    }

    public void setNodeMu(String nodeMu) {
        this.nodeMu = nodeMu;
    }

    public String getEdgeMu() {
        return edgeMu;
    }

    public void setEdgeMu(String edgeMu) {
        this.edgeMu = edgeMu;
    }

    public boolean isNodeValuesVisible() {
        return nodeValuesVisible;
    }

    public boolean edgeExists(int source, int destination) {
        return getEdgesByNodeIDs(source, destination)!=null;
    }
}
