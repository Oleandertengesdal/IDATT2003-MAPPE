package idi.edu.idatt.mappe.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PlayerToken extends Circle {

    public PlayerToken(String name) {
        super(10);  // radius
        setFill(Color.web("#" + Integer.toHexString(name.hashCode()).substring(0, 6)));
    }
}
