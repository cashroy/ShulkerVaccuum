package com.shulkerVaccuum.skins;

import com.shulkerVaccuum.core.BoxType;
import com.shulkerVaccuum.core.PlacedSkins;
import com.shulkerVaccuum.core.SpecialBoxes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

/**
 * The custom look for placed Vacuum/Trash boxes: two item displays (base and lid) sitting just
 * outside the real shulker box, so it's hidden but still does all the real work (storage, hoppers,
 * mining). When someone opens the box, the custom lid lifts and twists like a shulker lid.
 */
final class DisplaySkins implements PlacedSkins, Listener {
    private static final String BASE = "base";
    private static final String LID = "lid";
    /** Slightly bigger than a block, so the custom model covers the real box without flickering. */
    private static final float SCALE = 1.01f;
    /** Same as the vanilla shulker lid: 10 ticks to open, rising half a block while turning 270 degrees. */
    private static final int OPEN_TICKS = 10;

    private final Plugin plugin;
    private final SpecialBoxes boxes;
    private final NamespacedKey partKey;

    DisplaySkins(Plugin plugin, SpecialBoxes boxes) {
        this.plugin = plugin;
        this.boxes = boxes;
        this.partKey = new NamespacedKey(plugin, "skin_part");
    }

    @Override
    public void show(Block block, BoxType type) {
        remove(block);
        BlockFace facing = facing(block);
        spawnPart(block, type, BASE, facing);
        spawnPart(block, type, LID, facing);
    }

    @Override
    public void remove(Block block) {
        for (ItemDisplay part : parts(block)) part.remove();
    }

    // Lid animation follows the real box: open when the first person opens it, close when the last one leaves.

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onOpen(InventoryOpenEvent event) {
        Block block = specialBlock(event.getInventory());
        if (block != null) animate(block, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Block block = specialBlock(inventory);
        if (block == null) return;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (inventory.getViewers().isEmpty()) animate(block, false);
        });
    }

    /** Tidy up any look left behind by a box removed some other way (commands, other plugins). */
    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (!(entity instanceof ItemDisplay display) || part(display) == null) continue;
            if (boxes.placedType(display.getLocation().getBlock().getState()) == null) display.remove();
        }
    }

    private Block specialBlock(Inventory inventory) {
        if (inventory.getType() != InventoryType.SHULKER_BOX) return null;
        Location location = inventory.getLocation();
        if (location == null) return null;
        Block block = location.getBlock();
        return boxes.placedType(block.getState()) != null ? block : null;
    }

    private void animate(Block block, boolean open) {
        BlockFace facing = facing(block);
        for (ItemDisplay part : parts(block)) {
            part.setBrightness(light(block, facing));
            if (!LID.equals(part(part))) continue;
            part.setInterpolationDelay(0);
            part.setInterpolationDuration(OPEN_TICKS);
            part.setTransformation(transformation(facing, open));
        }
    }

    private void spawnPart(Block block, BoxType type, String part, BlockFace facing) {
        ItemStack model = new ItemStack(Material.PAPER);
        ItemMeta meta = model.getItemMeta();
        meta.setItemModel(new NamespacedKey(plugin, type.modelName() + "_" + part));
        model.setItemMeta(meta);
        block.getWorld().spawn(center(block), ItemDisplay.class, display -> {
            display.setItemStack(model);
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
            display.setTransformation(transformation(facing, false));
            display.setBrightness(light(block, facing));
            display.setPersistent(true);
            display.getPersistentDataContainer().set(partKey, PersistentDataType.STRING, part);
        });
    }

    private List<ItemDisplay> parts(Block block) {
        return block.getWorld().getNearbyEntities(center(block), 0.3, 0.3, 0.3,
                        entity -> entity instanceof ItemDisplay display && part(display) != null)
                .stream().map(ItemDisplay.class::cast).toList();
    }

    private String part(ItemDisplay display) {
        return display.getPersistentDataContainer().get(partKey, PersistentDataType.STRING);
    }

    private static Location center(Block block) {
        return block.getLocation().add(0.5, 0.5, 0.5);
    }

    /** Which way the box's lid points (shulker boxes can be placed on any side). */
    private static BlockFace facing(Block block) {
        return block.getBlockData() instanceof Directional directional ? directional.getFacing() : BlockFace.UP;
    }

    /** Light the model like the space in front of the lid; the inside of a block would be dark. */
    private static Display.Brightness light(Block block, BlockFace facing) {
        Block outside = block.getRelative(facing);
        return new Display.Brightness(outside.getLightFromBlocks(), outside.getLightFromSky());
    }

    private static Transformation transformation(BlockFace facing, boolean open) {
        Vector3f direction = new Vector3f(facing.getModX(), facing.getModY(), facing.getModZ());
        Quaternionf orientation = new Quaternionf().rotationTo(new Vector3f(0, 1, 0), direction);
        Vector3f lift = open ? new Vector3f(direction).mul(0.5f) : new Vector3f();
        Quaternionf twist = open ? new Quaternionf().rotationY((float) Math.toRadians(270)) : new Quaternionf();
        return new Transformation(lift, orientation, new Vector3f(SCALE), twist);
    }
}
