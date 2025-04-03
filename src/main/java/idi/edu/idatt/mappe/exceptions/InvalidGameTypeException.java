package idi.edu.idatt.mappe.exceptions;

public class InvalidGameTypeException extends Exception {

    /**
     * Constructs a new InvalidGameTypeException with the specified detail message.
     *
     * @param message The detail message
     */
    public InvalidGameTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidGameTypeException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    public InvalidGameTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
