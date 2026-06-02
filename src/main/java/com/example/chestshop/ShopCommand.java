package com.example.chestshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Цю команду може виконувати тільки гравець!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2 || !args[0].equalsIgnoreCase("create")) {
            player.sendMessage(ChatColor.RED + "Використання: /shop create [ціна_в_алмазах]");
            return true;
        }

        int price;
        try {
            price = Integer.parseInt(args[1]);
            if (price <= 0) {
                player.sendMessage(ChatColor.RED + "Ціна повинна бути більшою за 0!");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Будь ласка, вкажіть коректне число для ціни.");
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Ви повинні тримати в руці предмет, який хочете продавати!");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.getType() != Material.CHEST) {
            player.sendMessage(ChatColor.RED + "Ви повинні дивитися на сундук!");
            return true;
        }

        Material itemToSell = itemInHand.getType();

        String worldName = targetBlock.getWorld().getName();
        int x = targetBlock.getX();
        int y = targetBlock.getY();
        int z = targetBlock.getZ();
        String path = "shops." + worldName + "_" + x + "_" + y + "_" + z;

        FileConfiguration config = ChestShopPlugin.getInstance().getConfig();

        if (config.contains(path)) {
            player.sendMessage(ChatColor.RED + "Цей сундук уже зареєстрований як магазин!");
            return true;
        }

        config.set(path + ".owner", player.getUniqueId().toString());
        config.set(path + ".price", price);
        config.set(path + ".item", itemToSell.name());
        ChestShopPlugin.getInstance().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Магазин успішно створено!");
        player.sendMessage(ChatColor.GREEN + "Товар: " + ChatColor.YELLOW + itemToSell.name() + ChatColor.GREEN + " | Ціна за 1 шт: " + ChatColor.YELLOW + price + " алмазів.");
        return true;
    }
}
