package org.example.enums;


public enum OrderStatus {
    FULFILLABLE("FULFILLABLE"),
    UNFULFILLABLE("UNFULFILLABLE"),
    CREATED("CREATED"),
    INVOICED("INVOICED");


    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
