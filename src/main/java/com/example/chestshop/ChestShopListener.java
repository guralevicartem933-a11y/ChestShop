package com.example.chestshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChestShopListener implements Listener {

    private final Material CURRENCY = Material.DIAMOND;

    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.CHEST) return;

        String worldName = clickedBlock.getWorld().getName();
        int x = clickedBlock.getX();
        int y = clickedBlock.getY();
        int z = clickedBlock.getZ();
        String path = "shops." + worldName + "_" + x + "_" + y + "_" + z;

        FileConfiguration config = ChestShopPlugin.getInstance().getConfig();

        if (!config.contains(path)) return;

        Player player = event.getPlayer();
        String ownerUUIDString = config.getString(path + ".owner");
        int price = config.getInt(path + ".price");
        
        String itemName = config.getString(path + ".item");
        Material itemToSell = Material.matchMaterial(itemName);

        if (itemToSell == null) {
            player.sendMessage(ChatColor.RED + "Помилка конфігурації магазину.");
            return;
        }

        if (player.getUniqueId().toString().equals(ownerUUIDString)) {
            player.sendMessage(ChatColor.YELLOW + "Ви відкрили свій магазин. Товар: " + itemToSell.name() + " | Ціна: " + price + " алмазів.");
            return;
        }

        event.setCancelled(true);

        Chest chest = (Chest) clickedBlock.getState();
        Inventory chestInv = chest.getInventory();

        if (!chestInv.contains(itemToSell, 1)) {
            player.sendMessage(ChatColor.RED + "Магазин порожній! Товар закінчився.");
            
            Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUIDString));
            if (owner != null && owner.isOnline()) {
                owner.sendMessage(ChatColor.RED + "[Магазин] У вашому сундуку закінчився товар: " + itemToSell.name() + "!");
            }
            return;
        }

        if (!player.getInventory().contains(CURRENCY, price)) {
            player.sendMessage(ChatColor.RED + "У вас недостатньо алмазів! Ціна: " + price + " шт.");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Ваш інвентар повний!");
            return;
        }

        player.getInventory().removeItem(new ItemStack(CURRENCY, price));
        chestInv.removeItem(new ItemStack(itemToSell, 1));
        chestInv.addItem(new ItemStack(CURRENCY, price));
        player.getInventory().addItem(new ItemStack(itemToSell, 1));
        
        chest.update();

        player.sendMessage(ChatColor.GREEN + "Ви успішно купили 1 " + itemToSell.name() + " за " + price + " алмазів!");

        if (!chestInv.contains(itemToSell, 1)) {
            Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUIDString));
            if (owner != null && owner.isOnline()) {
                owner.sendMessage(ChatColor.RED + "[Магазин] Увага! Щойно закінчився товар (" + itemToSell.name() + ") у вашому сундуку!");
            }
        }
    }
}
