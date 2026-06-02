package com.example.chestshop;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChestShopPlugin extends JavaPlugin {

    private static ChestShopPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ChestShopListener(), this);
        this.getCommand("shop").setExecutor(new ShopCommand());
        getLogger().info("ChestShop успішно увімкнено!");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("ChestShop вимкнено.");
    }

    public static ChestShopPlugin getInstance() {
        return instance;
    }
}
