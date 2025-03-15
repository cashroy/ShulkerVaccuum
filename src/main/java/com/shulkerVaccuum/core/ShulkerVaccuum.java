package com.shulkerVaccuum.core;

import com.shulkerVaccuum.handlers.DestroyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public final class ShulkerVaccuum extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("ShulkerVaccuum has been enabled");
        new DestroyHandler(this);
        registerVacuum();
        registerTrash();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("ShulkerVaccuum has been disabled");
    }

    private void registerVacuum() {
        // Create the custom result item (Shulker Box with Vacuum lore)
        ItemStack result = new ItemStack(Material.CYAN_SHULKER_BOX);
        ItemMeta resultMeta = result.getItemMeta();

        if (resultMeta != null) {
            resultMeta.setDisplayName("§6Shulker Box of Vacuum");
            resultMeta.setLore(List.of("§7Vacuum I"));
            resultMeta.getPersistentDataContainer().set(new NamespacedKey("shulker_vacuum", "vacuum"), PersistentDataType.STRING, "true");
            result.setItemMeta(resultMeta);
        }

        // Create the ShapelessRecipe
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "shulker_vacuum_recipe"), result);
        recipe.addIngredient(Material.SHULKER_BOX); // This will cover any of the colored Shulker Boxes
        recipe.addIngredient(Material.HOPPER);      // Hopper

        // Register the recipe with the server
        Bukkit.addRecipe(recipe);
    }

    private void registerTrash() {
        // Create the custom result item (Shulker Box with Trash lore)
        ItemStack result = new ItemStack(Material.RED_SHULKER_BOX);
        ItemMeta resultMeta = result.getItemMeta();

        if (resultMeta != null) {
            resultMeta.setDisplayName("§6Shulker Box of Trash");
            resultMeta.setLore(List.of("§7Trash I"));
            resultMeta.getPersistentDataContainer().set(new NamespacedKey("shulker_trash", "trash"), PersistentDataType.STRING, "true");
            result.setItemMeta(resultMeta);
        }

        // Create the ShapelessRecipe
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "shulker_trash_recipe"), result);
        recipe.addIngredient(Material.SHULKER_BOX); // This will cover any of the colored Shulker Boxes
        recipe.addIngredient(Material.LAVA_BUCKET); // Lava Bucket
        Bukkit.addRecipe(recipe);
    }

    // New method to handle crafting table
    public Inventory handleCraftingTableItems(ItemStack[] craftingItems) {
        ItemStack shulker = null;
        Inventory inv = null;

        // Create an array of ItemStacks and set them to the crafting table items
        ItemStack[] itemsArray = new ItemStack[craftingItems.length];
        for (int i = 0; i < craftingItems.length; i++) {
            itemsArray[i] = craftingItems[i];
        }

        // Look through the items array to find a Shulker Box and set it to 'shulker'
        for (ItemStack item : itemsArray) {
            if (item != null && item.getType() == Material.SHULKER_BOX) {
                shulker = item;
                break;
            }
        }

        // If a Shulker Box is found, do something with it (for example, log it)
        if (shulker != null) {
            getLogger().info("Found Shulker Box: " + shulker.getItemMeta().getDisplayName());
            ShulkerBox shulkerBox = (ShulkerBox) shulker;
            inv = shulkerBox.getInventory();
            return inv;
        } else {
            getLogger().info("No Shulker Box found in crafting table.");
            return inv;
        }
    }
    public boolean isShulkerBox(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        // Check if the item's material is any Shulker Box
        if (item.getType().name().endsWith("_SHULKER_BOX")) {
            return true;
        }

        // Check if the item has BlockStateMeta and its state is a ShulkerBox
        if (item.getItemMeta() instanceof BlockStateMeta meta) {
            return meta.getBlockState() instanceof ShulkerBox;
        }

        return false;
    }
}