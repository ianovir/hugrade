package com.ianovir.hugrade.core.business.converters;

import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Converts the graph to a csv transition matrix
 */
public class TransMatrix2CsvConverter {

    /**
     * Converts the graph to a csv String.
     * @param graph  the input {@link Graph}
     * @return a {{@link String}} representing the graph and formatted as follow:
     * the first row is a header with the csv names of the nodes; the next i-th row contains the list of weights of all
     * the edges starting from the i-th node.
     */
    static public String doWork(Graph graph) {
        StringBuilder sb = new StringBuilder();

        if(graph!=null){
            graph.sortNodes();

            //csv header
            sb.append(graph.getNodes().stream().map(GNode::getName).collect(Collectors.joining(",")));
            sb.append("\n");

            float[][] ret = Graph2TransMatrixConverter.convert(graph);

            for(float[] fr: ret){
                sb.append(IntStream.range(0, fr.length)
                        .mapToDouble(i-> fr[i])
                        .mapToObj(e -> String.format("%.4f", e))
                        .collect(Collectors.joining(","))
                );
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
