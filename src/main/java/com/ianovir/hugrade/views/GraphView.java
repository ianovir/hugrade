package com.ianovir.hugrade.views;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.util.*;

/**
 * The JFX element representing and wrapping a {@link Graph}.
 */
public class GraphView {
    private Graph mGraph;
    private ObservableList<EdgeView> edges;
    private ObservableList<NodeView> nodes;

    private ArrayList<NodeView> selectedNodes;
    private ArrayList<EdgeView> selectedEdges;

    private ArrayList<SelectionObserver> selectionObservers;

    public GraphView(Graph graph){
        try{

            selectionObservers = new ArrayList<>();

            nodes = FXCollections.observableList(new ArrayList<>());
            edges = FXCollections.observableList(new ArrayList<>());
            selectedNodes = new ArrayList<>();
            selectedEdges = new ArrayList<>();
            this.mGraph = graph;

            if(mGraph !=null){
                for(GNode n : graph.getNodes()) addNode(new NodeView(n));

                //check for disconnected edges:
                Iterator<GEdge> edgesIterator = graph.getEdges().iterator();
                while (edgesIterator.hasNext()) {
                    GEdge e = edgesIterator.next();
                    NodeView src = findNodeViewById(e.getSource());
                    NodeView dst = findNodeViewById(e.getDestination());
                    if(dst!=null && src!=null)
                        addEdge(new EdgeView(src, dst,e));
                    else
                        edgesIterator.remove();
                }
                //binding obs. lists to inner graph object:
                nodes.addListener((ListChangeListener<NodeView>) c -> {
                    while (c.next()) {
                        if(!c.wasPermutated() && !c.wasUpdated()){
                            for(NodeView nv : c.getAddedSubList()) mGraph.addNodes(nv.getGNode());
                            for(NodeView nv : c.getRemoved()) mGraph.removeNode(nv.getGNode());
                        }
                    }
                });

                edges.addListener((ListChangeListener<EdgeView>) c -> {
                while (c.next()) {
                    if(!c.wasPermutated() && !c.wasUpdated()){
                        for(EdgeView ev : c.getAddedSubList()) mGraph.addEdges(ev.getEdge());
                        for(EdgeView ev : c.getRemoved()) mGraph.removeEdge(ev.getEdge());
                    }
                }

                c.reset();
            });
            }
        }catch(Exception e ){
            e.printStackTrace();
        }

    }

    private NodeView findNodeViewById(int id) {
        Object[] r = nodes.stream().filter(n -> n.getGNode().getId() == id).toArray();
        if(r!=null && r.length>0) return (NodeView) r[0];
        return null;
    }

    private void addNode(NodeView nodeView) {
        nodes.add(nodeView);
    }

    public void addNodes(NodeView... nodeViews) {
        Collections.addAll(this.nodes, nodeViews);
    }

    public void addEdges(EdgeView... edgeViews) {
        Collections.addAll(this.edges, edgeViews);
    }

    public void addEdge(EdgeView edgeView) {
       edges.add(edgeView);
    }

    /**
     *
     * @return All child elements belonging to the current graph
     */
    public List<Node> explode() {
        List<Node> ret = new ArrayList<>();
        for(EdgeView e : edges) {
            e.refresh();
            ret.add(e);
            Collections.addAll(ret, e.getContent());
        }
        for(NodeView n : nodes) {
            n.refresh();
            ret.add(n);
            Collections.addAll(ret, n.getContent(mGraph.showNodeValues()));
        }
        return ret;
    }

    public Node hitSomething(double x, double y) {
        Node n ;
        n= testHitNodes(x, y);
        if(n==null) n= testHitEdges(x, y);
        return n;
    }

    private NodeView testHitNodes(double x, double y) {
        clearSelection();
        for(NodeView n : nodes){
            if(n.getLayoutBounds().contains(new Point2D(x, y))) {
                return n;
            }
        }
        return null;
    }

    private EdgeView testHitEdges(double x, double y) {
        clearSelection();
        for(EdgeView e : edges){
            if(e.intersects(x, y, 0,0)) {
                return e;
            }
        }
        return null;
    }

    public void select(Node n) {
        if(n==null) return;
        clearSelection();
        if(n instanceof  NodeView) addSelectedNode((NodeView) n);
        if(n instanceof EdgeView) setSelectedEdges((EdgeView) n);
    }

    public void selectNodeById(int nid){
        if(nid<0 || nid>=nodes.size()) return;
        clearSelection();
        addSelectedNode(nodes.get(nid));
    }

    public void clearSelection() {
        nodes.forEach(n-> n.select(false));
        edges.forEach(e-> e.setIsSelected(false));
        selectedNodes.clear();
        selectedEdges.clear();
    }

    public ArrayList<NodeView> getSelectedNodes() {
        return selectedNodes;
    }

    public ArrayList<EdgeView> getSelectedEdges() {
        return selectedEdges;
    }

    private void addSelectedNode(NodeView node) {
        if(this.selectedNodes ==null) return;
        selectedNodes.add(node);
        node.select(true);
        notifyObservers(node);
    }

    private void setSelectedEdges(EdgeView edge) {
        if(edge==null) return;
        selectedEdges.add(edge);
        edge.setIsSelected(true);
        notifyObservers(edge);
    }

    public boolean edgeExists(NodeView origin, NodeView destination) {
        return edges.stream().filter(e -> e.getOrigin().equals(origin) &&
                e.getDestination().equals(destination))
                .toArray().length>0;
    }

    public EdgeView[] getEdgesByNode(NodeView node) {
        return edges.stream().filter(e -> e.getOrigin().equals(node) ||
                e.getDestination().equals(node))
                .toArray(EdgeView[]::new);
    }


    public NodeView[] getNodesByEdge(EdgeView e) {
        Optional<NodeView> o = nodes.stream().filter(n -> e.getOrigin().equals(n)).findFirst();
        Optional<NodeView> d = nodes.stream().filter(n -> e.getDestination().equals(n)).findFirst();
        NodeView[] ret = new NodeView[2];
        o.ifPresent(nodeView -> ret[0] = nodeView);
        d.ifPresent(nodeView -> ret[1] = nodeView);
        return ret;
    }

    public Graph getGraph() {
        return mGraph;
    }

    public void removeNode(NodeView sNode) {
        EdgeView[] links = getEdgesByNode(sNode);
        if(links!=null){
            for (EdgeView ev:links) edges.remove(ev);
        }
        nodes.remove(sNode);
    }

    public void removeEdge(EdgeView sEdge) {
        edges.remove(sEdge);
    }

    public void addSelectionObserver(SelectionObserver obs){
        if(!selectionObservers.contains(obs))
            selectionObservers.add(obs);
    }

    public void removeSelectionObserver(SelectionObserver obs){
            selectionObservers.remove(obs);
    }

    private void notifyObservers(Node selectedNode) {
        for(SelectionObserver obs : selectionObservers){
            obs.onSelectedItem(selectedNode);
        }
    }

    public NodeView getNodeById(int nodeID) {
        Optional<NodeView> opn = nodes.stream().filter(n -> n.getGNode().getId() == nodeID).findFirst();
        return opn.orElse(null);
    }

    public void refreshEdges() {
        for(EdgeView ev : edges) ev.refresh();
    }

    public void clearZeroEdges() {
        edges.removeIf(e -> e.getEdge().getWeight() == 0);
    }

    public void removeNodes(ArrayList<NodeView> nodes) {
        if(nodes==null) return;
        for(NodeView n : nodes) removeNode(n);
    }

    public void removeEdges(ArrayList<EdgeView> edges) {
        if(edges==null) return;
        for(EdgeView ev: edges) removeEdge(ev);
    }

    public void selectNodes(int[] nodes) {
        for(Integer i : nodes){
            NodeView nv = getNodeById(i);
            if(nv!=null){
                addSelectedNode(nv);
            }
        }
    }

    void updateOrCreateEdge(int src, int dst, float edgeVal) {
        GEdge edge = getGraph().getEdgesByNodeIDs(src, dst);
        if(edge!=null) {
            edge.setWeight(edgeVal);
        }else{
            //creating new edge
            addEdges(new EdgeView(getNodeById(src),
                    getNodeById(dst),
                    new GEdge(edgeVal, src, dst)));
        }
    }

    public interface SelectionObserver {
        void onSelectedItem(Node selectedNode);
    }

}
