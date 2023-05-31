package com.ianovir.hugrade.data.plugout;

import com.ianovir.hugrade.core.models.Graph;

import java.io.File;

public interface GraphFileReader {
    Graph readGraph(File file);
}
