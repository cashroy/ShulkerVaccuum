package com.shulkerVaccuum.skins;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Sends the ShulkerVaccuum texture pack to players as they join. It's added alongside any other
 * server resource pack (for example The Sift's), rather than replacing it.
 */
final class PackSender implements Listener {
    private static final UUID PACK_ID = UUID.nameUUIDFromBytes("ShulkerVaccuum:pack".getBytes(StandardCharsets.UTF_8));

    private final boolean send;
    private final boolean required;
    private final String url;
    private final byte[] hash;
    private final String prompt;

    PackSender(JavaPlugin plugin) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("resource-pack");
        String sha1 = config == null ? "" : config.getString("sha1", "");
        this.url = config == null ? "" : config.getString("url", "");
        this.required = config != null && config.getBoolean("required", false);
        this.prompt = config == null ? null : config.getString("prompt");
        this.hash = sha1 != null && sha1.matches("[0-9a-fA-F]{40}") ? HexFormat.of().parseHex(sha1) : null;
        this.send = config != null && config.getBoolean("send", true) && url != null && !url.isBlank() && hash != null;
        if (config != null && config.getBoolean("send", true) && !send) {
            plugin.getLogger().warning("resource-pack.send is on, but resource-pack.url or resource-pack.sha1 is missing or invalid; not sending the pack.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (send) event.getPlayer().addResourcePack(PACK_ID, url, hash, prompt, required);
    }
}
