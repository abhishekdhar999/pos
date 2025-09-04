package org.example.enums;


public enum OrderStatus {
    FULFILLABLE("Fulfillable"),
    UNFULFILLABLE("Unfulfillable"),
    CREATED("Created");


    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
