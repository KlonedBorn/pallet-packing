package edu.uwi.soscai.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Rect {
    private static final double SCALE_FACTOR = 0.8;
    private static final double TO_SCREEN = 4.0;
    private final DoubleProperty xProperty = new SimpleDoubleProperty(this, "rect.x", 0.0);
    private final DoubleProperty yProperty = new SimpleDoubleProperty(this, "rect.y", 0.0);
    private final DoubleProperty widthProperty = new SimpleDoubleProperty(this, "rect.width", 0.0);
    private final DoubleProperty lengthProperty = new SimpleDoubleProperty(this, "rect.length", 0.0);
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty(this, "rect.selected", false);
    private final BooleanProperty rotatedProperty = new SimpleBooleanProperty(this, "rect.rotated", false);
    private final Rectangle view = new Rectangle();
    private final Color fill;

    public Rect(double width, double length, Color fill) {
        this(0.0, 0.0, width, length, fill);
    }

    public Rect(double x, double y, double width, double length, Color fill) {
        this.xProperty.setValue(x);
        this.yProperty.setValue(y);
        this.widthProperty.setValue(width);
        this.lengthProperty.setValue(length);
        this.fill = fill;
        view.setStroke(Color.BLACK);
        view.xProperty().bind(xProperty.multiply(TO_SCREEN));
        view.yProperty().bind(yProperty.multiply(TO_SCREEN));
        view.widthProperty().bind(widthProperty.multiply(TO_SCREEN).multiply(SCALE_FACTOR));
        view.heightProperty().bind(lengthProperty.multiply(TO_SCREEN).multiply(SCALE_FACTOR));
        view.fillProperty().bind(Bindings.when(selectedProperty).then(Color.LIGHTGRAY).otherwise(fill));
        rotatedProperty.addListener((obs, old, nvw) -> onRotate());
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(fill);
        gc.fillRect(xProperty.getValue() * TO_SCREEN, yProperty.getValue() * TO_SCREEN,
                widthProperty.getValue() * TO_SCREEN,
                lengthProperty.getValue() * TO_SCREEN);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(xProperty.getValue() * TO_SCREEN, yProperty.getValue() * TO_SCREEN,
                widthProperty.getValue() * TO_SCREEN,
                lengthProperty.getValue() * TO_SCREEN);
    }

    // Getter and Setter for x
    public double getX() {
        return xProperty.get();
    }

    public void setX(double x) {
        this.xProperty.set(x);
    }

    public DoubleProperty xProperty() {
        return xProperty;
    }

    // Getter and Setter for y
    public double getY() {
        return yProperty.get();
    }

    public void setY(double y) {
        this.yProperty.set(y);
    }

    public DoubleProperty yProperty() {
        return yProperty;
    }

    // Getter and Setter for width
    public double getWidth() {
        return widthProperty.get();
    }

    public void setWidth(double width) {
        this.widthProperty.set(width);
    }

    public DoubleProperty widthProperty() {
        return widthProperty;
    }

    // Getter and Setter for length
    public double getLength() {
        return lengthProperty.get();
    }

    public void setLength(double length) {
        this.lengthProperty.set(length);
    }

    public DoubleProperty lengthProperty() {
        return lengthProperty;
    }

    // Getter and Setter for selected
    public boolean isSelected() {
        return selectedProperty.get();
    }

    public void setSelected(boolean selected) {
        this.selectedProperty.set(selected);
    }

    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    // Getter and Setter for rotated
    public boolean isRotated() {
        return rotatedProperty.get();
    }

    public void setRotated(boolean rotated) {
        this.rotatedProperty.set(rotated);
    }

    public BooleanProperty rotatedProperty() {
        return rotatedProperty;
    }

    // Getter for view
    public Rectangle getView() {
        return view;
    }

    // Getter for selectFill
    public Color getFill() {
        return fill;
    }

    private final void onRotate() {
        double x = xProperty.getValue();
        double y = yProperty.getValue();
        double width = widthProperty.getValue();
        double length = lengthProperty.getValue();
        xProperty.setValue(y);
        yProperty.setValue(x);
        widthProperty.setValue(length);
        lengthProperty.setValue(width);
    }

    public boolean contains(Point2D point) {
        return (point.getX() >= getX() && point.getX() <= getX() + getWidth() &&
                point.getY() >= getY() && point.getY() <= getY() + getLength());
    }

    public boolean overlaps(Rect rect) {
        double left1 = xProperty.getValue();
        double right1 = left1 + widthProperty.getValue();
        double top1 = yProperty.getValue();
        double bottom1 = top1 + lengthProperty.getValue();

        double left2 = rect.xProperty.getValue();
        double right2 = left2 + rect.widthProperty.getValue();
        double top2 = rect.yProperty.getValue();
        double bottom2 = top2 + rect.lengthProperty.getValue();

        return !(bottom1 <= top2 || top1 >= bottom2 || right1 <= left2 || left1 >= right2);
    }

    public double getArea() {
        return widthProperty.getValue() * lengthProperty.getValue();
    }

    public double minDistance(Rect m) {
        double outerLeft = Math.min(xProperty.getValue(), m.getX());
        double outerRight = Math.max(xProperty.getValue() + widthProperty.getValue(),
                m.getX() + m.getWidth());
        double outerBottom = Math.min(yProperty.getValue(), m.getY());
        double outerTop = Math.max(yProperty.getValue() + lengthProperty.getValue(),
                m.getY() + m.getLength());

        double outerWidth = outerRight - outerLeft;
        double outerHeight = outerTop - outerBottom;

        double innerWidth = Math.max(0, outerWidth - widthProperty.getValue() - m.getWidth());
        double innerHeight = Math.max(0, outerHeight - lengthProperty.getValue() - m.getLength());

        return Math.sqrt(innerWidth * innerWidth + innerHeight * innerHeight);
    }

    public Rect clone() {
        Rect r = new Rect(xProperty.getValue(), yProperty.getValue(), widthProperty.getValue(),
                lengthProperty.getValue(), fill);
        r.setRotated(rotatedProperty.getValue());
        return r;
    }

    public double getBottom() {
        return getY();
    }

    public double getLeft() {
        return getY() + getLength();
    }

    public double getTop() {
        return getX();
    }

    public double getRight() {
        return getX() + getWidth();
    }

    public void rotate() {
        setRotated(!isRotated());
    }

    public Bounds toBounds() {
        return new BoundingBox(getX(), getY(), getX() + getWidth(), getY() + getLength());
    }
}
