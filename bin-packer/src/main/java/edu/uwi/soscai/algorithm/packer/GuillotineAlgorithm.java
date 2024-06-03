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

public class GuillotineAlgorithm extends BinPacker {
    private static final Duration DELAY_BETWEEN_BOXES = Duration.millis(200);
    private boolean rotation;
    private List<FreeRectangle> freeRectangles;
    private List<Rect> placedBoxes;

    public GuillotineAlgorithm(Canvas drawer, Bounds container, boolean rotation) {
        super(drawer, container);
        this.rotation = rotation;
        this.freeRectangles = new ArrayList<>();
        this.placedBoxes = new ArrayList<>();
        this.freeRectangles.add(new FreeRectangle(0, 0, container.getWidth(), container.getHeight()));
    }

    private class FreeRectangle {
        double x, y, width, height;

        public FreeRectangle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public double area() {
            return width * height;
        }

        public boolean canFit(Rect box) {
            return (box.getWidth() <= width && box.getLength() <= height) ||
                    (rotation && box.getLength() <= width && box.getWidth() <= height);
        }

        public List<FreeRectangle> split(Rect box) {
            List<FreeRectangle> newFreeRectangles = new ArrayList<>();
            double rightWidth = width - box.getWidth();
            double topHeight = height - box.getLength();

            if (rightWidth > 0) {
                newFreeRectangles.add(new FreeRectangle(x + box.getWidth(), y, rightWidth, height));
            }
            if (topHeight > 0) {
                newFreeRectangles.add(new FreeRectangle(x, y + box.getLength(), width, topHeight));
            }
            return newFreeRectangles;
        }
    }

    @Override
    public Map<String, Number> pack(List<Rect> boxes) {
        int boxesPacked = 0;
        freeRectangles.clear();
        freeRectangles.add(new FreeRectangle(0, 0, container.getWidth(), container.getHeight()));
        placedBoxes.clear();
        timeline = new Timeline();
        int index = 0;

        GraphicsContext gc = drawer.getGraphicsContext2D();
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, container.getWidth() * 4.0, container.getHeight() * 4.0);

        long startTime = System.nanoTime();
        for (Rect box : boxes) {
            boolean placed = false;
            FreeRectangle bestRect = null;
            boolean bestRotation = false;
            double bestShortside = Double.MAX_VALUE;

            for (FreeRectangle freeRectangle : freeRectangles) {
                if (freeRectangle.canFit(box) && !doesOverlap(box, freeRectangle)) {
                    double shortside = Math.min(freeRectangle.width - box.getWidth(),
                            freeRectangle.height - box.getLength());
                    if (shortside < bestShortside) {
                        bestRect = freeRectangle;
                        bestShortside = shortside;
                        bestRotation = false;
                    }
                }
                if (rotation && freeRectangle.canFit(box) && !doesOverlap(box, freeRectangle)) {
                    box.rotate();
                    double shortside = Math.min(freeRectangle.width - box.getWidth(),
                            freeRectangle.height - box.getLength());
                    if (shortside < bestShortside) {
                        bestRect = freeRectangle;
                        bestShortside = shortside;
                        bestRotation = true;
                    }
                    box.rotate(); // Rotate back
                }
            }

            if (bestRect != null) {
                if (bestRotation) {
                    box.rotate();
                }
                box.setX(bestRect.x);
                box.setY(bestRect.y);
                List<FreeRectangle> newFreeRectangles = bestRect.split(box);
                freeRectangles.remove(bestRect);
                freeRectangles.addAll(newFreeRectangles);
                placedBoxes.add(box);
                animateBoxPlacement(gc, box, ++index);
                boxesPacked++;
                placed = true;
            }

            if (!placed) {
                System.err.println("No space left to pack the box: " + box);
            }
        }
        timeline.play();

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e6;
        double totalVolume = container.getWidth() * container.getHeight();
        double usedVolume = 0;
        for (Rect box : placedBoxes) {
            usedVolume += box.getWidth() * box.getLength();
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

    private void animateBoxPlacement(GraphicsContext gc, Rect box, int index) {
        KeyFrame keyFrame = new KeyFrame(DELAY_BETWEEN_BOXES.multiply(index), event -> {
            box.setSelected(true);
            box.draw(gc);
        });
        timeline.getKeyFrames().add(keyFrame);
    }

    private boolean doesOverlap(Rect box, FreeRectangle freeRectangle) {
        for (Rect placedBox : placedBoxes) {
            if (!(box.getX() + box.getWidth() <= placedBox.getX() ||
                    box.getX() >= placedBox.getX() + placedBox.getWidth() ||
                    box.getY() + box.getLength() <= placedBox.getY() ||
                    box.getY() >= placedBox.getY() + placedBox.getLength())) {
                return true;
            }
        }
        return false;
    }
}
