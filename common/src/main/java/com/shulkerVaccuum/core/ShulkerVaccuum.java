package com.shulkerVaccuum.core;

import com.shulkerVaccuum.handlers.BoxListener;
import com.shulkerVaccuum.handlers.CraftListener;
import com.shulkerVaccuum.handlers.PlacedBoxListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ShulkerVaccuum extends JavaPlugin {
    private final Map<NamespacedKey, BoxType> recipes = new LinkedHashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        SpecialBoxes boxes = new SpecialBoxes(this);
        // Any colour of shulker box + hopper = Vacuum; any colour + lava bucket = Trash.
        registerRecipe(boxes, "shulker_vacuum_recipe", BoxType.VACUUM, Material.HOPPER);
        registerRecipe(boxes, "shulker_trash_recipe", BoxType.TRASH, Material.LAVA_BUCKET);

        getServer().getPluginManager().registerEvents(new BoxListener(boxes), this);
        getServer().getPluginManager().registerEvents(new CraftListener(boxes, recipes), this);
        PlacedSkins skins = loadModernFeatures(boxes);
        getServer().getPluginManager().registerEvents(new PlacedBoxListener(this, boxes, skins), this);
        getLogger().info("ShulkerVaccuum enabled" + (skins == PlacedSkins.NONE ? "" : " with custom placed-box look"));
    }

    @Override
    public void onDisable() {
        recipes.keySet().forEach(Bukkit::removeRecipe);
        recipes.clear();
    }

    /**
     * The placed-box look and resource-pack sending need Minecraft 1.21.4+ (item models and display
     * entities). That code is built separately and only loaded here when the server has those features,
     * so the plugin still runs on older versions.
     */
    private PlacedSkins loadModernFeatures(SpecialBoxes boxes) {
        if (!SpecialBoxes.itemModelsSupported()) return PlacedSkins.NONE;
        try {
            Class.forName("org.bukkit.entity.ItemDisplay");
            Class<?> modern = Class.forName("com.shulkerVaccuum.skins.ModernFeatures");
            return (PlacedSkins) modern.getMethod("enable", JavaPlugin.class, SpecialBoxes.class).invoke(null, this, boxes);
        } catch (ReflectiveOperationException | LinkageError e) {
            getLogger().warning("Couldn't load the placed-box look: " + e);
            return PlacedSkins.NONE;
        }
    }

    private void registerRecipe(SpecialBoxes boxes, String name, BoxType type, Material extra) {
        NamespacedKey key = new NamespacedKey(this, name);
        // The real result is built from the box in the grid (see CraftListener); this is the recipe-book preview.
        ItemStack preview = boxes.mark(new ItemStack(type == BoxType.VACUUM ? Material.CYAN_SHULKER_BOX : Material.RED_SHULKER_BOX), type);
        ShapelessRecipe recipe = new ShapelessRecipe(key, preview);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(Tag.SHULKER_BOXES));
        recipe.addIngredient(extra);
        Bukkit.removeRecipe(key);
        Bukkit.addRecipe(recipe);
        recipes.put(key, type);
    }
}
