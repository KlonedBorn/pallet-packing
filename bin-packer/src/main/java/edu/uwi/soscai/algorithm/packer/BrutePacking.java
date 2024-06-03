package edu.uwi.soscai.algorithm.packer;

import java.util.Arrays;

public class BrutePacking {

    public static int[] generateRandomItems(int itemCount, int minSize, int maxSize) {
        int[] items = new int[itemCount];
        for (int i = 0; i < itemCount; i++) {
            items[i] = (int) (Math.random() * (maxSize - minSize + 1) + minSize);
        }
        return items;
    }

    public static void runTest(int[] itemSizes, int binCapacity, String datasetName) {
        System.out.println("Running test for " + datasetName);

        // Measure the start time
        long startTime = System.nanoTime();

        int[] packing = packBins(itemSizes, binCapacity);

        // Measure the end time
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // convert to milliseconds

        System.out.println("Time taken: " + duration + " ms");

        double packingDensity = calculatePackingDensity(itemSizes, packing, binCapacity);
        System.out.println("Packing Density: " + packingDensity);

        // Save the results for graphing
        saveResults(datasetName, itemSizes.length, duration, packingDensity);
    }

    public static void saveResults(String datasetName, int numberOfItems, long timeTaken, double packingDensity) {
        // Save the results to a file or database for later graphing
        // This is just a placeholder for actual implementation
        System.out.printf("Dataset: %s, Number of Items: %d, Time Taken: %d ms, Packing Density: %.2f%n",
                datasetName, numberOfItems, timeTaken, packingDensity);
    }

    public static int[] packBins(int[] itemSizes, int binCapacity) {
        int sumItemSizes = Arrays.stream(itemSizes).sum();
        int itemCount = itemSizes.length;
        int binCount = (int) Math.ceil((double) sumItemSizes / binCapacity);
        int maxIterations = 1000000; // Limit iterations to prevent indefinite hang

        while (true) {
            int[] packing = initPacking(itemCount);
            int iterations = 0;

            while (!checkPacking(binCount, itemCount, itemSizes, packing, binCapacity)) {
                packing = nextPacking(itemCount, binCount, packing);
                if (packing == null || iterations >= maxIterations) break;
                iterations++;
            }

            if (packing != null) {
                return packing;
            }

            binCount++;
        }
    }

    public static int[] initPacking(int itemCount) {
        int[] packing = new int[itemCount];
        Arrays.fill(packing, 0);
        return packing;
    }

    public static boolean checkPacking(int binCount, int itemCount, int[] itemSizes, int[] packing, int binCapacity) {
        int[] bins = new int[binCount];
        Arrays.fill(bins, 0);

        for (int i = 0; i < itemCount; i++) {
            bins[packing[i]] += itemSizes[i];
            if (bins[packing[i]] > binCapacity) return false;
        }

        return true;
    }

    public static int[] nextPacking(int itemCount, int binCount, int[] packing) {
        int packingIndex = 0;

        while (packingIndex < itemCount) {
            if (packing[packingIndex] < binCount - 1) {
                packing[packingIndex]++;
                return packing;
            }

            packing[packingIndex] = 0;
            packingIndex++;
        }

        return null;
    }

    public static double calculatePackingDensity(int[] itemSizes, int[] packing, int binCapacity) {
        int binCount = Arrays.stream(packing).max().getAsInt() + 1;
        int usedVolume = Arrays.stream(itemSizes).sum();
        int totalVolume = binCount * binCapacity;
        return (double) usedVolume / totalVolume;
    }
}
