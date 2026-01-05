package com.marcosfg.meconomy.commands;

import com.marcosfg.meconomy.Main;
import com.marcosfg.meconomy.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class MagnataCommand implements CommandExecutor {

    private final Main plugin;

    public MagnataCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int limit = plugin.getConfig().getInt("top-balance.limit", 10);

        sender.sendMessage("§2§lTOP " + limit + " §aMais Ricos:");

        Map<String, Double> top = plugin.getEconomyManager().getTopBalance(limit);

        int pos = 1;
        String format = plugin.getConfig().getString("top-balance.format", "&7%pos%º &f%player% &7- &2%balance%");

        for (Map.Entry<String, Double> entry : top.entrySet()) {
            String msg = format
                    .replace("%pos%", String.valueOf(pos))
                    .replace("%player%", entry.getKey())
                    .replace("%balance%", plugin.getEconomyManager().format(entry.getValue()));

            sender.sendMessage(ColorUtils.color(msg));
            pos++;
        }

        return true;
    }
}
