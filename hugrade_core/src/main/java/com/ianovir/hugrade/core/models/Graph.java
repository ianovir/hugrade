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
        Optional<GNode> result = nodes.stream().filter(n -> getNodeId(n) == nodeId).findAny();
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
        int nodeId = getNodeId(node);
        return getNeighbors(nodeId);
    }

    public List<GNode> getNeighbors(int node) {
        Stream<GNode> a = Arrays.stream(getEdgesByNodeId(node)).map(e -> getNodeById(e.getSource()));
        Stream<GNode> b = Arrays.stream(getEdgesByNodeId(node)).map(e -> getNodeById(e.getDestination()));
        Stream<GNode> stream = Stream.concat(a, b);

        //TODO filter
        return stream.filter(n-> getNodeId(n)!=node)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all edges connected to the passed node
     * @param node the reference node
     * @return All edges connected to the node
     */
    public GEdge[] getEdgesByNode(GNode node) {
        return getEdgesByNodeId(getNodeId(node));
    }

    public GEdge[] getEdgesByNodeId(int ... id) {
        return edges.stream().filter(e -> nodesContained(e, id))
                .toArray(GEdge[]::new);
    }

    private boolean nodesContained(GEdge e, int[] nodeIds) {
        boolean srcContained = Arrays.stream(nodeIds).anyMatch(v -> v == e.getSource());
        boolean dstContained = Arrays.stream(nodeIds).anyMatch(v -> v == e.getDestination());
        return srcContained || dstContained;
    }

    public GEdge[] getEdgesBySourceNode(GNode node) {
        int nodeId = getNodeId(node);
        return getEdgesBySourceNode(nodeId);
    }

    public GEdge[] getEdgesBySourceNode(int id) {
        return edges.stream().filter(e -> e.getSource()==id)
                .toArray(GEdge[]::new);
    }

    public GEdge[] getEdgesByDestinationNode(GNode node) {
        int nodeId = getNodeId(node);
        return getEdgesByDestinationNode(nodeId);
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


    public void changeNodeID(int oldID, int newID){
        swapEdgesReferences(oldID, newID);
        Collections.swap(nodes, oldID, newID);
    }

    private void swapEdgesReferences(int oldID, int newID) {
        GEdge[] compromisedEdges = getEdgesByNodeId(oldID, newID);
        for(GEdge e : compromisedEdges){
            e.swapNodesIds(oldID, newID);
        }
    }

    public void normalizeNodeEdges(GNode srcNode) {
        GEdge[] nEdges = getEdgesBySourceNode(srcNode);
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

    public boolean edgeExists(int source, int destination) {
        return getEdgesByNodeIDs(source, destination)!=null;
    }

    public int getNodeId(GNode node) {
        return nodes.indexOf(node);
    }

    public boolean isNodeValuesVisible() {
        return nodeValuesVisible;
    }

    public boolean areNodeValuesVisible() {
        return nodeValuesVisible;
    }
}
