package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;

public class GoToStartTileAction implements TileAction {

    private final String description;

    GoToStartTileAction(String description) {
        super();
        this.description = description;
    }

    /**
     * Places the player on the start tile
     *
     * @param player The player to move
     */
    @Override
    public void perform(Player player) {
        player.placeOnTile(player.getBoard().getTileByIndex(1));

    }

    /**
     * Returns the description of the action
     *
     * @return The description of the action
     */
    @Override
    public String getDescription() {
        return description;
    }
}
