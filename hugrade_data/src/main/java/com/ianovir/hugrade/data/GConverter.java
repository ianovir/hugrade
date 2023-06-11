package com.ianovir.hugrade.data;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.data.dtos.EdgeDto;
import com.ianovir.hugrade.data.dtos.GraphDto;
import com.ianovir.hugrade.data.dtos.NodeDto;
import java.util.stream.Collectors;

public class GConverter {

    public static GraphDto Graph2GraphDto(Graph graph){
        GraphDto result = new GraphDto();

        result.nodeValuesVisible = graph.isNodeValuesVisible();
        result.nodeMu = graph.getNodeMu();
        result.edgeMu = graph.getEdgeMu();
        result.nodes = graph.getNodes().stream()
                .map(GConverter::Node2NodeDto)
                .collect(Collectors.toList());
        result.edges = graph.getEdges().stream()
                .map(GConverter::Edge2EdgeDto)
                .collect(Collectors.toList());

        return result;
    }

    public static NodeDto Node2NodeDto(GNode node){
        NodeDto result = new NodeDto();

        result.name = node.getName();
        result.description = node.getDescription();
        result.x = node.getX();
        result.y = node.getY();
        result.color = node.getColor();
        result.value = node.getValue();

        return result;
    }

    public static EdgeDto Edge2EdgeDto(GEdge edge){
        EdgeDto result = new EdgeDto();

        result.weight = edge.getWeight();
        result.source = edge.getSource();
        result.destination = edge.getDestination();

        return result;
    }

    public static Graph GraphDto2Graph(GraphDto dto){
        Graph result = new Graph();

        result.setNodeValuesVisible(dto.nodeValuesVisible);
        result.setNodeMu(dto.nodeMu);
        result.setEdgeMu(dto.edgeMu);
        dto.nodes.forEach(n-> result.addNodes(NodeDto2Node(n)));
        dto.edges.forEach(e-> result.addEdges(EdgeDto2Edge(e)));

        return result;
    }

    public static GNode NodeDto2Node(NodeDto dto){
        GNode result = new GNode();

        result.setName(dto.name);
        result.setDescription(dto.description);
        result.setX(dto.x);
        result.setY(dto.y);
        result.setValue(dto.value);
        result.setColor(dto.color[0], dto.color[1], dto.color[2]);

        return result;
    }

    public static GEdge EdgeDto2Edge(EdgeDto dto){
        GEdge result = new GEdge();

        result.setWeight(dto.weight);
        result.setSource(dto.source);
        result.setDestination(dto.destination);

        return result;
    }



}
