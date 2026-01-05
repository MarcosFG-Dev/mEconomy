package com.marcosfg.meconomy.manager;

import com.marcosfg.meconomy.Main;
import com.marcosfg.meconomy.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyManager {

    private final Main plugin;
    private final Map<UUID, Double> cache = new ConcurrentHashMap<>();

    public EconomyManager(Main plugin) {
        this.plugin = plugin;
    }

    public double getBalance(OfflinePlayer player) {
        if (player.isOnline()) {
            if (!cache.containsKey(player.getUniqueId())) {
                cache.put(player.getUniqueId(), plugin.getStorageManager().loadBalance(player.getUniqueId()));
            }
            return cache.get(player.getUniqueId());
        }
        return plugin.getStorageManager().loadBalance(player.getUniqueId());
    }

    public void setBalance(OfflinePlayer player, double amount) {
        if (amount < 0)
            amount = 0;
        cache.put(player.getUniqueId(), amount);

        double finalAmount = amount;
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getStorageManager().saveBalance(player.getUniqueId(), finalAmount));

        checkMagnata();
    }

    public void deposit(OfflinePlayer player, double amount) {
        setBalance(player, getBalance(player) + amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        setBalance(player, getBalance(player) - amount);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    public String format(double amount) {
        return FormatUtils.format(amount, plugin);
    }

    private void checkMagnata() {
    }

    public Map<String, Double> getTopBalance(int limit) {
        Map<String, Double> raw = plugin.getStorageManager().getTopAccounts(limit);
        Map<String, Double> result = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : raw.entrySet()) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
            result.put(p.getName() != null ? p.getName() : "Desconhecido", entry.getValue());
        }

        return result;
    }
}
