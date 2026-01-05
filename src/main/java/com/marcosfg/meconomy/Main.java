package com.marcosfg.meconomy;

import com.marcosfg.meconomy.api.VaultHook;
import com.marcosfg.meconomy.commands.MoneyCommand;
import com.marcosfg.meconomy.commands.MagnataCommand;
import com.marcosfg.meconomy.manager.EconomyManager;
import com.marcosfg.meconomy.storage.StorageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private StorageManager storageManager;
    private EconomyManager economyManager;
    private VaultHook vaultHook;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        storageManager = new StorageManager(this);
        economyManager = new EconomyManager(this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setupVault();
        }

        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("magnata").setExecutor(new MagnataCommand(this));

        getLogger().info("mEconomy carregado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (storageManager != null) {
            storageManager.close();
        }
        getLogger().info("mEconomy desativado.");
    }

    private void setupVault() {
        vaultHook = new VaultHook(this);
        getServer().getServicesManager().register(Economy.class, vaultHook, this, ServicePriority.Highest);
        getLogger().info("Vault hook registrado!");
    }

    public static Main getInstance() {
        return instance;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
