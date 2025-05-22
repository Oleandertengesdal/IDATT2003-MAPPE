package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;

public class GoToJailTileAction implements TileAction {

    private final String description;
    private final Board board;

    public GoToJailTileAction(String description, Board board) {
        this.description = description;
        this.board = board;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void perform(Player player) {
        // player.goToJail();
    }

}
