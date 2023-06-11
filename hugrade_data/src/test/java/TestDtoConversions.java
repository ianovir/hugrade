import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.GNode;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.data.GConverter;
import com.ianovir.hugrade.data.dtos.EdgeDto;
import com.ianovir.hugrade.data.dtos.GraphDto;
import com.ianovir.hugrade.data.dtos.NodeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestDtoConversions {


    @Test
    public void test_Graph_to_GraphDto() {
        Graph g = new Graph();
        g.setEdgeMu("edgeMU");
        g.setNodeMu("nodeMU");
        g.setNodeValuesVisible(true);
        g.addNodes(new GNode("Node1", 1, 2));
        g.addNodes(new GNode("Node2", 3, 4));
        g.addNodes(new GNode("Node3", 5, 6));
        g.addEdges(new GEdge(1f, 0,1));
        g.addEdges(new GEdge(1f, 2,1));

        GraphDto gDto = GConverter.Graph2GraphDto(g);

        assertEquals(3, gDto.nodes.size());
        assertEquals(2, gDto.edges.size());
        assertEquals(g.getEdgeMu(), gDto.edgeMu);
        assertEquals(g.getNodeMu(), gDto.nodeMu);
        assertEquals("Node3", gDto.nodes.get(2).name);
        assertEquals(5, gDto.nodes.get(2).x);
        assertEquals(6, gDto.nodes.get(2).y);
        assertEquals(1, gDto.edges.get(1).weight);
        assertEquals(2, gDto.edges.get(1).source);
        assertEquals(1, gDto.edges.get(1).destination);
        assertTrue(gDto.nodeValuesVisible);
    }

    @Test
    public void test_GraphDto_to_Graph() {
        GraphDto gDto = new GraphDto();
        gDto.edgeMu = "edgeMU";
        gDto.nodeMu = "nodeMU";
        gDto.nodeValuesVisible= true;
        NodeDto n1 = new NodeDto();
        n1.value = 1;
        n1.color = new double[]{1,2,3};
        n1.x = 1;
        n1.y = 2;
        n1.name = "Node1";
        NodeDto n2 = new NodeDto();
        n2.value = 1;
        n2.color = new double[]{1,2,3};
        n2.x = 3;
        n2.y = 4;
        n2.name = "Node2";
        NodeDto n3 = new NodeDto();
        n3.value = 1;
        n3.color = new double[]{1,2,3};
        n3.x = 5;
        n3.y = 6;
        n3.name = "Node3";
        EdgeDto e1 = new EdgeDto();
        EdgeDto e2 = new EdgeDto();
        e2.weight = 5;
        e2.destination= 0;
        e2.source = 1;
        gDto.nodes.add(n1);
        gDto.nodes.add(n2);
        gDto.nodes.add(n3);
        gDto.edges.add(e1);
        gDto.edges.add(e2);

        Graph g = GConverter.GraphDto2Graph(gDto);

        Assertions.assertEquals(3, g.getNodes().size());
        Assertions.assertEquals(2, g.getEdges().size());
        Assertions.assertEquals(g.getEdgeMu(), g.getEdgeMu());
        Assertions.assertEquals(g.getNodeMu(), g.getNodeMu());
        Assertions.assertEquals("Node3", g.getNodes().get(2).getName());
        Assertions.assertEquals(5, g.getNodes().get(2).getX());
        Assertions.assertEquals(6, g.getNodes().get(2).getY());
        Assertions.assertEquals(5, g.getEdges().get(1).getWeight());
        Assertions.assertEquals(1, g.getEdges().get(1).getSource());
        Assertions.assertEquals(0, g.getEdges().get(1).getDestination());
        assertTrue(gDto.nodeValuesVisible);
    }


    @Test
    public void test_GEdge_to_EdgeDto() {
        GEdge e = new GEdge();
        e.setSource(8);
        e.setDestination(11);
        e.setWeight(85);

        EdgeDto eDto = GConverter.Edge2EdgeDto(e);

        assertEquals(e.getWeight(), eDto.weight);
        assertEquals(e.getSource(), eDto.source);
        assertEquals(e.getDestination(), eDto.destination);
    }

    @Test
    public void test_EdgeDto_to_GEdge() {
        EdgeDto eDto = new EdgeDto();
        eDto.source = 8;
        eDto.destination = 11;
        eDto.weight = 85;

        GEdge e = GConverter.EdgeDto2Edge(eDto);

        assertEquals(e.getWeight(), eDto.weight);
        assertEquals(e.getSource(), eDto.source);
        assertEquals(e.getDestination(), eDto.destination);
    }


}
