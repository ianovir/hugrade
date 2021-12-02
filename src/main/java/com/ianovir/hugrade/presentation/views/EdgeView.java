package com.ianovir.hugrade.presentation.views;

import com.ianovir.hugrade.core.models.GEdge;
import com.ianovir.hugrade.core.models.Graph;
import javafx.beans.InvalidationListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

/**
 * The JFX element representing and wrapping a {@link GEdge}.
 */
public class EdgeView extends CubicCurve {

    private static final Paint COLOR_SELECTED = Color.BLUE;
    private static final Paint COLOR_USELESS = Color.LIGHTGREY;

    private static final double PI_4 = Math.PI/4;
    private static final double STROKE_W = 2;
    private static final double SELF_FACT = Math.PI;
    private final Graph graph;
    private NodeView origin;
    private NodeView destination;

    private final Text weightLabel;
    private final GEdge mEdge;

    private Group arrowEnd;
    private Polygon triangle;
    private Color color = Color.DARKGRAY;
    private boolean isSelected;
    private InvalidationListener controlXUpdater;
    private InvalidationListener controlYUpdater;

    public EdgeView(Graph graph, NodeView origin, NodeView destination, float weight) {
        this(graph, origin, destination, new GEdge(weight,
                graph.getNodeId(origin.getGNode()),
                graph.getNodeId(destination.getGNode())) );
    }

    public EdgeView(Graph graph, NodeView origin, NodeView destination, GEdge e) {
        super();
        this.mEdge = e;
        this.graph = graph;
        setPickOnBounds(false);
        initExtremesUpdateListeners();
        setOrigin(origin);
        setDestination(destination);
        setStroke(getColor());
        setStrokeLineCap(StrokeLineCap.ROUND);
        setStrokeWidth(STROKE_W);
        setFill(null);

        weightLabel = new Text(String.valueOf(mEdge.getWeight()));
        setupArrowEnd();

        refresh();
    }

    private void initExtremesUpdateListeners() {
        controlXUpdater = observable -> onExtremeChangedX();
        controlYUpdater = observable -> onExtremeChangedY();
    }

    private void onExtremeChangedY() {
        double alpha = getAngle(origin, destination);
        double[] dirs = getControlDirByAngle(alpha);
        double dy = dirs[1];
        if(!origin.equals(destination)){
            updateControlsYStraight(dy);
        }else{
            updateControlsYCircular();
        }
        updateLabelY();
    }

    private void updateControlsYStraight(double dy) {
        double fact = Math.abs(origin.getCenterY()- destination.getCenterY())/3;
        setControlY1(origin.getCenterY() - dy * fact);
        setControlY2(destination.getCenterY() + dy * fact);
        setEndY(destination.getCenterY() + dy * destination.getRadius());
    }

    private void updateControlsYCircular() {
        setControlY1(origin.getCenterY() - SELF_FACT * origin.getRadius());
        setControlY2(origin.getCenterY());
        setEndY(origin.getCenterY());
    }

    private void updateLabelY() {
        double delt = Math.signum(destination.getCenterY()-origin.getCenterY())*origin.getRadius()/2;
        double y1 = (getEndY() + getStartY() + getControlY1() + getControlY2())/4;
        weightLabel.setY(y1+ delt);
    }

    private void onExtremeChangedX() {
        double alpha = getAngle(origin, destination);
        double[] dirs = getControlDirByAngle(alpha);
        double dx = dirs[0];
        if(!origin.equals(destination)){
            updateControlsXStraight(dx);
        }else{
            updateControlsXCircular();
        }
        updateLabelX();
    }

    private void updateControlsXStraight(double dx) {
        double fact = Math.abs(origin.getCenterX()- destination.getCenterX())/3;
        setControlX1(origin.getCenterX() + dx * fact);
        setControlX2(destination.getCenterX() - dx * fact);
        setEndX(destination.getCenterX() - dx * destination.getRadius());
    }

    private void updateControlsXCircular() {
        setControlX1(origin.getCenterX());
        setControlX2(origin.getCenterX() + SELF_FACT * origin.getRadius());
        setEndX(origin.getCenterX() +  origin.getRadius());
    }

    private void updateLabelX() {
        double delt = Math.signum(destination.getCenterY()-origin.getCenterY())*origin.getRadius()/2;
        double x1 = (getEndX() + getStartX() + getControlX1() + getControlX2())/4;
        weightLabel.setX(x1 +delt);
    }

    private void setupArrowEnd() {
        createArrowTriangle();
        setupArrowTriangleTransform();
        arrowEnd = new Group(triangle);
    }

    private void setupArrowTriangleTransform() {
        Rotate triangleRotation = new Rotate();
        triangle.getTransforms().add(triangleRotation);

        InvalidationListener terminalUpdater = o -> refreshArrowTriangleTransform(triangleRotation);
        startXProperty().addListener(terminalUpdater);
        startYProperty().addListener(terminalUpdater);
        endXProperty().addListener(terminalUpdater);
        endYProperty().addListener(terminalUpdater);

        terminalUpdater.invalidated(null);
    }

    private void createArrowTriangle() {
        arrowEnd=new Group();
        triangle = new Polygon();
        triangle.getPoints().addAll(0.0, 0.0,
                -10.0, -5.0,
                -10.0, 5.0);
        triangle.setStroke(getColor());
        triangle.setFill(getColor());
    }

    private void refreshArrowTriangleTransform(Rotate triangleRotation) {
        double sx = getControlX2();
        double sy = getControlY2();
        double ex = getEndX();
        double ey = getEndY();

        triangle.setTranslateX(ex);
        triangle.setTranslateY(ey);
        double a = getAngle(sx, sy, ex, ey);
        double[] dir = getControlDirByAngle(a);
        triangleRotation.setAngle(getDegAngleFromDirection(a, dir));
    }

    private double getDegAngleFromDirection(double rad, double[] dir) {
        return dir[1] == 0 ? grad2Deg(rad) : grad2Deg(-rad);
    }

    private double grad2Deg(double a) {
        return a * 180d / Math.PI;
    }

    public NodeView getOrigin() {
        return origin;
    }

    public NodeView getDestination() {
        return destination;
    }

    public void setOrigin(NodeView origin) {
        unbindPreviousOrigin();

        this.origin = origin;
        mEdge.setSource(graph.getNodeId(origin.getGNode()));

        origin.centerXProperty().addListener(controlXUpdater);
        origin.centerYProperty().addListener(controlYUpdater);

        this.startXProperty().bind(origin.centerXProperty());
        this.startYProperty().bind(origin.centerYProperty());

    }

    public void setDestination(NodeView destination) {
        unbindPreviousDestination();

        this.destination = destination;
        mEdge.setDestination(graph.getNodeId(destination.getGNode()));

        destination.centerXProperty().addListener(controlXUpdater);
        destination.centerYProperty().addListener(controlYUpdater);
    }

    private void unbindPreviousOrigin() {
        if(this.origin!=null){
            this.origin.centerXProperty().removeListener(controlXUpdater);
            this.origin.centerYProperty().removeListener(controlYUpdater);
            this.startXProperty().unbind();
        }
    }

    private void unbindPreviousDestination() {
        if(this.destination!=null){
            this.destination.centerXProperty().removeListener(controlXUpdater);
            this.destination.centerYProperty().removeListener(controlYUpdater);
        }
    }

    public void setIsSelected(boolean b) {
        isSelected = b;
        refresh();
    }

    public GEdge getEdge() {
        return mEdge;
    }

    public double getWeight() {
        return mEdge.getWeight();
    }

    public void setWeight(float value) {
        mEdge.setWeight(value);
        refresh();
    }

    public void refresh() {
        weightLabel.setText( String.format("%.1f", mEdge.getWeight()));
        if(mEdge.getWeight()!=0){
            refreshWeightedEdge();
        }else{
            refreshZeroEdge();
        }
        controlXUpdater.invalidated(null);
        controlYUpdater.invalidated(null);
    }

    private void refreshZeroEdge() {
        setStroke(isSelected? COLOR_SELECTED : COLOR_USELESS);
        weightLabel.setStroke(isSelected? COLOR_SELECTED : COLOR_USELESS);
        triangle.setStroke(isSelected? COLOR_SELECTED : COLOR_USELESS);
        triangle.setFill(isSelected? COLOR_SELECTED : COLOR_USELESS);
        getStrokeDashArray().addAll(5d, 5d);
    }

    private void refreshWeightedEdge() {
        setStroke(isSelected? COLOR_SELECTED : getColor());
        weightLabel.setStroke(isSelected? COLOR_SELECTED : getColor());
        triangle.setStroke(isSelected? COLOR_SELECTED : getColor());
        triangle.setFill(isSelected? COLOR_SELECTED : getColor());
        getStrokeDashArray().clear();
    }


    private double getAngle(NodeView origin, NodeView destination) {
        return getAngle(origin.getCenterX(), origin.getCenterY(),
                destination.getCenterX(), destination.getCenterY());
    }

    private double getAngle(double ox , double oy, double dx, double dy) {
        double deltax = dx - ox;
        double deltay = oy - dy;
        return Math.atan2(deltay, deltax);
    }

    private double[] getControlDirByAngle(double angle){
        double[] ret = {0,0};
        if(isPointingEast(angle))
            ret[0]=1;
        else
        if(isPointingWest(angle))
            ret[0]=-1;

        if(isPointingNorth(angle))
            ret[1]=1;
        else
        if(isPointingSouth(angle))
            ret[1]=-1;
        return ret;
    }

    private boolean isPointingSouth(double angle) {
        return angle>=-3*PI_4 && angle<-PI_4;
    }

    private boolean isPointingNorth(double angle) {
        return angle>=PI_4 && angle<3*PI_4;
    }

    private boolean isPointingWest(double angle) {
        return (angle >= 3 * PI_4 && angle <= Math.PI) || (angle >= -Math.PI && angle < -3 * PI_4);
    }

    private boolean isPointingEast(double angle) {
        return angle>=-PI_4 && angle<PI_4;
    }

    public Node[] getContent() {
        return new Node[] {weightLabel, arrowEnd};
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        setStroke(color);
    }

    @Override
    public String toString(){
        return mEdge.toString();
    }

    @Override
    public boolean contains(double localX, double localY) {
        boolean weightContains = weightLabel.contains(localX, localY);
        boolean superContains =  super.contains(localX, localY);
        return weightContains || superContains;
    }


}
