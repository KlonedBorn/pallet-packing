package edu.uwi.soscai.data;

/**
 * Pallet sizes and maximum load capacities.
 * Source: https://www.cargoson.com/en/blog/pallet-sizes-dimensions
 */
public enum PalletSize {
    AMERICAN_SQUARE_PALLET_42X42("American Square Pallet 42\" X 42\"", 106.7, 106.7, 1678),
    AMERICAN_SQUARE_PALLET_48X48("American Square Pallet 48\" X 48\"", 121.9, 121.9, 2087),
    AMERICAN_STANDARD_PALLET("American Standard Pallet", 121.9, 101.6, 2087),
    ASIAN_STANDARD_PALLET("Asian Standard Pallet", 110, 110, 1200),
    AUSTRALIAN_STANDARD_PALLET("Australian Standard Pallet", 116.5, 116.5, 2000),
    EUR2_EUR3_FIN_IPL_ISO2("EUR 2/EUR 3/FIN/IPL/ISO2", 100, 120, 1500),
    EUR2_UK_PALLET("EUR 2 (UK pallet)", 120, 100, 1250),
    EUR_EUR1_ISO1("EUR/EUR 1/ISO1", 120, 80, 1500),
    HPL_EUR6_ISO0("HPL (EUR 6 / ISO0)", 60, 80, 500),
    PXL_PALLET("PXL Pallet", 120, 120, 1200),
    QPL_QUARTER_PALLET("QPL (Quarter Pallet)", 60, 40, 250),
    UPL_PALLET("UPL Pallet", 120, 110, 1200),
    CUSTOM("Custom", 0, 0, 0); // Custom pallet with default values

    private final String name;
    private final double length;
    private final double width;
    private final double maxLoad;

    // Additional fields for custom dimensions
    private double customLength;
    private double customWidth;
    private double customMaxLoad;

    PalletSize(String name, double length, double width, double maxLoad) {
        this.name = name;
        this.length = length;
        this.width = width;
        this.maxLoad = maxLoad;
    }

    public void setCustomDimensions(double length, double width, double maxLoad) {
        if (this != CUSTOM) {
            throw new UnsupportedOperationException("Can only set custom dimensions for CUSTOM pallet");
        }
        this.customLength = length;
        this.customWidth = width;
        this.customMaxLoad = maxLoad;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return this == CUSTOM ? customLength : length;
    }

    public double getWidth() {
        return this == CUSTOM ? customWidth : width;
    }

    public double getMaxLoad() {
        return this == CUSTOM ? customMaxLoad : maxLoad;
    }

    @Override
    public String toString() {
        return name + " (" +
                "length=" + getLength() + " cm, " +
                "width=" + getWidth() + " cm, " +
                "maxLoad=" + getMaxLoad() + " kg)";
    }
}
