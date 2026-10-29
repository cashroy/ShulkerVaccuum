package com.shulkerVaccuum.skins;

import com.shulkerVaccuum.core.PlacedSkins;
import com.shulkerVaccuum.core.SpecialBoxes;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point for everything that needs Minecraft 1.21.4+. The main plugin only calls this
 * (by reflection) after checking the server has item models and display entities.
 */
public final class ModernFeatures {
    private ModernFeatures() {
    }

    public static PlacedSkins enable(JavaPlugin plugin, SpecialBoxes boxes) {
        plugin.getServer().getPluginManager().registerEvents(new PackSender(plugin), plugin);
        if (!plugin.getConfig().getBoolean("placed-skins", true)) return PlacedSkins.NONE;
        DisplaySkins skins = new DisplaySkins(plugin, boxes);
        plugin.getServer().getPluginManager().registerEvents(skins, plugin);
        return skins;
    }
}
