package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.enums.TokenType;

/**
 * Action for revealing a treasure token
 */
public class TreasureTileAction implements TileAction {
    private TokenType treasureType;
    private String description;

    public TreasureTileAction(TokenType treasureType) {
        this.treasureType = treasureType;
        this.description = "You found a " + treasureType.name() + " worth " + treasureType.getValue() + " coins!";
    }

    @Override
    public void perform(Player player) {
        player.addMoney(treasureType.getValue());
    }

    @Override
    public String getDescription() {
        return description;
    }
}