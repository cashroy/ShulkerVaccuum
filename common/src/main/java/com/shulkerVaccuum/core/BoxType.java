package com.shulkerVaccuum.core;

import org.bukkit.ChatColor;

import java.util.List;

/** The two special shulker boxes. Both act on mined drops in the off-hand and on pickups in the main hand. */
public enum BoxType {
    VACUUM("vacuum", "vacuum_box", "Shulker Box of Vacuum", "Vacuum I",
            "Off-hand: stores blocks you mine",
            "Main hand: stores items you pick up"),
    TRASH("trash", "trash_box", "Shulker Box of Trash", "Trash I",
            "Off-hand: deletes blocks you mine",
            "Main hand: deletes items you pick up");

    final String id;
    /** Item model in the resource pack: assets/shulkervaccuum/items/<model>.json */
    final String model;
    private final String name;
    private final String level;
    private final String offHandHint;
    private final String mainHandHint;

    BoxType(String id, String model, String name, String level, String offHandHint, String mainHandHint) {
        this.id = id;
        this.model = model;
        this.name = name;
        this.level = level;
        this.offHandHint = offHandHint;
        this.mainHandHint = mainHandHint;
    }

    /** Base name of this box's models in the resource pack, e.g. "vacuum_box". */
    public String modelName() {
        return model;
    }

    // Plain Bukkit text (works on Spigot as well as Paper). RESET first so the name isn't italic.
    String displayName() {
        return plain(name, ChatColor.GOLD);
    }

    List<String> lore() {
        return List.of(
                plain(level, ChatColor.GRAY),
                plain(offHandHint, ChatColor.DARK_GRAY),
                plain(mainHandHint, ChatColor.DARK_GRAY));
    }

    private static String plain(String text, ChatColor color) {
        return ChatColor.RESET.toString() + color + text;
    }
}
