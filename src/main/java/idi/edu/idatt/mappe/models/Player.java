package idi.edu.idatt.mappe.models;

public class Player {

    private String name;
    private Tile currentTile;
    private BoardGame game;

    public Player(String name, BoardGame game) {
        this.name = name;
        this.game = game;
    }

    public void moveOnTile(Tile tile) {
        //TODO:
    }

    public void move(int steps) {
        //TODO:
    }
}
