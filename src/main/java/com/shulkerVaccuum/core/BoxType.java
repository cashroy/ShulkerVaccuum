package com.shulkerVaccuum.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

/** The two special shulker boxes. Both act on mined drops in the off-hand and on pickups in the main hand. */
public enum BoxType {
    VACUUM("vacuum", "Shulker Box of Vacuum", "Vacuum I",
            "Off-hand: stores blocks you mine",
            "Main hand: stores items you pick up"),
    TRASH("trash", "Shulker Box of Trash", "Trash I",
            "Off-hand: deletes blocks you mine",
            "Main hand: deletes items you pick up");

    final String id;
    private final String name;
    private final String level;
    private final String offHandHint;
    private final String mainHandHint;

    BoxType(String id, String name, String level, String offHandHint, String mainHandHint) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.offHandHint = offHandHint;
        this.mainHandHint = mainHandHint;
    }

    Component displayName() {
        return plain(name, NamedTextColor.GOLD);
    }

    List<Component> lore() {
        return List.of(
                plain(level, NamedTextColor.GRAY),
                plain(offHandHint, NamedTextColor.DARK_GRAY),
                plain(mainHandHint, NamedTextColor.DARK_GRAY));
    }

    private static Component plain(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }
}
