package com.marcosfg.meconomy.commands;

import com.marcosfg.meconomy.Main;
import com.marcosfg.meconomy.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class MagnataCommand implements CommandExecutor {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 100;

    private static final String CONFIG_LIMIT = "top-balance.limit";
    private static final String CONFIG_MAX_LIMIT = "top-balance.max-limit";
    private static final String CONFIG_PERMISSION = "top-balance.permission";
    private static final String CONFIG_HEADER = "top-balance.header";
    private static final String CONFIG_FORMAT = "top-balance.format";
    private static final String CONFIG_EMPTY = "top-balance.empty";
    private static final String CONFIG_INVALID_LIMIT = "top-balance.invalid-limit";
    private static final String CONFIG_NO_PERMISSION = "top-balance.no-permission";

    private static final String DEFAULT_PERMISSION = "";
    private static final String DEFAULT_HEADER = "&2&lTOP %limit% &aMais Ricos:";
    private static final String DEFAULT_FORMAT = "&7%pos%º &f%player% &7- &2%balance%";
    private static final String DEFAULT_EMPTY = "&cNenhum jogador encontrado no ranking.";
    private static final String DEFAULT_INVALID_LIMIT = "&cUse /%label% [quantidade] com um número entre %min% e %max%.";
    private static final String DEFAULT_NO_PERMISSION = "&cVocê não tem permissão para usar este comando.";

    private final Main plugin;

    public MagnataCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            send(sender, getMessage(CONFIG_NO_PERMISSION, DEFAULT_NO_PERMISSION));
            return true;
        }

        int maxLimit = getConfiguredMaxLimit();
        Integer requestedLimit = parseLimit(sender, label, args, maxLimit);

        if (requestedLimit == null) {
            return true;
        }

        Map<String, Double> topBalances = plugin.getEconomyManager().getTopBalance(requestedLimit);

        if (topBalances == null || topBalances.isEmpty()) {
            send(sender, getMessage(CONFIG_EMPTY, DEFAULT_EMPTY));
            return true;
        }

        send(sender, applyPlaceholders(
                getMessage(CONFIG_HEADER, DEFAULT_HEADER),
                0,
                "",
                0.0D,
                requestedLimit,
                maxLimit,
                label
        ));

        int position = 1;
        String lineFormat = getMessage(CONFIG_FORMAT, DEFAULT_FORMAT);

        for (Map.Entry<String, Double> entry : topBalances.entrySet()) {
            send(sender, applyPlaceholders(
                    lineFormat,
                    position,
                    entry.getKey(),
                    entry.getValue(),
                    requestedLimit,
                    maxLimit,
                    label
            ));
            position++;
        }

        return true;
    }

    private boolean hasPermission(CommandSender sender) {
        String permission = plugin.getConfig().getString(CONFIG_PERMISSION, DEFAULT_PERMISSION);

        if (permission == null || permission.trim().isEmpty()) {
            return true;
        }

        return sender.hasPermission(permission.trim());
    }

    private Integer parseLimit(CommandSender sender, String label, String[] args, int maxLimit) {
        int configuredLimit = plugin.getConfig().getInt(CONFIG_LIMIT, DEFAULT_LIMIT);
        int limit = clamp(configuredLimit, MIN_LIMIT, maxLimit);

        if (args.length == 0) {
            return limit;
        }

        if (args.length > 1) {
            sendInvalidLimit(sender, label, maxLimit);
            return null;
        }

        try {
            return clamp(Integer.parseInt(args[0]), MIN_LIMIT, maxLimit);
        } catch (NumberFormatException exception) {
            sendInvalidLimit(sender, label, maxLimit);
            return null;
        }
    }

    private int getConfiguredMaxLimit() {
        int configuredMaxLimit = plugin.getConfig().getInt(CONFIG_MAX_LIMIT, MAX_LIMIT);
        return Math.max(MIN_LIMIT, configuredMaxLimit);
    }

    private void sendInvalidLimit(CommandSender sender, String label, int maxLimit) {
        send(sender, applyPlaceholders(
                getMessage(CONFIG_INVALID_LIMIT, DEFAULT_INVALID_LIMIT),
                0,
                "",
                0.0D,
                plugin.getConfig().getInt(CONFIG_LIMIT, DEFAULT_LIMIT),
                maxLimit,
                label
        ));
    }

    private String applyPlaceholders(String message, int position, String player, double balance, int limit, int maxLimit, String label) {
        return message
                .replace("%pos%", String.valueOf(position))
                .replace("%player%", player)
                .replace("%balance%", plugin.getEconomyManager().format(balance))
                .replace("%limit%", String.valueOf(limit))
                .replace("%min%", String.valueOf(MIN_LIMIT))
                .replace("%max%", String.valueOf(maxLimit))
                .replace("%label%", label);
    }

    private String getMessage(String path, String fallback) {
        String message = plugin.getConfig().getString(path);
        return message == null || message.trim().isEmpty() ? fallback : message;
    }

    private void send(CommandSender sender, String message) {
        sender.sendMessage(ColorUtils.color(message));
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
