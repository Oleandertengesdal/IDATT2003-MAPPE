package idi.edu.idatt.mappe.handlers.reader;


import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.models.Board;

public interface BoardFileReader {
    public Board readBoard(String filePath) throws JsonParsingException;
}