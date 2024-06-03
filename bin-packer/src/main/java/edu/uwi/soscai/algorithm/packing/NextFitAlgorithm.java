package edu.uwi.soscai.algorithm.packing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NextFitAlgorithm {

    public static void nextFit(List<Double> items, List<Integer> assignment, List<Double> freeSpace, double binCapacity) {
        freeSpace.add(binCapacity);
        int binIdx = 0;
        for (int idx = 0; idx < items.size(); idx++) {
            double value = items.get(idx);
            if (value <= freeSpace.get(binIdx)) {
                freeSpace.set(binIdx, freeSpace.get(binIdx) - value);
                assignment.set(idx, binIdx);
            } else {
                freeSpace.add(binCapacity - value);
                assignment.set(idx, ++binIdx);
            }
        }
    }

    public static List<Double> uniformRandomVector(int n, Random gen, double minSize, double maxSize) {
        List<Double> vec = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            vec.add(minSize + (maxSize - minSize) * gen.nextDouble());
        }
        return vec;
    }

    public static double waste(List<Double> freeSpace) {
        double waste = 0;
        for (double space : freeSpace) {
            waste += space;
        }
        return waste;
    }

    public static void runTest(int size, double minSize, double maxSize, double binCapacity, String datasetName) {
        Random gen = new Random();
        int trials = 50;
        long totalTime = 0;
        double totalDensity = 0;

        for (int t = 0; t < trials; t++) {
            List<Double> vec = uniformRandomVector(size, gen, minSize, maxSize);
            List<Integer> assignment = new ArrayList<>(Collections.nCopies(vec.size(), 0));
            List<Double> freeSpace = new ArrayList<>();

            long startTime = System.nanoTime();
            nextFit(vec, assignment, freeSpace, binCapacity);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);

            double packedVolume = vec.stream().mapToDouble(Double::doubleValue).sum();
            double totalVolume = freeSpace.size() * binCapacity;
            totalDensity += (packedVolume / totalVolume);

            // Debugging statements
            System.out.println("Trial: " + t);
            System.out.println("Items: " + vec);
            System.out.println("Free space: " + freeSpace);
            System.out.println("Packed volume: " + packedVolume);
            System.out.println("Total volume: " + totalVolume);
            System.out.println("Packing density for trial: " + (packedVolume / totalVolume));
        }

        double avgTime = totalTime / (double) trials;
        double avgDensity = totalDensity / (double) trials;

        System.out.printf("Dataset: %s, Number of Items: %d, Average Time (ns): %f, Average Packing Density: %f%n",
                datasetName, size, avgTime, avgDensity);

        saveResults(datasetName, size, avgTime, avgDensity);
    }

    public static void saveResults(String datasetName, int numberOfItems, double timeTaken, double packingDensity) {
        // Save the results to a file or database for later graphing
        // This is just a placeholder for actual implementation
        System.out.printf("Dataset: %s, Number of Items: %d, Time Taken: %f ns, Packing Density: %f%n",
                datasetName, numberOfItems, timeTaken, packingDensity);
    }
}
