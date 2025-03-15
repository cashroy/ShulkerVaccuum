package com.shulkerVaccuum.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("test")) {
            if (sender instanceof Player){
                Player player = (Player) sender;
                player.sendMessage(ChatColor.GREEN + "You are using a test");
            }
            else{
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
            }
        }
        return false;
    }
}
