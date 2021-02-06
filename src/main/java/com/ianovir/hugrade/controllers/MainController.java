package com.ianovir.hugrade.controllers;

import com.ianovir.hugrade.Main;
import com.ianovir.hugrade.core.business.converters.Graph2GMLConverter;
import com.ianovir.hugrade.core.business.solvers.path.AStarSolver;
import com.ianovir.hugrade.core.business.solvers.path.BellmanFordSolver;
import com.ianovir.hugrade.core.business.solvers.path.DijkstraSolver;
import com.ianovir.hugrade.core.business.solvers.path.PathSolver;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.views.EdgeView;
import com.ianovir.hugrade.views.NodeView;
import com.ianovir.hugrade.views.GraphView;
import com.ianovir.hugrade.views.TransitionMatrixView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

import static com.ianovir.hugrade.managers.FileManager.openGraphFile;
import static com.ianovir.hugrade.managers.FileManager.saveGraphFile;

public class MainController implements GraphView.SelectionObserver, TransitionMatrixView.GraphChangeObserver {

    private GraphView graphView;
    private NodeView firstNode;

    private NodeView helperOrigin;
    private NodeView helperDestination;
    private EdgeView helperEdge;
    public TextArea taConsole;

    public Menu miFile;
    public MenuBar mainBar;
    public MenuItem miQuit;
    public MenuItem miExportGml;
    public MenuItem miBellmanFordSP;
    public MenuItem miProsasMarch;
    public MenuItem miClearNullEdges;
    public MenuItem miFileNew;
    public MenuItem miFileOpen;
    public MenuItem miFileSave;
    public MenuItem miFileSaveAs;
    public MenuItem miTransMatrix;
    public MenuItem miDijkstraSP;
    public MenuItem miAStarSP;
    public MenuItem miSettings;
    public MenuItem miNodesTable;
    public MenuItem miAbout;

    public BorderPane mainPane;
    public Pane graphContentPane;
    public ScrollPane scrollPane;
    public Pane elPane;

    private Application application;
    private File currentFile;
    private FileChooser fileChooser;

    private double panPressedX;
    private double panPressedY;

    private static final FileChooser.ExtensionFilter graphExtFilter =
            new FileChooser.ExtensionFilter("Hugrade Graph project (*.xgp)", "*.xgp");

    private static final FileChooser.ExtensionFilter gmlExtFilter =
            new FileChooser.ExtensionFilter("Graph Modelling Language (*.gml)", "*.gml");

    public void init(Application application)  {

        this.application = application;
        helperOrigin = new NodeView( 0,0);
        helperDestination = new NodeView(0,0);
        helperEdge = new EdgeView(helperOrigin, helperDestination, 1);
        helperEdge.setColor(Color.RED);

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(graphExtFilter);

        redirectOutput();
        setupMenuItems();
        setupMouseInteractions();
        setupKeyboardInteractions();

        updateScene();
        onGraphChanged(null);
    }

    private void redirectOutput() {
        PrintStream ps = new PrintStream(new TextAreaStream(taConsole));
        System.setOut(ps);
        System.setErr(ps);
    }

    private void setupMenuItems() {

        miFileNew.setOnAction(e -> {
            currentFile=null;
            if(graphView!=null) graphView.removeSelectionObserver(this);
            graphView = buildDefaultTree();
            graphView.addSelectionObserver(this);
            onGraphChanged(graphView);
        });

        miFileOpen.setOnAction(e -> {
                    if(currentFile!=null) fileChooser.setInitialDirectory(currentFile.getParentFile());
                    File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
                    if (file != null) {
                        graphView = openGraphFile(file);
                        if(graphView!=null){
                            graphView.addSelectionObserver(this);
                        }
                        currentFile = file;
                        onGraphChanged(graphView);
                    }
                });

        miFileSave.setOnAction(e -> {
                    if(graphView==null) return;
                    if(currentFile!=null)
                        saveGraphFile(graphView, currentFile);
                    else
                        miFileSaveAs.getOnAction().handle(e);
                });

        miFileSaveAs.setOnAction(e -> {
            if(graphView==null) return;
            if(currentFile!=null) fileChooser.setInitialDirectory(currentFile.getParentFile());
            File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
            if (file != null) {
                saveGraphFile(graphView, file);
            }
        });

        miExportGml.setOnAction(e -> {
            if(graphView==null) return;
            FileChooser fs = new FileChooser();
            fs.getExtensionFilters().add(gmlExtFilter);
            if(currentFile!=null) fs.setInitialDirectory(currentFile.getParentFile());
            File file = fs.showSaveDialog(mainPane.getScene().getWindow());
            if(file!=null){
                String content = Graph2GMLConverter.convert(graphView.getGraph());
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(content);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });

        miQuit.setOnAction(e ->close());
        miTransMatrix.setOnAction(e->launchTransMxWindow());
        miNodesTable.setOnAction(e->launchNodesWindow());
        miDijkstraSP.setOnAction(e->launchDijkstraSPSolver());
        miAStarSP.setOnAction(e->launchAStarSPSolver());
        miProsasMarch.setOnAction(event ->launchProsasWindow());
        miBellmanFordSP.setOnAction(event ->launchBellmanFordWindow());

        miSettings.setOnAction(e-> launchGraphSettings());

        miClearNullEdges.setOnAction(e->{
            if(graphView!=null){
                graphView.clearZeroEdges();
                updateScene();
            }
        });

        miAbout.setOnAction(e-> launchAbout());

    }

    private void launchDijkstraSPSolver() {
        launchSolverWindow("Dijkstra path solver", new DijkstraSolver(graphView.getGraph()));
    }

    private void launchAStarSPSolver() {
        launchSolverWindow("A* solver", new AStarSolver(graphView.getGraph()));
    }

    private void launchBellmanFordWindow() {
       var ctrl = launchSolverWindow("Bellman-Ford solver", new BellmanFordSolver(graphView.getGraph()));
       if(ctrl!=null){
           ctrl.selectNegativeEdgesItem(PathSolver.NegativeEdgesOp.AS_IS);
           ctrl.setNegativeEdgesDisabled(true);
       }
    }

    private PathSolverController launchSolverWindow(String title, PathSolver solver) {
        if(graphView==null) return null;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/path_solver.fxml"));
            root = loader.load();
            PathSolverController ctrl = loader.getController();
            ctrl.init(graphView, solver);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            return ctrl;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void launchGraphSettings() {
        if(graphView==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/graph_settings.fxml"));
            root = loader.load();
            GraphSettingsController ctrl = loader.getController();
            ctrl.init(graphView.getGraph());
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Settings");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setOnCloseRequest(event -> updateScene());
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchAbout() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/about.fxml"));
            root = loader.load();
            AboutController ctrl = loader.getController();
            ctrl.init(application);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("About");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchTransMxWindow() {
        if(graphView==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/transition_matrix.fxml"));
            root = loader.load();
            TransitionMatrixController ctrl = loader.getController();
            ctrl.setGraphView(graphView);
            ctrl.getTransMatrix().addChangeObserver(this);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Transition matrix");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchNodesWindow() {
        if(graphView==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nodes_matrix.fxml"));
            root = loader.load();
            NodesMatrixController ctrl = loader.getController();
            ctrl.setGraphView(graphView);
            ctrl.addChangeObserver(this);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nodes matrix");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchProsasWindow() {
        if(graphView==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/prosas_solver.fxml"));
            root = loader.load();
            ProsasSolverController ctrl = loader.getController();
            ctrl.setGraphView(graphView);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Prosas solver");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupKeyboardInteractions() {
        graphContentPane.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                ArrayList<NodeView> selNodes= graphView.getSelectedNodes();
                ArrayList<EdgeView> selEdges = graphView.getSelectedEdges();
                if(!selNodes.isEmpty()){
                    graphView.removeNodes(selNodes);
                    graphView.clearSelection();
                    updateScene();
                }
                if(!selEdges.isEmpty()){
                    graphView.removeEdges(selEdges);
                    graphView.clearSelection();
                    updateScene();
                }
            }
        });
    }

    private void setupMouseInteractions() {
        //TODO: not working as expected
        scrollPane.setOnMouseClicked(mouseEvent -> {
            if(graphView==null) return;
            double mx = mouseEvent.getX() ;
            double my = mouseEvent.getY();
            Node n = graphView.hitSomething(mx, my);
            if(n!=null) graphView.select(n);

           if (mouseEvent.getButton()== MouseButton.PRIMARY) {
               if (n == null && mouseEvent.getClickCount() == 2) {
                   //adding new node if double-clicked:
                   NodeView nnode = new NodeView("Node", mx, my);
                   nnode.setColor(NodePaneController.getLastColor());
                   graphView.addNodes(nnode);
                   updateScene();
               }
           }
            else
                if(mouseEvent.getButton()== MouseButton.SECONDARY) {
                    if (n instanceof NodeView) {
                        updateNewEdgeProc(n);
                    } else {
                        updateNewEdgeProc(null);
                    }
                }
        });

        //panning:
        graphContentPane.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.MIDDLE)) {
                panPressedX = event.getX();
                panPressedY = event.getY();
            }
        });

        graphContentPane.setOnMouseDragged(event -> {
            if(event.getButton().equals(MouseButton.MIDDLE)) {
                scrollPane.setHvalue(scrollPane.getHvalue() - (event.getX() - panPressedX));
                scrollPane.setVvalue(scrollPane.getVvalue() - (event.getY() - panPressedY));
                event.consume();
            }
        });
    }

    private void updateNewEdgeProc(Node n) {
        if(n==null){
            firstNode = null;
            startNewEdgeHelp(false);
            updateScene();
            return;
        }
        if(firstNode == null){
            firstNode = (NodeView)n;
            helperOrigin.setPos(firstNode.getCenterX(), firstNode.getCenterY());
            startNewEdgeHelp(true);
        }
        else{
            NodeView dst = (NodeView)n;
            if(!graphView.edgeExists(firstNode, dst)){//checking if the edge exists already
                EdgeView ne= new EdgeView(firstNode, dst,1 );
                graphView.addEdges(ne);
                firstNode = null;
                startNewEdgeHelp(false);
                updateScene();
            }
        }
    }

    private void startNewEdgeHelp(boolean start) {
        helperEdge.setVisible(start);
        enableOnMouseUpdate(start);
    }

    private void enableOnMouseUpdate(boolean b) {
        if(b){
            graphContentPane.setOnMouseMoved(mouseEvent -> {
                helperDestination.setCenterX(mouseEvent.getX());
                helperDestination.setCenterY(mouseEvent.getY());
            });
        }else{
            mainPane.setOnMouseMoved(null);
        }
    }

    private void showElPane(Node n) {
        try {
            if(n instanceof  NodeView){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/nodePane.fxml"));
                    Parent root = loader.load();
                    elPane.getChildren().setAll(root);
                    NodePaneController nodeCtrl = loader.getController();
                    nodeCtrl.setNode((NodeView)n, graphView);
            }else if(n instanceof  EdgeView){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edgePane.fxml"));
                Parent root = loader.load();
                elPane.getChildren().setAll(root);
                EdgePaneController edgePaneController = loader.getController();
                edgePaneController.setEdge((EdgeView) n, graphView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GraphView buildDefaultTree() {
       GraphView rTree = new GraphView(new Graph());
       NodeView nodeA = new NodeView("A", 50,100);
       NodeView nodeB = new NodeView("B",  150, 300);
       NodeView nodeC = new NodeView("C", 300, 150);
       EdgeView edge1 = new EdgeView(nodeC, nodeB, 1f);
       EdgeView edge2 = new EdgeView(nodeC, nodeA, 1f);
       EdgeView edge3 = new EdgeView(nodeC, nodeC, 1f);
       rTree.addNodes(nodeA, nodeB, nodeC);
       rTree.addEdges(edge1, edge2, edge3);
       return rTree;
    }

    public void close() {
        if(graphView!=null && currentFile!=null){
            //TODO: ask for saving before exit
            saveGraphFile(graphView, currentFile);
        }
        Platform.exit();
    }

    private void updateScene() {
        if(graphView!=null){
            graphContentPane.getChildren().clear();
            graphContentPane.getChildren().add(helperEdge);
            graphContentPane.getChildren().addAll(graphView.explode());
        }
    }

    @Override
    public void onSelectedItem(Node selectedNode) {
        if(selectedNode!=null) {
            showElPane(selectedNode);
        }else{
            elPane.getChildren().clear();
        }
    }

    @Override
    public void onGraphChanged(GraphView gv) {
        boolean validGraph = gv!=null && gv.getGraph()!=null;

        for(Menu mi : mainBar.getMenus()){
            disableMenuItems(mi, !validGraph);
        }

        miFile.setDisable(false);
        miFileOpen.setDisable(false);
        miQuit.setDisable(false);
        miFileNew.setDisable(false);
        miAbout.setDisable(false);

        updateScene();
    }

    private void disableMenuItems(Menu m, boolean disable){
        for(MenuItem mi: m.getItems()) {
            if(mi instanceof Menu){
                disableMenuItems((Menu) mi, disable);
            }else{
                mi.setDisable(disable);
            }
        }
    }


}
