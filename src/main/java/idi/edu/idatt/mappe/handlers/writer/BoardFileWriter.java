package idi.edu.idatt.mappe.handlers.writer;

import idi.edu.idatt.mappe.models.Board;

public interface BoardFileWriter {
    public void writeBoard(Board board, String filePath);
}