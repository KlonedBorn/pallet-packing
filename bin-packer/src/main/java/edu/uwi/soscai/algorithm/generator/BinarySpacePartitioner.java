package edu.uwi.soscai.algorithm.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class BinarySpacePartitioner {

    private Bounds mainContainer;
    private Bounds minSize;
    private int maxSubdivisions;
    private Random random = new Random();

    public BinarySpacePartitioner(Bounds container, Bounds minSize, int maxSubdivisions) {
        this.mainContainer = container;
        this.minSize = minSize;
        this.maxSubdivisions = maxSubdivisions;
    }

    public List<Bounds> generate() {
        List<Bounds> containers = new ArrayList<>();
        partition(mainContainer, containers, 0, random.nextBoolean());
        return containers;
    }

    private void partition(Bounds container, List<Bounds> containers, int subdivisions, boolean splitHorizontally) {
        if (subdivisions >= maxSubdivisions
                || container.getWidth() < minSize.getWidth() * 2 && container.getHeight() < minSize.getHeight() * 2) {
            containers.add(container);
            return;
        }

        if (container.getWidth() < minSize.getWidth() * 2) {
            splitHorizontally = false;
        } else if (container.getHeight() < minSize.getHeight() * 2) {
            splitHorizontally = true;
        }

        if (splitHorizontally) {
            int maxSplit = (int) (container.getHeight() - minSize.getHeight() * 2);
            if (maxSplit > 0) {
                int split = (int) (minSize.getHeight() + random.nextInt(maxSplit + 1));
                partition(new BoundingBox(container.getMinX(), container.getMinY(), container.getWidth(), split),
                        containers,
                        subdivisions + 1, !splitHorizontally);
                partition(
                        new BoundingBox(container.getMinX(), container.getMinY() + split, container.getWidth(),
                                container.getHeight() - split),
                        containers, subdivisions + 1, !splitHorizontally);
            } else {
                containers.add(container);
            }
        } else {
            int maxSplit = (int) (container.getWidth() - minSize.getWidth() * 2);
            if (maxSplit > 0) {
                int split = (int) (minSize.getWidth() + random.nextInt(maxSplit + 1));
                partition(new BoundingBox(container.getMinX(), container.getMinY(), split, container.getHeight()),
                        containers,
                        subdivisions + 1, !splitHorizontally);
                partition(
                        new BoundingBox(container.getMinX() + split, container.getMinY(), container.getWidth() - split,
                                container.getHeight()),
                        containers, subdivisions + 1, !splitHorizontally);
            } else {
                containers.add(container);
            }
        }
    }
}
