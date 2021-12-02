package com.ianovir.hugrade.data.dtos;

import java.util.LinkedList;
import java.util.List;

public class GraphDto {

    public List<NodeDto> nodes;
    public List<EdgeDto> edges;
    public boolean nodeValuesVisible;
    public String nodeMu;
    public String edgeMu;

    public GraphDto(){
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
    }

}
