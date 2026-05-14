package com.marcosfg.meconomy;

import com.marcosfg.meconomy.api.VaultHook;
import com.marcosfg.meconomy.commands.MagnataCommand;
import com.marcosfg.meconomy.commands.MoneyCommand;
import com.marcosfg.meconomy.gui.MoneyMenu;
import com.marcosfg.meconomy.manager.EconomyManager;
import com.marcosfg.meconomy.storage.StorageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private StorageManager storageManager;
    private EconomyManager economyManager;
    private VaultHook vaultHook;
    private MoneyMenu moneyMenu;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        storageManager = new StorageManager(this);
        economyManager = new EconomyManager(this);
        moneyMenu = new MoneyMenu(this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setupVault();
        }

        getCommand("money").setExecutor(new MoneyCommand(this, moneyMenu));
        getCommand("magnata").setExecutor(new MagnataCommand(this));
        getServer().getPluginManager().registerEvents(moneyMenu, this);

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

    public MoneyMenu getMoneyMenu() {
        return moneyMenu;
    }
}
