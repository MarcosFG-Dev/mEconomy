package com.marcosfg.meconomy.commands;

import com.marcosfg.meconomy.Main;
import com.marcosfg.meconomy.gui.MoneyMenu;
import com.marcosfg.meconomy.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private final Main plugin;
    private final MoneyMenu moneyMenu;

    public MoneyCommand(Main plugin, MoneyMenu moneyMenu) {
        this.plugin = plugin;
        this.moneyMenu = moneyMenu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return handleMenu(sender);
        }

        switch (args[0].toLowerCase()) {
            case "menu":
            case "gui":
            case "painel":
                return handleMenu(sender);
            case "saldo":
            case "balance":
            case "bal":
                return handleSelfBalance(sender);
            case "pay":
            case "pagar":
                return handlePay(sender, args);
            case "set":
                return handleAdminSet(sender, args);
            case "give":
                return handleAdminGive(sender, args);
            case "take":
                return handleAdminTake(sender, args);
            case "reload":
                return handleReload(sender);
            case "help":
            case "ajuda":
                return handleHelp(sender);
            default:
                return handleOtherBalance(sender, args[0]);
        }
    }

    private boolean handleMenu(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cConsole deve especificar player ou usar comandos administrativos.");
            sender.sendMessage("§a/money <jogador>");
            sender.sendMessage("§a/money set <jogador> <quantia>");
            sender.sendMessage("§a/money give <jogador> <quantia>");
            sender.sendMessage("§a/money take <jogador> <quantia>");
            return true;
        }

        moneyMenu.openMain((Player) sender);
        return true;
    }

    private boolean handleSelfBalance(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cConsole deve especificar player.");
            return true;
        }

        Player player = (Player) sender;
        double balance = plugin.getEconomyManager().getBalance(player);
        player.sendMessage(colorMessage("messages.balance")
                .replace("%symbol%", currencySymbol())
                .replace("%amount%", plugin.getEconomyManager().format(balance)));
        return true;
    }

    private boolean handleOtherBalance(CommandSender sender, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            sender.sendMessage(colorMessage("messages.player-not-found"));
            return true;
        }

        double balance = plugin.getEconomyManager().getBalance(target);
        sender.sendMessage(colorMessage("messages.balance-other")
                .replace("%player%", safeName(target))
                .replace("%symbol%", currencySymbol())
                .replace("%amount%", plugin.getEconomyManager().format(balance)));
        return true;
    }

    private boolean handlePay(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 3) {
            player.sendMessage(colorMessage("messages.invalid-args"));
            player.sendMessage(ColorUtils.color("&7Dica: use &a/money menu &7para pagar pelo menu."));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if ((!target.isOnline() && !target.hasPlayedBefore()) || target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(colorMessage("messages.player-not-found"));
            return true;
        }

        Double amount = parsePositiveAmount(args[2]);
        if (amount == null) {
            player.sendMessage(colorMessage("messages.invalid-amount"));
            return true;
        }

        if (!plugin.getEconomyManager().has(player, amount)) {
            player.sendMessage(colorMessage("messages.insufficient-funds"));
            return true;
        }

        plugin.getEconomyManager().withdraw(player, amount);
        plugin.getEconomyManager().deposit(target, amount);

        player.sendMessage(colorMessage("messages.paid")
                .replace("%player%", safeName(target))
                .replace("%symbol%", currencySymbol())
                .replace("%amount%", plugin.getEconomyManager().format(amount)));

        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(colorMessage("messages.received")
                    .replace("%player%", player.getName())
                    .replace("%symbol%", currencySymbol())
                    .replace("%amount%", plugin.getEconomyManager().format(amount)));
        }
        return true;
    }

    private boolean handleAdminSet(CommandSender sender, String[] args) {
        if (!checkAdminPermission(sender)) return true;
        if (args.length < 3) {
            sender.sendMessage(colorMessage("messages.invalid-args"));
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            sender.sendMessage(colorMessage("messages.player-not-found"));
            return true;
        }

        Double amount = parseNonNegativeAmount(args[2]);
        if (amount == null) {
            sender.sendMessage(colorMessage("messages.invalid-amount"));
            return true;
        }

        plugin.getEconomyManager().setBalance(target, amount);
        sender.sendMessage(colorMessage("messages.admin-set")
                .replace("%player%", safeName(target))
                .replace("%symbol%", currencySymbol())
                .replace("%amount%", plugin.getEconomyManager().format(amount)));
        return true;
    }

    private boolean handleAdminGive(CommandSender sender, String[] args) {
        if (!checkAdminPermission(sender)) return true;
        if (args.length < 3) {
            sender.sendMessage(colorMessage("messages.invalid-args"));
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            sender.sendMessage(colorMessage("messages.player-not-found"));
            return true;
        }

        Double amount = parsePositiveAmount(args[2]);
        if (amount == null) {
            sender.sendMessage(colorMessage("messages.invalid-amount"));
            return true;
        }

        plugin.getEconomyManager().deposit(target, amount);
        sender.sendMessage(colorMessage("messages.admin-give")
                .replace("%player%", safeName(target))
                .replace("%symbol%", currencySymbol())
                .replace("%amount%", plugin.getEconomyManager().format(amount)));
        return true;
    }

    private boolean handleAdminTake(CommandSender sender, String[] args) {
        if (!checkAdminPermission(sender)) return true;
        if (args.length < 3) {
            sender.sendMessage(colorMessage("messages.invalid-args"));
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            sender.sendMessage(colorMessage("messages.player-not-found"));
            return true;
        }

        Double amount = parsePositiveAmount(args[2]);
        if (amount == null) {
            sender.sendMessage(colorMessage("messages.invalid-amount"));
            return true;
        }

        plugin.getEconomyManager().withdraw(target, amount);
        sender.sendMessage(colorMessage("messages.admin-take")
                .replace("%player%", safeName(target))
                .replace("%symbol%", currencySymbol())
                .replace("%amount%", plugin.getEconomyManager().format(amount)));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!checkAdminPermission(sender)) {
            return true;
        }
        plugin.reloadConfig();
        sender.sendMessage("§aConfiguração recarregada com sucesso!");
        return true;
    }

    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage("§a/money §7- Abre o menu de economia");
        sender.sendMessage("§a/money saldo §7- Mostra seu saldo no chat");
        sender.sendMessage("§a/money <jogador> §7- Mostra o saldo de outro jogador");
        sender.sendMessage("§a/money pay <player> <quantia> §7- Envia dinheiro");
        sender.sendMessage("§a/money menu §7- Abre o menu GUI profissional");
        if (sender.hasPermission("meconomy.admin")) {
            sender.sendMessage("§c/money set <player> <quantia>");
            sender.sendMessage("§c/money give <player> <quantia>");
            sender.sendMessage("§c/money take <player> <quantia>");
            sender.sendMessage("§c/money reload");
        }
        return true;
    }

    private boolean checkAdminPermission(CommandSender sender) {
        if (!sender.hasPermission("meconomy.admin")) {
            sender.sendMessage(colorMessage("messages.no-permission"));
            return false;
        }
        return true;
    }

    private Double parsePositiveAmount(String raw) {
        try {
            double value = Double.parseDouble(raw.replace(",", "."));
            return value > 0 ? value : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Double parseNonNegativeAmount(String raw) {
        try {
            double value = Double.parseDouble(raw.replace(",", "."));
            return value >= 0 ? value : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String colorMessage(String path) {
        return ColorUtils.color(plugin.getConfig().getString(path, ""));
    }

    private String currencySymbol() {
        return plugin.getConfig().getString("settings.currency-symbol", "$" );
    }

    private String safeName(OfflinePlayer player) {
        return player.getName() != null ? player.getName() : "Desconhecido";
    }
}
