package com.shulkerVaccuum.handlers;

import com.shulkerVaccuum.core.ShulkerVaccuum;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class RecipeHandler implements Listener {

    @EventHandler
    public void onCraft(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING) {
            ItemStack result = event.getInventory().getItem(event.getSlot());

            // Check if the result is a Shulker Box
            if (result != null && result.getType() == Material.CYAN_SHULKER_BOX) {
                // Get the inventory of the original Shulker Box
                ItemStack[] contents = null;
                for (ItemStack item : event.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.SHULKER_BOX) {
                        // Get the original Shulker Box contents
                        if (item.hasItemMeta()) {
                            ShulkerBox originalShulkerBox = (ShulkerBox) item;
                            contents = originalShulkerBox.getInventory().getContents();
                            break;
                        }
                    }
                }

                // Transfer the contents from the original Shulker Box to the new one
                if (contents != null) {
                    ShulkerBox newShulkerBox = (ShulkerBox) result;
                    Inventory inv = newShulkerBox.getInventory();
                    for (ItemStack item : contents) {
                        if (item != null) {
                            inv.addItem(item);
                        }
                    }
                }
            }
        }
    }
}