package com.ianovir.hugrade.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.data.dtos.GraphDto;
import com.ianovir.hugrade.data.plugout.GraphFileReader;
import com.ianovir.hugrade.data.plugout.GraphFileWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GraphFileReaderWriter implements GraphFileWriter, GraphFileReader {

    public void writeGraph(Graph graph, File file) {
        FileWriter fw = null;
        try{
            var graphDto = GConverter.Graph2GraphDto(graph);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String content = gson.toJson(graphDto);
            fw = new FileWriter(file.getAbsolutePath());
            fw.write(content);
            System.out.printf("Saved to %s",file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileWriter(fw);
        }
    }

    public Graph readGraph(File file)  {
        try (FileReader reader = new FileReader(file.getAbsolutePath()))
        {
            Gson gson = new Gson();
            var graphDto = gson.fromJson(reader, GraphDto.class);
            var graph = GConverter.GraphDto2Graph(graphDto);
            System.out.printf("Loaded graph from %s",file.getAbsolutePath());
            return graph;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void closeFileWriter(FileWriter fw) {
        try {
            if(fw!=null){
                fw.flush();
                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
