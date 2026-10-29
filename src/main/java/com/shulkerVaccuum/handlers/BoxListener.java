package com.shulkerVaccuum.handlers;

import com.shulkerVaccuum.core.BoxType;
import com.shulkerVaccuum.core.SpecialBoxes;
import org.bukkit.Sound;
import org.bukkit.block.Container;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Iterator;
import java.util.Map;

/**
 * Off-hand box: acts on the drops of blocks you mine.
 * Main-hand box: acts on items you pick up.
 * Vacuum stores them (anything that doesn't fit goes to your inventory as normal); Trash deletes them.
 * Shulker boxes are never stored or deleted, and blocks that hold items (chests, barrels…) are left alone,
 * so a box in your hand can't swallow or destroy a container's contents.
 */
public final class BoxListener implements Listener {
    private final SpecialBoxes boxes;

    public BoxListener(SpecialBoxes boxes) {
        this.boxes = boxes;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockDrops(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack box = inventory.getItemInOffHand();
        BoxType type = boxes.typeOf(box);
        if (type == null) return;
        if (boxes.applyModel(box, type)) inventory.setItemInOffHand(box);
        if (event.getBlockState() instanceof Container) return;

        boolean handled = false;
        Iterator<Item> drops = event.getItems().iterator();
        while (drops.hasNext()) {
            Item drop = drops.next();
            ItemStack stack = drop.getItemStack();
            if (SpecialBoxes.isShulkerBox(stack)) continue;

            if (type == BoxType.TRASH) {
                drops.remove();
                handled = true;
                continue;
            }

            ItemStack leftover = SpecialBoxes.storeIn(box, stack);
            if (leftover == null) {
                drops.remove();
                handled = true;
                continue;
            }
            if (leftover.getAmount() != stack.getAmount()) handled = true;
            // Box is full: give the rest to the player; whatever still doesn't fit drops as usual.
            Map<Integer, ItemStack> noRoom = inventory.addItem(leftover);
            if (noRoom.isEmpty()) {
                drops.remove();
            } else {
                drop.setItemStack(noRoom.values().iterator().next());
            }
        }

        if (handled) {
            inventory.setItemInOffHand(box);
            feedback(player, type);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerInventory inventory = player.getInventory();
        ItemStack box = inventory.getItemInMainHand();
        BoxType type = boxes.typeOf(box);
        if (type == null) return;
        if (boxes.applyModel(box, type)) inventory.setItemInMainHand(box);

        Item entity = event.getItem();
        ItemStack stack = entity.getItemStack();
        if (SpecialBoxes.isShulkerBox(stack)) return;

        if (type == BoxType.TRASH) {
            event.setCancelled(true);
            entity.remove();
            feedback(player, type);
            return;
        }

        ItemStack leftover = SpecialBoxes.storeIn(box, stack);
        // Box full: let the normal pickup put it in the player's inventory.
        if (leftover != null && leftover.getAmount() == stack.getAmount()) return;

        event.setCancelled(true);
        inventory.setItemInMainHand(box);
        if (leftover == null) {
            entity.remove();
        } else {
            // Partly stored; the rest stays on the ground and is picked up normally next tick.
            entity.setItemStack(leftover);
        }
        feedback(player, type);
    }

    /** Upgrade boxes crafted before the textures existed, so they show the custom model. */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack[] items = inventory.getContents();
        for (int slot = 0; slot < items.length; slot++) {
            BoxType type = boxes.typeOf(items[slot]);
            if (type != null && boxes.applyModel(items[slot], type)) inventory.setItem(slot, items[slot]);
        }
    }

    private static void feedback(Player player, BoxType type) {
        if (type == BoxType.TRASH) {
            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.15f, 1.8f);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2f, 1.6f);
        }
    }
}
