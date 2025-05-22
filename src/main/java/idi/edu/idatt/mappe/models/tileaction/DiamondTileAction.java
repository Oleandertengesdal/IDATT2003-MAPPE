package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.enums.TokenType;

/**
 * Action for revealing a diamond token
 */
public class DiamondTileAction implements TileAction {
    private String description;

    public DiamondTileAction() {
        this.description = "You found the Lost Diamond! Return to a starting city to win!";
    }

    @Override
    public void perform(Player player) {
        player.setHasDiamond(true);
        //player.getGame().notifyObserversOfDiamondFound(player);
    }

    @Override
    public String getDescription() {
        return description;
    }
}