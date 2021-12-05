package com.ianovir.hugrade.core.business.converters;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

/**
 * Converts the Graph to a ready-to-use text format.
 */
public class TransMatrix2TxtConverter {

    /**
     * Converts the Graph to a ready-to-use text format
     * @param graph the input {@link Graph}
     * @return a {{@link String}} formatted as follows:
     * - first row will contain an int, representing the number of nodes N in the graph;
     * - next N rows will contain the names of the nodes;
     * - next row will contain an int, representing the number of edges E in the graph;
     * - next E rows till contain the edges' information, formatted as "origin_node_id destination_node_id weight".
     */
    static public String convert(Graph graph) {
        StringBuilder sb = new StringBuilder();

        if(graph!=null){
            sb.append(graph.getNodes().size()).append("\n");
            for(GNode gn : graph.getNodes()){
                sb.append(gn.getName()).append("\n");
            }

            sb.append(graph.getEdges().size()).append("\n");
            for(GEdge ge : graph.getEdges()){
                sb
                        .append(ge.getSource()).append(" ")
                        .append(ge.getDestination()).append(" ")
                        .append(String.format("%.4f", ge.getWeight())).append("\n");
            }
        }
        return sb.toString();
    }
}
