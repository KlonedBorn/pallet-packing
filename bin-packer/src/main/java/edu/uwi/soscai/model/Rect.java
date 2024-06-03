package edu.uwi.soscai.model;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Rect {
    private Bounds bounds;
    private final Rectangle shape = getRect();

    public Rect(Bounds bounds) {
        setBounds(bounds);
    }

    public void draw(Canvas canvas) {
        canvas.getGraphicsContext2D().setFill(shape.getFill());
        canvas.getGraphicsContext2D().fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        canvas.getGraphicsContext2D().setStroke(Color.BLACK);
        canvas.getGraphicsContext2D().strokeRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    private void resize() {
        shape.setX(bounds.getMinX() * SCALE);
        shape.setY(bounds.getMinY() * SCALE);
        shape.setWidth(bounds.getWidth() * SCALE);
        shape.setHeight(bounds.getHeight() * SCALE);
    }

    public Rectangle getShape() {
        return shape;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
        if (bounds != null) {
            resize();
        } else {
            shape.setX(0);
            shape.setY(0);
            shape.setWidth(0);
            shape.setHeight(0);
        }
    }

    private Rectangle getRect() {
        Rectangle rect = new Rectangle();
        rect.setFill(Color.color(Math.random(), Math.random(), Math.random()));
        rect.setStroke(Color.BLACK);
        return rect;
    }

    public static final double SCALE = 0.8;
}