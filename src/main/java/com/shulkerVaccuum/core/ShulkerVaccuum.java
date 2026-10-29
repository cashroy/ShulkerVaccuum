package com.shulkerVaccuum.core;

import com.shulkerVaccuum.handlers.BoxListener;
import com.shulkerVaccuum.handlers.CraftListener;
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
        SpecialBoxes boxes = new SpecialBoxes(this);
        // Any colour of shulker box + hopper = Vacuum; any colour + lava bucket = Trash.
        registerRecipe(boxes, "shulker_vacuum_recipe", BoxType.VACUUM, Material.HOPPER);
        registerRecipe(boxes, "shulker_trash_recipe", BoxType.TRASH, Material.LAVA_BUCKET);

        getServer().getPluginManager().registerEvents(new BoxListener(boxes), this);
        getServer().getPluginManager().registerEvents(new CraftListener(boxes, recipes), this);
        getLogger().info("ShulkerVaccuum enabled");
    }

    @Override
    public void onDisable() {
        recipes.keySet().forEach(Bukkit::removeRecipe);
        recipes.clear();
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
