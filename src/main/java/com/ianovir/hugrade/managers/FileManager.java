package com.ianovir.hugrade.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.views.GraphView;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static void saveGraphFile(GraphView graphView, File file) {
        FileWriter fw = null;
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String content = gson.toJson(graphView.getGraph());
            fw = new FileWriter(file.getAbsolutePath());
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fw != null;
                fw.flush();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static GraphView openGraphFile(File file)  {
        try (FileReader reader = new FileReader(file.getAbsolutePath()))
        {
            Gson gson = new Gson();
            Graph graph = gson.fromJson(reader, Graph.class);

            graph.sortNodes();
            return new GraphView(graph);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
