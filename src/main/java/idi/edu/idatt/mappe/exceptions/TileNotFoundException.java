package idi.edu.idatt.mappe.exceptions;

public class TileNotFoundException extends Exception {

    public TileNotFoundException(String message) {
        super(message);
    }

    public TileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TileNotFoundException(Throwable cause) {
        super(cause);
    }
}
