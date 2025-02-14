package idi.edu.idatt.mappe.models.games;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;

public class SnakesAndLadders extends BoardGame {

    private Board board;

    public SnakesAndLadders() {
        super();
        createBoard(90);
        createDice(2);
    }
}
