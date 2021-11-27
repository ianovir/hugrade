package com.ianovir.hugrade.controllers;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

import static com.ianovir.hugrade.managers.FileManager.openGraphFile;
import static com.ianovir.hugrade.managers.FileManager.saveGraphFile;

public class MainController implements GraphView.SelectionObserver, TransitionMatrixView.GraphChangeObserver {

    private GraphView graphView;
    private NodeView firstSelectionNode;

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

    private final FileChooser.ExtensionFilter graphExtFilter =
            new FileChooser.ExtensionFilter("Hugrade Graph project (*.xgp)", "*.xgp");

    private final FileChooser.ExtensionFilter gmlExtFilter =
            new FileChooser.ExtensionFilter("Graph Modelling Language (*.gml)", "*.gml");
    private NodePaneController mNodePaneController;

    public void init(Application application)  {

        this.application = application;

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(graphExtFilter);

        redirectOutput();
        setupMenuItems();
        setupMouseInteractions();
        setupKeyboardInteractions();

        updateScene();
        onGraphChanged(null);
        printStartingHints();
    }

    private void redirectOutput() {
        PrintStream ps = new PrintStream(new TextAreaStream(taConsole));
        System.setOut(ps);
        System.setErr(ps);
    }

    private void setupMenuItems() {
        miFileNew.setOnAction(e -> actionNewFile());
        miFileOpen.setOnAction(e -> actionOpenFile());
        miFileSave.setOnAction(this::actionFileSave);
        miFileSaveAs.setOnAction(e -> actionFileSaveAs());
        miExportGml.setOnAction(e -> actionExportGml());
        miQuit.setOnAction(e ->close(new WindowEvent(taConsole.getScene().getWindow(), e.getEventType())));
        miTransMatrix.setOnAction(e->launchTransMxWindow());
        miNodesTable.setOnAction(e->launchNodesWindow());
        miDijkstraSP.setOnAction(e->launchDijkstraSPSolver());
        miAStarSP.setOnAction(e->launchAStarSPSolver());
        miProsasMarch.setOnAction(event ->launchProsasWindow());
        miBellmanFordSP.setOnAction(event ->launchBellmanFordWindow());
        miSettings.setOnAction(e-> launchGraphSettings());
        miClearNullEdges.setOnAction(e-> actionClearNullEdges());
        miAbout.setOnAction(e-> launchAbout());
    }

    private void actionNewFile() {
        Optional<ButtonType> result = showAndWaitNewProjectAlert();
        if(result.isEmpty()) return;
        if (isButtonYesResult(result)) {
            resetCurrentProject();
        }
    }

    private void resetCurrentProject() {
        currentFile=null;
        if(graphView!=null) graphView.removeSelectionObserver(this);
        graphView = buildDefaultTree();
        graphView.addSelectionObserver(this);
        onGraphChanged(graphView);
        printHints();
    }

    private boolean isButtonYesResult(Optional<ButtonType> result) {
        ButtonType b = ButtonType.NO;
        if(result.isPresent()) b = result.get();
        return b.getButtonData() == ButtonBar.ButtonData.YES;
    }

    private void actionClearNullEdges() {
        if(graphView!=null){
            graphView.clearZeroEdges();
            updateScene();
        }
    }

    private void actionExportGml() {
        if(graphView==null) return;
        FileChooser fs = new FileChooser();
        fs.getExtensionFilters().add(gmlExtFilter);
        if(currentFile!=null) fs.setInitialDirectory(currentFile.getParentFile());
        File file = fs.showSaveDialog(mainPane.getScene().getWindow());
        exportGml(file);
    }

    private void exportGml(File file) {
        if(file ==null) return;
        String content = Graph2GMLConverter.convert(graphView.getGraph());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void actionFileSaveAs() {
        if(graphView==null) return;
        if(currentFile!=null) fileChooser.setInitialDirectory(currentFile.getParentFile());
        File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
        if (file != null) {
            saveGraphFile(graphView, file);
            currentFile = file;
        }
    }

    private void actionFileSave(javafx.event.ActionEvent e) {
        if(graphView==null) return;
        if(currentFile!=null)
            saveGraphFile(graphView, currentFile);
        else
            miFileSaveAs.getOnAction().handle(e);
    }

    private void actionOpenFile() {
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
    }

    private void launchDijkstraSPSolver() {
        launchSolverWindow("Dijkstra path solver",
                new DijkstraSolver(graphView.getGraph()));
    }

    private void launchAStarSPSolver() {
        launchSolverWindow("A* solver",
                new AStarSolver(graphView.getGraph()));
    }

    private void launchBellmanFordWindow() {
       var ctrl = launchSolverWindow("Bellman-Ford solver",
               new BellmanFordSolver(graphView.getGraph()));
       if(ctrl!=null){
           ctrl.selectNegativeEdgesItem(PathSolver.NegativeEdgesOp.AS_IS);
           ctrl.setNegativeEdgesDisabled(true);
       }
    }

    private PathSolverController launchSolverWindow(String title, PathSolver solver) {
        if(graphView==null) return null;
        Parent root;
        try {
            return tryLaunchPathSolverController(title, solver);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PathSolverController tryLaunchPathSolverController(String title, PathSolver solver) throws IOException {
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/path_solver.fxml"));
        root = loader.load();
        PathSolverController ctrl = loader.getController();
        ctrl.init(graphView, solver);
        launchNewStage(title, root);
        return ctrl;
    }

    private Stage launchNewStage(String title, Parent root) {
        return launchNewStage(title, new Scene(root));
    }

    private Stage launchNewStage(String title, Scene scene) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        return stage;
    }

    private void launchGraphSettings() {
        if(graphView==null) return;
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/graph_settings.fxml"));
            root = loader.load();
            GraphSettingsController ctrl = loader.getController();
            ctrl.init(graphView.getGraph());
            Stage stage = launchNewStage("Settings", root);
            stage.setOnCloseRequest(event -> updateScene());
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
            launchNewStage("About", root);
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
            launchNewStage("Transition matrix", new Scene(root, 450, 450) );
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
            launchNewStage("Nodes matrix", root);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchProsasWindow() {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/prosas_solver.fxml"));
            root = loader.load();
            ProsasSolverController ctrl = loader.getController();
            ctrl.setGraphView(graphView);
            launchNewStage("Prosas solver", root);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupKeyboardInteractions() {
        graphContentPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                deleteSelectedNodes(graphView.getSelectedNodes());
                deleteSelectedEdges(graphView.getSelectedEdges());
            }else{
                propagateKeyEvent2ElPanes(e);
            }
        });
        graphContentPane.getScene().onKeyPressedProperty().bind(graphContentPane.onKeyPressedProperty());
    }

    private void propagateKeyEvent2ElPanes(KeyEvent e) {
        if(mNodePaneController!=null){
            mNodePaneController.onKeyPress(e);
        }
    }

    private void deleteSelectedEdges(ArrayList<EdgeView> selEdges) {
        if(selEdges.isEmpty()) return;
        graphView.removeEdges(selEdges);
        graphView.clearSelection();
        updateScene();
    }

    private void deleteSelectedNodes(ArrayList<NodeView> selNodes) {
        if(selNodes.isEmpty()) return;
        graphView.removeNodes(selNodes);
        graphView.clearSelection();
        updateScene();
    }

    private void setupMouseInteractions() {
        //TODO: not working as expected
        scrollPane.setOnMouseClicked(this::mainMouseClickHandler);

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

    private void mainMouseClickHandler(javafx.scene.input.MouseEvent mouseEvent) {
        if(graphView==null) return;
        double mx = mouseEvent.getX() ;
        double my = mouseEvent.getY();
        Node n = checkHitAndSelect(mx, my);

        if (mouseEvent.getButton()== MouseButton.PRIMARY)
            handlePrimaryClickMouseAction(mouseEvent, mx, my, n);

         else if(mouseEvent.getButton()== MouseButton.SECONDARY) {
            handleSecondaryClickMouseAction(n);
        }
    }

    private Node checkHitAndSelect(double mx, double my) {
        Node n = graphView.hitSomething(mx, my);
        if(n!=null) graphView.select(n);
        return n;
    }

    private void handlePrimaryClickMouseAction(javafx.scene.input.MouseEvent mouseEvent, double mx, double my, Node n) {
        if (n == null && mouseEvent.getClickCount() == 2) {
            createNewNode(mx, my);
        }
    }

    private void handleSecondaryClickMouseAction(Node n) {
        if (n instanceof NodeView) {
            updateNewEdgeCreationRoutine(n);
        } else {
            updateNewEdgeCreationRoutine(null);
        }
    }

    private void createNewNode(double mx, double my) {
        NodeView nnode = new NodeView("Node", mx, my);
        nnode.setColor(NodePaneController.getLastColor());
        graphView.addNodes(nnode);
        updateScene();
    }

    private void updateNewEdgeCreationRoutine(Node n) {
        if(n==null){
            resetNewEdgeCreation();
            return;
        }

        if(firstSelectionNode == null)
            startNewEdgeCreation((NodeView) n);
        else
            finishNewEdgeCreation((NodeView) n);

    }

    private void finishNewEdgeCreation(NodeView n) {
        boolean edgeExists = graphView.edgeExists(firstSelectionNode, n);
        if(!edgeExists){
            EdgeView ne= new EdgeView(graphView.getGraph(), firstSelectionNode, n,1 );
            graphView.addEdges(ne);
            resetNewEdgeCreation();
        }
    }

    private void startNewEdgeCreation(NodeView startinNode) {
        firstSelectionNode = startinNode;
        graphView.setHelperOriginPos(firstSelectionNode.getCenterX(), firstSelectionNode.getCenterY());
        startNewEdgeHelp(true);
    }

    private void resetNewEdgeCreation() {
        firstSelectionNode = null;
        startNewEdgeHelp(false);
        updateScene();
    }

    private void startNewEdgeHelp(boolean start) {
        graphView.getHelpEdge().setVisible(start);
        enableOnMouseUpdate(start);
    }

    private void enableOnMouseUpdate(boolean b) {
        if(b){
            graphContentPane.setOnMouseMoved(mouseEvent -> {
                graphView.setHelperDestinationPos(mouseEvent.getX(), mouseEvent.getY());
            });
        }else{
            mainPane.setOnMouseMoved(null);
        }
    }

    private void showElPane(Node n) {
        try {
            tryShowElPane(n);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryShowElPane(Node n) throws IOException {
        if(n instanceof  NodeView){
            showNodeViewPane((NodeView) n);
        }else if(n instanceof  EdgeView){
            showEdgeViewPane((EdgeView) n);
        }
    }

    private void showEdgeViewPane(EdgeView n) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edgePane.fxml"));
        Parent root = loader.load();
        elPane.getChildren().setAll(root);
        EdgePaneController edgePaneController = loader.getController();
        edgePaneController.setEdge(n, graphView);
    }

    private void showNodeViewPane(NodeView n) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/nodePane.fxml"));
        Parent root = loader.load();
        root.setFocusTraversable(true);
        elPane.getChildren().setAll(root);
        NodePaneController nodeCtrl = loader.getController();
        this.mNodePaneController = nodeCtrl;
        nodeCtrl.setNode(n, graphView);
    }

    private GraphView buildDefaultTree() {
        Graph g = new Graph();
        GraphView rTree = new GraphView(g);
        NodeView nodeA = new NodeView("A", 50,100);
        NodeView nodeB = new NodeView("B",  150, 300);
        NodeView nodeC = new NodeView("C", 300, 150);
        rTree.addNodes(nodeA, nodeB, nodeC);
        EdgeView edge1 = new EdgeView(g, nodeC, nodeB, 1f);
        EdgeView edge2 = new EdgeView(g, nodeC, nodeA, 1f);
        EdgeView edge3 = new EdgeView(g, nodeC, nodeC, 1f);
        rTree.addEdges(edge1, edge2, edge3);
        return rTree;
    }

    public void close(WindowEvent event) {
        if(event.isConsumed()) return;
        if (graphView == null) Platform.exit();
        askUserToClose(event);
    }

    private void askUserToClose(WindowEvent event) {
        Optional<ButtonType> result = showAndWaitCloseAlert();
        if(result.isEmpty()) return;

        if (isButtonYesResult(result)) {
            Platform.exit();
        }else{
            event.consume();
        }
    }

    private Optional<ButtonType> showAndWaitNewProjectAlert() {
        return showAndWaitWarningAlert("Not saved",
                "Reset without saving?"
        );
    }

    private Optional<ButtonType> showAndWaitCloseAlert() {
        return showAndWaitWarningAlert("Quit",
                "Are you sure you want to quit?"
        );
    }

    private Optional<ButtonType> showAndWaitWarningAlert(String title, String message) {
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);

        Alert alert = new Alert(Alert.AlertType.WARNING, message, no, yes);
        alert.setHeaderText(title);
        return alert.showAndWait();
    }

    private void updateScene() {
        if(graphView!=null){
            graphContentPane.getChildren().clear();
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
        boolean validGraph = isValidGraph(gv);
        disableUIElements(validGraph);
        updateScene();
    }

    private void disableUIElements(boolean validGraph) {
        for(Menu mi : mainBar.getMenus()){
            disableMenuItems(mi, !validGraph);
        }

        miFile.setDisable(false);
        miFileOpen.setDisable(false);
        miQuit.setDisable(false);
        miFileNew.setDisable(false);
        miAbout.setDisable(false);
    }

    private boolean isValidGraph(GraphView gv) {
        return gv!=null && gv.getGraph()!=null;
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

    private void printStartingHints() {
        System.out.println("Welcome to Hugrade!");
        System.out.println("Click on File->New to create a new project");
    }

    private void printHints() {
        System.out.println("Double click to create a Node");
        System.out.println("Right click to create an Edge between two Nodes");
    }
}
