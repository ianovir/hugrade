package com.ianovir.hugrade.core.business.converters;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

/**
 * Converter to Graph Modelling Language (GML)
 */
public class Graph2GMLConverter {

    /**
     * Converts the input graph to a GML string
     * @param graph the input {@link Graph}
     * @return A {@link String} representing the input graph formatted as GML specs
     */
    public static String convert(Graph graph) {
        StringBuilder sb = new StringBuilder();

        //header
        sb.append("graph").append("\n").append("[").append("\n");

        //sb.append("version ").append(Graph.VERSION).append("\n");
        //sb.append("nodes_mu \"").append(graph.getNodeMu()).append("\"\n");
        //sb.append("edges_mu \"").append(graph.getEdgeMu()).append("\"\n");

        for(GNode node : graph.getNodes()) parseNode(graph, node, sb);
        for(GEdge edge : graph.getEdges()) parseEdge(edge, sb);

        //footer
        sb.append("]").append("\n");

        return sb.toString();
    }

    private static void parseNode(Graph graph, GNode node, StringBuilder sb){
        //header
        sb.append("\tnode").append("\n").append("\t[").append("\n");

        sb.append("\t\tid ").append(graph.getNodeId(node)).append("\n");
        sb.append("\t\tlabel \"").append(node.getName()).append("\"\n");
        sb.append("\t\tdescription \"").append(node.getDescription()).append("\"\n");

        sb.append("\t\tgraphics ").append("\n\t\t[").append("\n");
        sb.append("\t\t\tx ").append(node.getX()).append("\n");
        sb.append("\t\t\ty ").append(node.getY()).append("\n");
        sb.append("\t\t\tfill \"").append(node.getColorString()).append("\"\n");
        sb.append("\t\t]").append("\n");

        //footer
        sb.append("\t]").append("\n");
    }

    private static void parseEdge(GEdge edge, StringBuilder sb){
        //header
        sb.append("\tedge").append("\n").append("\t[").append("\n");

        sb.append("\t\tsource ").append(edge.getSource()).append("\n");
        sb.append("\t\ttarget ").append(edge.getDestination()).append("\n");
        sb.append("\t\tlabel \"").append(edge.getWeight()).append("\"\n");
        sb.append("\t\tweight ").append(edge.getWeight()).append("\n");

        //footer
        sb.append("\t]").append("\n");
    }
}
