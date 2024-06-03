package edu.uwi.soscai.data;

public enum PackingAlgorithm {
    BEST_FIT("Best Fit Algorithm"),
    BRUTE_FORCE("Brute Force Algorithm"),
    GREEDY("Greedy Algorithm"),
    FIRST_FIT("First Fit Algorithm"),
    NEXT_FIT("Next Fit Algorithm");

    private final String name;

    PackingAlgorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
