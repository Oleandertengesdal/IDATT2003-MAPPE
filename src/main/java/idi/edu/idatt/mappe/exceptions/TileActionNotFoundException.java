package idi.edu.idatt.mappe.exceptions;

/**
 * Exception thrown when a tile action is not found.
 */
public class TileActionNotFoundException extends Exception {

    public TileActionNotFoundException(String message) {
        super(message);
    }

    public TileActionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
