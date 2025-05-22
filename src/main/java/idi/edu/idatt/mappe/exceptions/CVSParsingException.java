package idi.edu.idatt.mappe.exceptions;

/**
 * Exception class for handling errors related to parsing CSV files.
 */
public class CVSParsingException extends Exception {


    public CVSParsingException(String message) {
        super(message);
    }

    public CVSParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CVSParsingException(Throwable cause) {
        super(cause);
    }
}
