package com.ianovir.hugrade.data.plugout;

import com.ianovir.hugrade.core.models.Graph;

import java.io.File;

public interface GraphFileWriter {
    void writeGraph(Graph graph, File file);
}
