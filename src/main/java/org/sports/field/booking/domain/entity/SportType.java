package org.sports.field.booking.domain.entity;

public enum SportType {
    FOOTBALL("Sepak Bola"),
    FUTSAL("Futsal"),
    BASKETBALL("Basket"),
    VOLLEYBALL("Voli"),
    BADMINTON("Bulu Tangkis"),
    TENNIS("Tenis"),
    SQUASH("Squash");

    private final String displayName;

    SportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}