package com.shulkerVaccuum.handlers;

import com.shulkerVaccuum.core.ShulkerVaccuum;
import com.sun.tools.jconsole.JConsolePlugin;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DestroyHandler implements Listener {
    public DestroyHandler(ShulkerVaccuum plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> drops = new ArrayList<>(event.getBlock().getDrops(player.getInventory().getItemInMainHand()));
        Inventory inventory = player.getInventory();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check if the item in the offhand is a Shulker Box

        if (offHandItem.getItemMeta().hasLore()) {
            String lore = offHandItem.getItemMeta().getLore().toString();
            // Check for "vacuum" in the lore (case-insensitive)
            boolean hasVacuum = lore.toLowerCase().contains("vacuum");
            boolean hasTrash = lore.toLowerCase().contains("trash");

            NamespacedKey vacuumKey = new NamespacedKey("shulker_vacuum", "vacuum");
            NamespacedKey trashKey = new NamespacedKey("shulker_trash", "trash");

            if (offHandItem.getPersistentDataContainer().has(vacuumKey, PersistentDataType.STRING)) hasVacuum = true;
            if (offHandItem.getPersistentDataContainer().has(trashKey, PersistentDataType.STRING)) hasTrash = true;
            if (hasVacuum) {
                event.setDropItems(false);  // Prevent drops
                if (offHandItem.getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta im = (BlockStateMeta) offHandItem.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        Inventory inv = Bukkit.createInventory(null, 27, "Shulker Box");
                        inv.setContents(shulker.getInventory().getContents());

                        // Add the block drops to the Shulker Box
                        for (ItemStack drop : drops) {
                            inv.addItem(drop);
                        }

                        shulker.getInventory().setContents(inv.getContents());
                        im.setBlockState(shulker);
                        offHandItem.setItemMeta(im);  // Set the updated Shulker Box to offhand
                    }
                }
            } else if (hasTrash) {
                event.setDropItems(false);  // Prevent drops if "trash" is found in the lore
            }
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

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        // Check if the entity is a player
        if (event.getEntity() instanceof Player player) {
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            ItemStack drop = event.getItem().getItemStack(); // The Item entity being picked up
            Inventory pinv = player.getInventory();

            // Check if the item is a Shulker Box
            if (isShulkerBox(offHandItem)) {
                ItemMeta meta = offHandItem.getItemMeta();
                if (meta == null) return;

                // Check for "vacuum" or "trash" tags
                boolean hasVacuum = meta.getPersistentDataContainer().has(new NamespacedKey("shulker_vacuum", "vacuum"), PersistentDataType.STRING);
                boolean hasTrash = meta.getPersistentDataContainer().has(new NamespacedKey("shulker_trash", "trash"), PersistentDataType.STRING);

                // If it's a Vacuum Shulker Box, add the picked up items to the Shulker Box
                if (hasVacuum) {
                    if (offHandItem.getItemMeta() instanceof BlockStateMeta) {
                        BlockStateMeta im = (BlockStateMeta) offHandItem.getItemMeta();
                        if (im.getBlockState() instanceof ShulkerBox) {
                            ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                            Inventory inv = Bukkit.createInventory(null, 27, "Shulker Box");
                            inv.setContents(shulker.getInventory().getContents());
                            inv.addItem(drop);
                            shulker.getInventory().setContents(inv.getContents());
                            im.setBlockState(shulker);
                            offHandItem.setItemMeta(im);  // Set the updated Shulker Box to offhand
                            event.getItem().remove();
                            event.setCancelled(true);
                        }
                    }
                }
                // If it's a Trash Shulker Box, delete the picked item
                else if (hasTrash) {
                    pinv.removeItem(drop);; // Cancel the default item pickup
                    player.sendMessage(ChatColor.RED + "Item discarded in Trash Shulker!");
                }
            }
        }
    }
}