package edu.uwi.soscai.algorithm.packer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

import edu.uwi.soscai.algorithm.BinPacker;
import edu.uwi.soscai.model.Rect;

public class FirstFitAlgorithm extends BinPacker {
    private static final Duration DELAY_BETWEEN_BOXES = Duration.millis(200);

    public FirstFitAlgorithm(Canvas drawer, Bounds container) {
        super(drawer, container);
    }

    private class Shelf {
        private double yPosition;
        private double height;
        private double widthUsed;

        public Shelf(double yPosition, double height) {
            this.yPosition = yPosition;
            this.height = height;
            this.widthUsed = 0;
        }

        public boolean canFit(Rect box) {
            return widthUsed + box.getWidth() <= container.getWidth() && box.getLength() <= height;
        }

        public void placeBox(Rect box) {
            box.setX(widthUsed);
            box.setY(yPosition);
            widthUsed += box.getWidth();
        }
    }

    @Override
    public Map<String, Number> pack(List<Rect> boxes) {
        int boxesPacked = 0;
        List<Shelf> shelves = new ArrayList<>();
        double currentYPosition = 0;
        timeline = new Timeline();
        int index = 0;

        GraphicsContext gc = drawer.getGraphicsContext2D();
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, container.getWidth() * 4.0, container.getHeight() * 4.0);

        long startTime = System.nanoTime();
        for (Rect box : boxes) {
            boolean placed = false;
            for (Shelf shelf : shelves) {
                if (shelf.canFit(box)) {
                    animateShelfOutline(gc, shelf, index);
                    shelf.placeBox(box);
                    animateBoxPlacement(gc, box, ++index);
                    placed = true;
                    boxesPacked += 1;
                    break;
                }
            }

            if (!placed) {
                if (currentYPosition + box.getLength() <= container.getHeight()) {
                    Shelf newShelf = new Shelf(currentYPosition, box.getLength());
                    newShelf.placeBox(box);
                    shelves.add(newShelf);
                    currentYPosition += box.getLength();
                    boxesPacked += 1;
                    animateBoxPlacement(gc, box, ++index);
                } else {
                    System.err.println("No space left to pack the box: " + box);
                }
            }
        }
        timeline.play();

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e6;
        double totalVolume = container.getWidth() * container.getHeight();
        double usedVolume = 0;
        for (Shelf shelf : shelves) {
            usedVolume += shelf.widthUsed * shelf.height;
        }
        double spaceUtilization = (usedVolume / totalVolume) * 100;
        double packingDensity = usedVolume / totalVolume;

        Map<String, Number> metrics = new HashMap<>();
        metrics.put("Total number of boxes", boxes.size());
        metrics.put("Execution Time (ms)", executionTime);
        metrics.put("Space Utilization (%)", spaceUtilization);
        metrics.put("Packing Density", packingDensity);
        metrics.put("Boxes Packed", boxesPacked);
        metrics.put("Boxes Leftout", boxes.size() - boxesPacked);

        metrics.forEach((key, value) -> System.out.println(key + ": " + value));
        return metrics;
    }

    private void animateShelfOutline(GraphicsContext gc, Shelf shelf, int index) {
        KeyFrame keyFrame = new KeyFrame(DELAY_BETWEEN_BOXES.multiply(index), event -> {
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeRect(0, shelf.yPosition * 4.0, container.getWidth() * 4.0, shelf.height * 4.0);
        });
        timeline.getKeyFrames().add(keyFrame);
    }

    private void animateBoxPlacement(GraphicsContext gc, Rect box, int index) {
        KeyFrame keyFrame = new KeyFrame(DELAY_BETWEEN_BOXES.multiply(index), event -> {
            box.setSelected(true);
            box.draw(gc);
        });
        timeline.getKeyFrames().add(keyFrame);
    }
}