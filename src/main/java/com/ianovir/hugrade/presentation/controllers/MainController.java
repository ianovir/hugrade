package com.ianovir.hugrade.presentation.controllers;

import com.ianovir.hugrade.core.business.GraphChangeObserver;
import com.ianovir.hugrade.core.business.converters.Graph2GMLConverter;
import com.ianovir.hugrade.core.models.Graph;
import com.ianovir.hugrade.presentation.utils.HugradeSettings;
import com.ianovir.hugrade.presentation.utils.WindowsLauncher;
import com.ianovir.hugrade.presentation.views.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

import static com.ianovir.hugrade.data.FileManager.openGraphFile;
import static com.ianovir.hugrade.data.FileManager.saveGraphFile;

public class MainController implements GraphView.SelectionObserver, GraphChangeObserver {

    public StackPane mainStackPane;
    public Pane toolBar;
    private GraphView graphView;
    private NodeView firstSelectionNode;

    public TextArea taConsole;

    public Menu miFile;
    public MenuBar mainBar;
    public MenuItem miQuit;
    public MenuItem miExportGml;
    public MenuItem miBellmanFordSP;
    public MenuItem miProsasMarch;
    public MenuItem miRemoveZeroEdges;
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

    private final FileChooser.ExtensionFilter graphExtFilter =
            new FileChooser.ExtensionFilter("Hugrade Graph project (*.xgp)", "*.xgp");

    private final FileChooser.ExtensionFilter gmlExtFilter =
            new FileChooser.ExtensionFilter("Graph Modelling Language (*.gml)", "*.gml");
    private NodePaneController mNodePaneController;
    @FXML
    private Grid gridBox;
    private HugradeSettings settings;

    public void init(Application application)  {
        this.application = application;
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(graphExtFilter);

        initSettings();
        initGrid();
        redirectOutput();
        setupMenuItems();
        setupMouseInteractions();
        setupKeyboardInteractions();
        initToolbar();
        setupScrollPaneSizeListeners();

        updateScene();
        onGraphChanged(null);
        printStartingHints();
    }

    private void initToolbar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/toolBar.fxml"));
            Parent root = loader.load();
            toolBar.getChildren().setAll(root);
            ToolBarController controller = loader.getController();
            controller.init(settings, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupScrollPaneSizeListeners() {
        scrollPane.widthProperty().addListener(this::updateContentToScrollPaneWidth);
        scrollPane.heightProperty().addListener(this::updateContentToScrollPaneHeight);
        updateContentToScrollPaneWidth(null, null , scrollPane.getWidth());
    }

    private void initSettings() {
        this.settings = new HugradeSettings();
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
        miTransMatrix.setOnAction(e-> WindowsLauncher.launchTransMxWindow(this, graphView, this));
        miNodesTable.setOnAction(e->WindowsLauncher.launchNodesWindow(this, graphView,this));
        miDijkstraSP.setOnAction(e->WindowsLauncher.launchDijkstraSPSolver(this, graphView));
        miAStarSP.setOnAction(e->WindowsLauncher.launchAStarSPSolver(this, graphView));
        miProsasMarch.setOnAction(event ->WindowsLauncher.launchProsasWindow(this, graphView));
        miBellmanFordSP.setOnAction(event ->WindowsLauncher.launchBellmanFordWindow(this, graphView));
        miSettings.setOnAction(e-> WindowsLauncher.launchSettings(this, settings, this));
        miRemoveZeroEdges.setOnAction(e-> actionRemoveZeroEdges());
        miAbout.setOnAction(e-> WindowsLauncher.launchAbout(this, application));
    }

    private void actionNewFile() {
        if(canCreateNewProject()){
            resetCurrentProject();
        }
    }

    private boolean canCreateNewProject() {
        if(graphView == null) return true;
        Optional<ButtonType> result = showAndWaitNewProjectAlert();
        if(result.isEmpty()) return false;
        return isButtonYesResult(result);
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

    private void actionRemoveZeroEdges() {
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
            saveGraphFile(graphView.getGraph(), file);
            currentFile = file;
        }
    }

    private void actionFileSave(javafx.event.ActionEvent e) {
        if(graphView==null) return;
        if(currentFile!=null)
            saveGraphFile(graphView.getGraph(), currentFile);
        else
            miFileSaveAs.getOnAction().handle(e);
    }

    private void actionOpenFile() {
        if(currentFile!=null) fileChooser.setInitialDirectory(currentFile.getParentFile());
        File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        if (file != null) {
            graphView = new GraphView(openGraphFile(file));
            graphView.addSelectionObserver(this);
            currentFile = file;
            onGraphChanged(graphView);
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
        //scrollPane.setPannable(true);
        graphContentPane.setOnMouseClicked(this::mainMouseClickHandler);
    }


    private void mainMouseClickHandler(javafx.scene.input.MouseEvent mouseEvent) {
        if(graphView==null) return;
        double mx = mouseEvent.getX();
        double my = mouseEvent.getY();
        Node n = checkHitAndSelect(mx, my);

        if (n==null && mouseEvent.getButton()== MouseButton.PRIMARY)
            handlePrimaryClickMouseAction(mouseEvent, mx, my);
         else if(mouseEvent.getButton()== MouseButton.SECONDARY) {
            handleSecondaryClickMouseAction(n);
        }
    }

    private Node checkHitAndSelect(double mx, double my) {
        Node n = graphView.hitSomething(mx, my);
        if(n!=null) graphView.select(n);
        return n;
    }

    private void handlePrimaryClickMouseAction(javafx.scene.input.MouseEvent mouseEvent, double mx, double my) {
        if ( mouseEvent.getClickCount() == 2) {
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
        NodeView nodeView = new NodeView("Node", mx, my);
        nodeView.setColor(NodePaneController.getLastColor());
        graphView.addNodes(nodeView);
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
            graphContentPane.setOnMouseMoved(mouseEvent -> graphView.setHelperDestinationPos(mouseEvent.getX(), mouseEvent.getY()));
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
            updateGrid();
        }
    }

    private void updateGrid() {
        gridBox.enable(graphView.isMagnetGridOn());
        gridBox.refresh();

    }

    private void initGrid() {
        double gridDimension = NodeView.DEF_RAD*4;
        gridBox = new Grid(gridDimension, 1f);
        NodeView.setGridDimension(gridDimension);
        mainStackPane.getChildren().add(0, gridBox);
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
        settings.updateGraphView(gv);
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
        toolBar.setDisable(false);
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
        System.out.println("\t-Click on File->New to create a new project");
    }

    private void printHints() {
        System.out.println("\t-Double click to create a Node");
        System.out.println("\t-Right click to create an Edge between two Nodes");
    }


    private void updateContentToScrollPaneWidth(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (newValue.doubleValue() > graphContentPane.getWidth()) {
            graphContentPane.setMinWidth(newValue.doubleValue());
            gridBox.setMinWidth(newValue.doubleValue());
        }
    }

    private void updateContentToScrollPaneHeight(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (newValue.doubleValue() > graphContentPane.getHeight()) {
            graphContentPane.setMinHeight(newValue.doubleValue());
            gridBox.setMinHeight(newValue.doubleValue());
        }
    }
}
