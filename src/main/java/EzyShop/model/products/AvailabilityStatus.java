package EzyShop.model.products;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AvailabilityStatus {
    IN_STOCK("In Stock"),
    OUT_OF_STOCK("Out of Stock"),
    LOW_STOCK("Low Stock"),
    PRE_ORDER("Pre-order"),
    DISCONTINUED("Discontinued");

    private final String displayName;

    AvailabilityStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static AvailabilityStatus from(String input) {
        if (input == null)
            return null;
        String normalized = input.trim().toUpperCase().replaceAll("[\\s_]+", "_");
        for (AvailabilityStatus status : values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AvailabilityStatus: " + input);
    }

    public String getDisplayName() {
        return displayName;
    }
}