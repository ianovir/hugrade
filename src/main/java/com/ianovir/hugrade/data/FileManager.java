package com.ianovir.hugrade.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ianovir.hugrade.core.models.Graph;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static void saveGraphFile(Graph graph, File file) {
        FileWriter fw = null;
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String content = gson.toJson(graph);
            fw = new FileWriter(file.getAbsolutePath());
            fw.write(content);
            System.out.printf("Saved to %s",file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileWriter(fw);
        }
    }

    public static Graph openGraphFile(File file)  {
        try (FileReader reader = new FileReader(file.getAbsolutePath()))
        {
            Gson gson = new Gson();
            Graph graph = gson.fromJson(reader, Graph.class);
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
