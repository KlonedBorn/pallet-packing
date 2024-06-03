package edu.uwi.soscai.algorithm.packing;

import java.util.ArrayList;
import java.util.List;
import edu.uwi.soscai.model.Rect;
import javafx.geometry.BoundingBox;

public class GreedyAlgorithm {
    public static class BinPacker {
        private int binWidth;
        private int binHeight;
        private List<Rect> usedRectangles;

        public BinPacker(int binWidth, int binHeight) {
            this.binWidth = binWidth;
            this.binHeight = binHeight;
            this.usedRectangles = new ArrayList<>();
        }

        public int getBinWidth() {
            return binWidth;
        }

        public int getBinHeight() {
            return binHeight;
        }

        public List<Rect> packRectangles(List<Rect> rectangles) {
            // Sort rectangles by area (largest to smallest)
            rectangles.sort((r1, r2) -> (int) ((r2.getBounds().getWidth() * r2.getBounds().getHeight())
                    - (r1.getBounds().getWidth() * r1.getBounds().getHeight())));

            for (Rect rect : rectangles) {
                boolean placed = false;

                // Attempt to place the rectangle in every possible position within the bin
                for (int y = 0; y <= binHeight - rect.getBounds().getHeight(); y++) {
                    for (int x = 0; x <= binWidth - rect.getBounds().getWidth(); x++) {
                        if (canPlaceRect(rect, x, y)) {
                            rect.setBounds(
                                    new BoundingBox(x, y, rect.getBounds().getWidth(), rect.getBounds().getHeight()));
                            usedRectangles.add(rect);
                            placed = true;
                            break;
                        }
                    }
                    if (placed)
                        break;
                }

                if (!placed) {
                    System.out.println("Failed to place rectangle: " + rect);
                }
            }

            return usedRectangles;
        }

        private boolean canPlaceRect(Rect rect, int x, int y) {
            rect.setBounds(
                    new BoundingBox(x, y, rect.getBounds().getWidth(), rect.getBounds().getHeight()));
            // Check if the rectangle overlaps any existing rectangle
            for (Rect usedRect : usedRectangles) {
                if (rectanglesOverlap(rect, usedRect)) {
                    return false;
                }
            }

            // Check if the rectangle is within the container boundary
            if (x < 0 || y < 0 || x + rect.getBounds().getWidth() > binWidth
                    || y + rect.getBounds().getHeight() > binHeight) {
                return false;
            }

            return true;
        }

        private boolean rectanglesOverlap(Rect rect, Rect usedRect) {
            return rect.getBounds().getMinX() < usedRect.getBounds().getMinX()
                    + usedRect.getBounds().getWidth() &&
                    rect.getBounds().getMinX() + rect.getBounds().getWidth() > usedRect.getBounds()
                            .getMinX()
                    &&
                    rect.getBounds().getMinY() < usedRect.getBounds().getMinY()
                            + usedRect.getBounds().getHeight()
                    &&
                    rect.getBounds().getMinY() + rect.getBounds().getHeight() > usedRect
                            .getBounds().getMinY();
        }
    }

    public static void runTest(int binWidth, int binHeight, int numRectangles, int minSize, int maxSize) {
        BinPacker binPacker = new BinPacker(binWidth, binHeight);

        // Generate rectangles with random sizes within the specified range
        List<Rect> rectangles = new ArrayList<>();
        for (int i = 0; i < numRectangles; i++) {
            int width = (int) (Math.random() * (maxSize - minSize + 1) + minSize);
            int height = (int) (Math.random() * (maxSize - minSize + 1) + minSize);
            rectangles.add(new Rect(new BoundingBox(0, 0, width, height)));
        }

        // Pack the rectangles
        long startTime = System.currentTimeMillis();
        List<Rect> packedRectangles = binPacker.packRectangles(rectangles);
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        // Calculate the total volume of the packed rectangles
        int totalPackedVolume = 0;
        for (Rect rect : packedRectangles) {
            totalPackedVolume += rect.getBounds().getWidth() * rect.getBounds().getHeight();
        }

        // Calculate the total available volume in the bin
        int totalAvailableVolume = binPacker.getBinWidth() * binPacker.getBinHeight();

        // Calculate the packing density
        double packingDensity = (double) totalPackedVolume / totalAvailableVolume;

        // Print the packed rectangles
        System.out.println("Packed Rectangles for bin size " + binWidth + "x" + binHeight + ":");
        for (Rect rect : packedRectangles) {
            System.out.println(
                    "Rectangle: x=" + rect.getBounds().getMinX() + ", y=" + rect.getBounds().getMinY() + ", width="
                            + rect.getBounds().getWidth()
                            + ", height=" + rect.getBounds().getHeight());
        }

        // Print the results
        System.out.println("Number of Rectangles: " + packedRectangles.size());
        System.out.println("Time Taken: " + timeTaken + " ms");
        System.out.println("Packing Density: " + packingDensity);
        System.out.println();
    }
}