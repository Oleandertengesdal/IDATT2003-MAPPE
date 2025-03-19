package idi.edu.idatt.mappe.exceptions;

/**
 * Exception thrown when an error occurs while parsing JSON.
 */
public class JsonParsingException extends Exception {

    /**
     * Constructs a new JsonParsingException with the specified detail message.
     *
     * @param message The detail message
     */
    public JsonParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a new JsonParsingException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause
     */
    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
