package idi.edu.idatt.mappe.handlers.writer;

import idi.edu.idatt.mappe.exceptions.JsonParsingException;
import idi.edu.idatt.mappe.handlers.reader.BoardFileReader;
import idi.edu.idatt.mappe.models.Board;

import java.io.FileReader;
import java.io.IOException;


public class BoardFileReaderGson implements BoardFileReader {


    @Override
    public Board readBoard(String filePath) throws JsonParsingException {
        return null;
    }
}
