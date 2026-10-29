package com.shulkerVaccuum.core;

import org.bukkit.block.Block;

/**
 * The custom look for placed Vacuum/Trash boxes. The real implementation lives in the skins
 * module and is only loaded on servers that support it (1.21.4+); everywhere else it's NONE.
 */
public interface PlacedSkins {
    /** Put the custom look on a freshly placed special box. */
    void show(Block block, BoxType type);

    /** Take the custom look off a box that's being broken or destroyed. */
    void remove(Block block);

    PlacedSkins NONE = new PlacedSkins() {
        @Override
        public void show(Block block, BoxType type) {
        }

        @Override
        public void remove(Block block) {
        }
    };
}
