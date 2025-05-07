package idi.edu.idatt.mappe.models.enums;

public enum GameType {
    LUDO("Ludo"),
    SNAKES_AND_LADDERS("Snakes and Ladders"),
    THE_LOST_DIAMOND("The Lost Diamond");

    private final String name;

    GameType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
