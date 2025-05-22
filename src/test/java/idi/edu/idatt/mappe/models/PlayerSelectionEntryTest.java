package idi.edu.idatt.mappe.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerSelectionEntryTest {

    private Player player;
    private PlayerSelectionEntry entry;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer", "Token1");
        entry = new PlayerSelectionEntry(player);
    }

    @Test
    void testGetPlayer() {
        assertEquals(player, entry.getPlayer());
    }

    @Test
    void testGetName() {
        assertEquals("TestPlayer", entry.getName());
    }

    @Test
    void testGetAndSetToken() {
        assertEquals("Token1", entry.getToken());
        entry.setToken("NewToken");
        assertEquals("NewToken", entry.getToken());
    }

    @Test
    void testGetAndSetColor() {
        String initialColor = entry.getColor();
        assertNotNull(initialColor);
        entry.setColor("#ffffff");
        assertEquals("#ffffff", entry.getColor());
    }

    @Test
    void testIsSelected() {
        assertFalse(entry.isSelected());
        entry.setSelected(true);
        assertTrue(entry.isSelected());
    }

    @Test
    void testSelectedProperty() {
        entry.selectedProperty().set(true);
        assertTrue(entry.isSelected());
        entry.selectedProperty().set(false);
        assertFalse(entry.isSelected());
    }
}