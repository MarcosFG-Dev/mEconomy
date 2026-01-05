package com.marcosfg.meconomy.commands;

import com.marcosfg.meconomy.Main;
import com.marcosfg.meconomy.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private final Main plugin;

    public MoneyCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole deve especificar player.");
                return true;
            }
            Player p = (Player) sender;
            double bal = plugin.getEconomyManager().getBalance(p);
            p.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.balance")
                    .replace("%symbol%", plugin.getConfig().getString("settings.currency-symbol"))
                    .replace("%amount%", plugin.getEconomyManager().format(bal))));
            return true;
        }

        if (args[0].equalsIgnoreCase("pay")) {
            if (!(sender instanceof Player))
                return true;
            Player p = (Player) sender;

            if (args.length < 3) {
                p.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.invalid-args")));
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.invalid-amount")));
                return true;
            }

            if (amount <= 0) {
                p.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.invalid-amount")));
                return true;
            }

            if (!plugin.getEconomyManager().has(p, amount)) {
                p.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.insufficient-funds")));
                return true;
            }

            plugin.getEconomyManager().withdraw(p, amount);
            plugin.getEconomyManager().deposit(target, amount);

            p.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.paid")
                    .replace("%player%", target.getName())
                    .replace("%symbol%", plugin.getConfig().getString("settings.currency-symbol"))
                    .replace("%amount%", plugin.getEconomyManager().format(amount))));

            if (target.isOnline()) {
                target.getPlayer().sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.received")
                        .replace("%player%", p.getName())
                        .replace("%symbol%", plugin.getConfig().getString("settings.currency-symbol"))
                        .replace("%amount%", plugin.getEconomyManager().format(amount))));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("meconomy.admin")) {
                sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            if (args.length < 3)
                return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            double amount = Double.parseDouble(args[2]);
            plugin.getEconomyManager().setBalance(target, amount);
            sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.admin-set")
                    .replace("%player%", target.getName())
                    .replace("%symbol%", plugin.getConfig().getString("settings.currency-symbol"))
                    .replace("%amount%", plugin.getEconomyManager().format(amount))));
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("meconomy.admin")) {
                sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            if (args.length < 3)
                return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            double amount = Double.parseDouble(args[2]);
            plugin.getEconomyManager().deposit(target, amount);
            sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.admin-give")
                    .replace("%player%", target.getName())
                    .replace("%symbol%", plugin.getConfig().getString("settings.currency-symbol"))
                    .replace("%amount%", plugin.getEconomyManager().format(amount))));
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§a/money");
            sender.sendMessage("§a/money pay <player> <quantia>");
            if (sender.hasPermission("meconomy.admin")) {
                sender.sendMessage("§c/money set <player> <quantia>");
                sender.sendMessage("§c/money give <player> <quantia>");
                sender.sendMessage("§c/money take <player> <quantia>");
                sender.sendMessage("§c/money reload");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("take")) {
            if (!sender.hasPermission("meconomy.admin")) {
                sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            if (args.length < 3)
                return false;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.invalid-amount")));
                return true;
            }
            plugin.getEconomyManager().withdraw(target, amount);
            sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.admin-take")
                    .replace("%player%", target.getName())
                    .replace("%symbol%", plugin.getConfig().getString("settings.currency-symbol"))
                    .replace("%amount%", plugin.getEconomyManager().format(amount))));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("meconomy.admin")) {
                sender.sendMessage(ColorUtils.color(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage("§aConfiguração recarregada com sucesso!");
            return true;
        }

        return true;
    }
}
