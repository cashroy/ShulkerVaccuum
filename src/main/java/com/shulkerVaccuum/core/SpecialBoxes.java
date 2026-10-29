package com.shulkerVaccuum.core;

import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Map;

/** Recognises, creates and fills Vacuum and Trash shulker boxes. */
public final class SpecialBoxes {
    // Tags written by version 1 of the plugin, so boxes crafted back then keep working.
    private static final NamespacedKey LEGACY_VACUUM = new NamespacedKey("shulker_vacuum", "vacuum");
    private static final NamespacedKey LEGACY_TRASH = new NamespacedKey("shulker_trash", "trash");

    // Custom item models only exist on Minecraft 1.21.4+, so they're looked up at runtime.
    // On older servers these are null and the boxes keep the normal shulker box look.
    private static final Method GET_ITEM_MODEL = itemMetaMethod("getItemModel");
    private static final Method SET_ITEM_MODEL = itemMetaMethod("setItemModel", NamespacedKey.class);

    private final Plugin plugin;
    private final NamespacedKey typeKey;

    public SpecialBoxes(Plugin plugin) {
        this.plugin = plugin;
        this.typeKey = new NamespacedKey(plugin, "box_type");
    }

    /** The resource-pack model for a box type, e.g. shulkervaccuum:vacuum_box. */
    private NamespacedKey modelKey(BoxType type) {
        return new NamespacedKey(plugin, type.model);
    }

    /**
     * Gives a box its custom model if it doesn't have it yet (boxes made before textures existed).
     * Returns true if the item was changed.
     */
    public boolean applyModel(ItemStack box, BoxType type) {
        if (SET_ITEM_MODEL == null || GET_ITEM_MODEL == null) return false;
        ItemMeta meta = box.getItemMeta();
        if (meta == null || modelKey(type).equals(invoke(GET_ITEM_MODEL, meta))) return false;
        invoke(SET_ITEM_MODEL, meta, modelKey(type));
        box.setItemMeta(meta);
        return true;
    }

    private static Method itemMetaMethod(String name, Class<?>... parameters) {
        try {
            return ItemMeta.class.getMethod(name, parameters);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Object invoke(Method method, ItemMeta meta, Object... arguments) {
        try {
            return method.invoke(meta, arguments);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    public static boolean isShulkerBox(ItemStack item) {
        return item != null && Tag.SHULKER_BOXES.isTagged(item.getType());
    }

    /** The special type of this item, or null if it's anything else (including an ordinary shulker box). */
    public BoxType typeOf(ItemStack item) {
        if (!isShulkerBox(item) || !item.hasItemMeta()) return null;
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        String saved = data.get(typeKey, PersistentDataType.STRING);
        if (saved != null) {
            for (BoxType type : BoxType.values()) {
                if (type.id.equals(saved)) return type;
            }
        }
        if (data.has(LEGACY_VACUUM, PersistentDataType.STRING)) return BoxType.VACUUM;
        if (data.has(LEGACY_TRASH, PersistentDataType.STRING)) return BoxType.TRASH;
        return null;
    }

    /** A copy of {@code box} (same colour, same contents) turned into the given special box. */
    public ItemStack mark(ItemStack box, BoxType type) {
        ItemStack result = box.clone();
        result.setAmount(1);
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return result;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.remove(LEGACY_VACUUM);
        data.remove(LEGACY_TRASH);
        data.set(typeKey, PersistentDataType.STRING, type.id);
        meta.setDisplayName(type.displayName());
        meta.setLore(type.lore());
        result.setItemMeta(meta);
        applyModel(result, type);
        return result;
    }

    /**
     * Puts as much of {@code stack} into {@code box} as fits, updating the box item in place.
     * Returns what didn't fit, or null if all of it did. Shulker boxes are never stored,
     * matching vanilla, where a shulker box can't hold another.
     */
    public static ItemStack storeIn(ItemStack box, ItemStack stack) {
        if (isShulkerBox(stack)) return stack;
        if (!(box.getItemMeta() instanceof BlockStateMeta meta) || !(meta.getBlockState() instanceof ShulkerBox shulker)) {
            return stack;
        }
        Map<Integer, ItemStack> leftover = shulker.getSnapshotInventory().addItem(stack.clone());
        meta.setBlockState(shulker);
        box.setItemMeta(meta);
        return leftover.isEmpty() ? null : leftover.values().iterator().next();
    }
}
