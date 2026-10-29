package com.shulkerVaccuum.handlers;

import com.shulkerVaccuum.core.BoxType;
import com.shulkerVaccuum.core.PlacedSkins;
import com.shulkerVaccuum.core.SpecialBoxes;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Keeps placed Vacuum/Trash boxes special: remembers the type on the block, gives broken or
 * exploded boxes back their type, and adds or removes the placed-box look.
 */
public final class PlacedBoxListener implements Listener {
    private final Plugin plugin;
    private final SpecialBoxes boxes;
    private final PlacedSkins skins;

    public PlacedBoxListener(Plugin plugin, SpecialBoxes boxes, PlacedSkins skins) {
        this.plugin = plugin;
        this.boxes = boxes;
        this.skins = skins;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        BoxType type = boxes.typeOf(event.getItemInHand());
        if (type == null) return;
        Block block = event.getBlockPlaced();
        // One tick later, once the game has finished copying the item's data onto the block.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!isShulkerBlock(block)) return;
            boxes.markPlaced(block, type);
            skins.show(block, type);
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (boxes.placedType(event.getBlock().getState()) != null) skins.remove(event.getBlock());
    }

    /** Broken by a player: make sure the dropped box is still a Vacuum/Trash box. */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDrops(BlockDropItemEvent event) {
        BoxType type = boxes.placedType(event.getBlockState());
        if (type == null) return;
        for (Item drop : event.getItems()) {
            ItemStack stack = drop.getItemStack();
            if (SpecialBoxes.isShulkerBox(stack) && boxes.typeOf(stack) != type) drop.setItemStack(boxes.mark(stack, type));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    /** Pistons break shulker boxes rather than moving them; pop special ones out ourselves. */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) popOutIfSpecial(block);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) popOutIfSpecial(block);
    }

    private void handleExplosion(List<Block> blocks) {
        blocks.removeIf(this::popOutIfSpecial);
    }

    /**
     * If this is a placed Vacuum/Trash box, drop it as an item (keeping its type and contents),
     * remove its look and clear the block. Returns true if it did.
     */
    private boolean popOutIfSpecial(Block block) {
        BlockState state = block.getState();
        BoxType type = boxes.placedType(state);
        if (type == null) return false;
        skins.remove(block);
        // Build the dropped item straight from the block, contents and all.
        ItemStack item = new ItemStack(block.getType());
        if (item.getItemMeta() instanceof BlockStateMeta meta) {
            meta.setBlockState(state);
            item.setItemMeta(meta);
        }
        block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), boxes.mark(item, type));
        block.setType(Material.AIR, false);
        return true;
    }

    private static boolean isShulkerBlock(Block block) {
        return Tag.SHULKER_BOXES.isTagged(block.getType());
    }
}
