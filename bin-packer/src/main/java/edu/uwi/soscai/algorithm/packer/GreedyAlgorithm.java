package edu.uwi.soscai.algorithm.packer;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

import edu.uwi.soscai.algorithm.BinPacker;
import edu.uwi.soscai.data.PointType;
import edu.uwi.soscai.model.Rect;

public class GreedyAlgorithm extends BinPacker {

    public GreedyAlgorithm(Canvas drawer, Bounds container) {
        super(drawer, container);
    }

    @Override
    public Map<String, Number> pack(List<Rect> boxes) {
        Map<String, Number> metrics = new HashMap<>();
        Configuration C = new Configuration(new Point2D(container.getWidth(), container.getHeight()), boxes);
        BinPackerAlgorithm algorithm = new BinPackerAlgorithm(C);
        C = algorithm.packConfiguration(C);
        for (Rect r : C.getPackedRects()) {
            r.draw(drawer.getGraphicsContext2D());
        }
        System.out.println(C);
        return metrics;
    }

    public class Configuration {
        private static final double EPS = 0.001;

        private final Point2D size;
        private final List<Rect> unpackedRects;
        private final List<Rect> packedRects;
        private List<Rect> L;
        private List<Pair<Point2D, PointType>> concaveCorners;

        public Configuration(Point2D size, List<Rect> unpackedRects, List<Rect> packedRects) {
            this.size = size;
            this.unpackedRects = unpackedRects;
            this.packedRects = packedRects == null ? new ArrayList<>() : packedRects;
            generateL();
        }

        public Configuration(Point2D size, List<Rect> unpackedRects) {
            this(size, unpackedRects, null);
        }

        private void generateL() {
            this.concaveCorners = getConcaveCorners();
            List<Rect> ccoas = new ArrayList<>();
            for (Rect r : unpackedRects) {
                for (Pair<Point2D, PointType> corner : concaveCorners) {
                    for (boolean rotated : new boolean[] { false, true }) {
                        Rect ccoa = new Rect(corner.getKey().getX(), corner.getKey().getY(), r.getWidth(),
                                r.getLength(), r.getFill());
                        if (rotated) {
                            ccoa.setRotated(true);
                        }

                        if (fits(ccoa)) {
                            ccoas.add(ccoa);
                        }
                    }
                }
            }
            this.L = ccoas;
        }

        private List<Pair<Point2D, PointType>> getConcaveCorners() {
            List<Pair<Point2D, PointType>> concaveCorners = new ArrayList<>();
            for (Point2D corner : getAllCorners()) {
                PointType cornerType = getCornerType(corner);
                if (cornerType != null) {
                    concaveCorners.add(new Pair<>(corner, cornerType));
                }
            }
            return concaveCorners;
        }

        private PointType getCornerType(Point2D p) {
            boolean[] checks = checkBoundaries(p);
            if (sum(checks) == 3) {
                int index = -1;
                for (int i = 0; i < checks.length; i++) {
                    if (!checks[i]) {
                        index = i;
                        break;
                    }
                }
                return PointType.values()[index];
            }
            return null;
        }

        private boolean[] checkBoundaries(Point2D p) {
            return new boolean[] {
                    contains(new Point2D(p.getX() + EPS, p.getY() + EPS)),
                    contains(new Point2D(p.getX() - EPS, p.getY() + EPS)),
                    contains(new Point2D(p.getX() + EPS, p.getY() - EPS)),
                    contains(new Point2D(p.getX() - EPS, p.getY() - EPS))
            };
        }

        private boolean contains(Point2D point) {
            if (point.getX() <= 0 || point.getY() <= 0 || size.getX() <= point.getX() || size.getY() <= point.getY()) {
                return true;
            }

            for (Rect r : packedRects) {
                if (r.contains(point)) {
                    return true;
                }
            }
            return false;
        }

        private boolean fits(Rect ccoa) {
            if (ccoa.getX() < 0 || ccoa.getY() < 0 || size.getX() < ccoa.getX() + ccoa.getWidth()
                    || size.getY() < ccoa.getY() + ccoa.getLength()) {
                return false;
            }

            for (Rect rect : packedRects) {
                if (ccoa.overlaps(rect)) {
                    return false;
                }
            }
            return true;
        }

        public void placeRect(Rect rect) {
            packedRects.add(rect);

            unpackedRects.removeIf(r -> (r.getWidth() == rect.getWidth() && r.getLength() == rect.getLength())
                    || (r.getWidth() == rect.getLength() && r.getLength() == rect.getWidth()));

            generateL();

            plotConfiguration();
        }

        public double density() {
            double totalArea = size.getX() * size.getY();
            double occupiedArea = packedRects.stream().mapToDouble(Rect::getArea).sum();
            return occupiedArea / totalArea;
        }

        private List<Point2D> getAllCorners() {
            List<Point2D> corners = new ArrayList<>();
            corners.add(new Point2D(0, 0));
            corners.add(new Point2D(0, size.getY()));
            corners.add(new Point2D(size.getX(), 0));
            corners.add(size);

            for (Rect rect : packedRects) {
                corners.add(new Point2D(rect.getX(), rect.getY()));
                corners.add(new Point2D(rect.getX() + rect.getWidth(), rect.getY()));
                corners.add(new Point2D(rect.getX(), rect.getY() + rect.getLength()));
                corners.add(new Point2D(rect.getX() + rect.getWidth(), rect.getY() + rect.getLength()));
            }
            return corners;
        }

        public boolean isSuccessful() {
            return unpackedRects.isEmpty();
        }

        // Plot configuration (Dummy Implementation)
        private void plotConfiguration() {
            // Implement plotting if necessary
        }

        // Utility method for summing boolean array
        private int sum(boolean[] checks) {
            int sum = 0;
            for (boolean check : checks) {
                if (check) {
                    sum++;
                }
            }
            return sum;
        }

        public List<Rect> getPackedRects() {
            return packedRects;
        }

        public Point2D getSize() {
            return size;
        }

        public List<Rect> getL() {
            return L;
        }

        public List<Rect> getUnpackedRects() {
            return unpackedRects;
        }

        public Configuration deepCopy() {
            Configuration copy = new Configuration(size, new ArrayList<>(unpackedRects), new ArrayList<>(packedRects));
            copy.L = new ArrayList<>(L);
            copy.concaveCorners = new ArrayList<>(concaveCorners);
            return copy;
        }
    }

    public static class BinPackerAlgorithm {
        private Configuration C;

        public BinPackerAlgorithm(Configuration configuration) {
            this.C = configuration;
        }

        private double degree(Rect i, Configuration C) {
            List<Double> dMins = new ArrayList<>();
            for (Rect m : C.getPackedRects()) {
                dMins.add(i.minDistance(m));
            }

            dMins.addAll(List.of(i.getBottom(), i.getLeft(),
                    C.getSize().getY() - i.getTop(), C.getSize().getX() - i.getRight()));

            // Remove two smallest elements, which will be 0 - the two immediate neighbors
            assert (min(dMins) == 0);
            removeMin(dMins);
            assert (min(dMins) == 0);
            removeMin(dMins);

            double minDist = min(dMins);
            return 1 - (minDist / ((i.getWidth() + i.getLength()) / 2));
        }

        private double min(List<Double> dMins) {
            double min = Double.MAX_VALUE;
            for (double num : dMins) {
                if (num < min) {
                    min = num;
                }
            }
            return min;
        }

        private void removeMin(List<Double> arr) {
            double min = min(arr);
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i) == min) {
                    arr.set(i, Double.MAX_VALUE);
                    break;
                }
            }
        }

        private Configuration A0(Configuration C) {
            while (!C.getL().isEmpty()) {
                double[] degrees = new double[C.getL().size()];
                int index = 0;
                for (Rect ccoa : C.getL()) {
                    degrees[index++] = degree(ccoa, C);
                }
                int best = argmax(degrees);
                if (best >= 0)
                    C.placeRect(C.getL().get(best));
            }
            return C;
        }

        private double benefitA1(Rect ccoa, Configuration Cx) {
            Cx.placeRect(ccoa);
            Cx = A0(Cx);
            if (Cx.isSuccessful()) {
                return 1.0; // This could be any high value indicating success
            } else {
                return Cx.density();
            }
        }

        public Configuration packConfiguration(Configuration C) {
            while (!C.getL().isEmpty()) {
                double maxBenefit = 0;
                Rect maxBenefitCcoa = null;

                for (Rect ccoa : C.getL()) {
                    double d = benefitA1(ccoa, C.deepCopy());
                    if (d == 1.0) {
                        System.out.println("Found successful configuration");
                        return C;
                    } else {
                        if (maxBenefit < d) {
                            maxBenefit = d;
                            maxBenefitCcoa = ccoa;
                        }
                    }
                }

                if (maxBenefitCcoa != null) {
                    System.out
                            .println("Placed " + maxBenefitCcoa + ", " + C.getUnpackedRects().size()
                                    + " rects remaining");
                    C.placeRect(maxBenefitCcoa);
                }
            }

            if (C.isSuccessful()) {
                System.out.println("Found successful configuration");
            } else {
                System.out.println("Stopped with failure");
                System.out.println("Rects remaining: " + C.getUnpackedRects());
            }
            return C;
        }

        private int argmax(double[] arr) {
            double maxVal = Double.MIN_VALUE;
            int maxIndex = -1;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] > maxVal) {
                    maxVal = arr[i];
                    maxIndex = i;
                }
            }
            return maxIndex;
        }
    }

}