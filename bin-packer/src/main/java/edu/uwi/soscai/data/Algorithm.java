package edu.uwi.soscai.data;

public enum Algorithm {
    FIRST_FIT("First Fit Algorithm"),
    BEST_FIT("Best Fit Algorithm"),
    BRUTE_FORCE("Brute Force Algorithm"),
    GREEDY("Greedy Algorithm"),
    NEXT_FIT("Next Fit Algorithm");

    private final String name;

    Algorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
