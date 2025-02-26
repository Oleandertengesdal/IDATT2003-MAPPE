package idi.edu.idatt.mappe.validators;

public class BoardValidator {

    public static void boardSizeValidator(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Board size cannot be less than 1");
        }
    }
}
