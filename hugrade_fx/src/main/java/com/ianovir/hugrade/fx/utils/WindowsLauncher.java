package com.ianovir.hugrade.fx.utils;

import com.ianovir.hugrade.core.solvers.path.AStarSolver;
import com.ianovir.hugrade.core.solvers.path.BellmanFordSolver;
import com.ianovir.hugrade.core.solvers.path.DijkstraSolver;
import com.ianovir.hugrade.core.solvers.path.PathSolver;
import com.ianovir.hugrade.fx.GraphChangeObserver;
import com.ianovir.hugrade.fx.controllers.*;
import com.ianovir.hugrade.fx.views.GraphView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowsLauncher {

    public static void launchDijkstraSPSolver(Object controller, GraphView graphView) {
        launchSolverWindow(controller, graphView,  "Dijkstra path solver",
                new DijkstraSolver(graphView.getGraph()));
    }

    public static void launchAStarSPSolver(Object controller, GraphView graphView) {
        launchSolverWindow(controller, graphView, "A* solver",
                new AStarSolver(graphView.getGraph()));
    }

    public static void launchBellmanFordWindow(Object controller, GraphView graphView) {
        var ctrl = launchSolverWindow(controller, graphView,  "Bellman-Ford solver",
                new BellmanFordSolver(graphView.getGraph()));
        if(ctrl!=null){
            ctrl.selectNegativeEdgesItem(PathSolver.NegativeEdgesOp.AS_IS);
            ctrl.setNegativeEdgesDisabled(true);
        }
    }

     private static PathSolverController launchSolverWindow(Object controller, GraphView graphView, String title, PathSolver solver) {
        if(graphView==null) return null;
        try {
            return tryLaunchPathSolverController(controller,graphView, title, solver);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PathSolverController tryLaunchPathSolverController(Object controller, GraphView graphView,
                                                                      String title, PathSolver solver) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/path_solver.fxml"));
        root = loader.load();
        PathSolverController ctrl = loader.getController();
        ctrl.init(graphView, solver);
        launchNewStage(controller, title, root);
        return ctrl;
    }


    public static void launchSettings(Object controller, HugradeSettings settings, GraphChangeObserver observer ) {
        if(settings.getGraphView()==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/settings.fxml"));
            root = loader.load();
            SettingsController ctrl = loader.getController();
            ctrl.init(settings);
            Stage stage = launchNewStage(controller, "Settings", root);
            stage.setOnCloseRequest(event -> observer.onGraphChanged(settings.getGraphView()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void launchAbout(Object controller, Application application) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/about.fxml"));
            root = loader.load();
            AboutController ctrl = loader.getController();
            ctrl.init(application);
            launchNewStage(controller, "About", root);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void launchNodesWindow(Object controller, GraphView graphView, GraphChangeObserver observer) {
        if(graphView==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/nodes_matrix.fxml"));
            root = loader.load();
            NodesMatrixController ctrl = loader.getController();
            ctrl.setGraphView(graphView);
            ctrl.addChangeObserver(observer);
            launchNewStage(controller, "Nodes matrix", root);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void launchProsasWindow(Object controller, GraphView graphView) {
        if(graphView==null || graphView.getGraph()==null) return;
        if(graphView.getGraph().getNodeById(0)==null){
            Alert errAlert = new Alert(Alert.AlertType.ERROR);
            errAlert.setHeaderText("No zero node");
            errAlert.setContentText("The PROSAS solver needs one zero indexed node.\nAborting.");
            errAlert.showAndWait();
            return;
        }
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/prosas_solver.fxml"));
            root = loader.load();
            ProsasSolverController ctrl = loader.getController();
            ctrl.setGraphView(graphView);
            launchNewStage(controller, "Prosas solver", root);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void launchTransMxWindow(Object controller, GraphView graphView, GraphChangeObserver observer) {
        if(graphView==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/transition_matrix.fxml"));
            root = loader.load();
            TransitionMatrixController ctrl = loader.getController();
            ctrl.setGraphView(graphView);
            ctrl.getTransMatrix().addChangeObserver(observer);
            launchNewStage(controller, "Transition matrix", new Scene(root, 450, 450) );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Stage launchNewStage(Object controller, String title, Parent root) {
        return launchNewStage(controller, title, new Scene(root));
    }

    private static Stage launchNewStage(Object controller, String title, Scene scene) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(controller.getClass().getResourceAsStream("/icon.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        return stage;
    }

}
