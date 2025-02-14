package idi.edu.idatt.mappe.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ConfigFileReader {

    private ConfigFileReader() {
        // Private constructor to hide the implicit public one
    }

    public static Logger getLogger(Class<?> clazz) {
        try (InputStream configFile = clazz.getClassLoader().getResourceAsStream("logging.properties")) {
            if (configFile != null) {
                LogManager.getLogManager().readConfiguration(configFile);
            } else {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, "Could not load logging.properties file");
            }
        } catch (IOException e) {
            Logger.getLogger(clazz.getName()).log(Level.SEVERE, "Error loading logging configuration", e);
        }
        return Logger.getLogger(clazz.getName());
    }
}