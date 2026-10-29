package com.shulkerVaccuum.handlers;

import com.shulkerVaccuum.core.BoxType;
import com.shulkerVaccuum.core.SpecialBoxes;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Map;

/**
 * Makes the crafted box keep the colour and contents of the box you put in,
 * and adds the recipes to each player's recipe book.
 */
public final class CraftListener implements Listener {
    private final SpecialBoxes boxes;
    private final Map<NamespacedKey, BoxType> recipes;

    public CraftListener(SpecialBoxes boxes, Map<NamespacedKey, BoxType> recipes) {
        this.boxes = boxes;
        this.recipes = recipes;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof Keyed keyed)) return;
        BoxType type = recipes.get(keyed.getKey());
        if (type == null) return;

        CraftingInventory grid = event.getInventory();
        ItemStack input = null;
        for (ItemStack item : grid.getMatrix()) {
            if (SpecialBoxes.isShulkerBox(item)) {
                input = item;
                break;
            }
        }
        // Converting a Vacuum box into a Trash box (or back) is fine; re-making the same type is not.
        if (input == null || boxes.typeOf(input) == type) {
            grid.setResult(null);
            return;
        }
        grid.setResult(boxes.mark(input, type));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipes(recipes.keySet());
    }
}
