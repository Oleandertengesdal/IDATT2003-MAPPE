package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;

/**
 * Action for revealing a thief token
 */
public class ThiefTileAction implements TileAction {
    private String description = "A thief stole your treasures!";

    public ThiefTileAction() {
        // Constructor
    }

    @Override
    public void perform(Player player) {
        player.setMoney(0);
        player.setHasDiamond(false);
    }

    @Override
    public String getDescription() {
        return description;
    }
}