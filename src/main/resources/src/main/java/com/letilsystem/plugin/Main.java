package com.letilsystem.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener, CommandExecutor {

    private final HashMap<UUID, Integer> letilCounts = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("letil").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("letil")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("baslat")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Location loc = player.getLocation();
                        World world = loc.getWorld();
                        
                        world.setSpawnLocation(loc);

                        WorldBorder border = world.getWorldBorder();
                        border.setCenter(loc.getX(), loc.getZ());
                        border.setSize(2000); 
                        border.setSize(50, 10800); 
                        
                        Bukkit.broadcastMessage(ChatColor.RED + "Letil etkinliği başladı! Spawn ve Border bu nokta olarak ayarlandı, 3 saatlik büyük kapışma başladı!");
                    } else {
                        sender.sendMessage("Bu komut sadece oyundan kullanılabilir.");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("durdur")) {
                    World world = Bukkit.getWorlds().get(0);
                    WorldBorder border = world.getWorldBorder();
                    border.setSize(border.getSize());
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Letil etkinliği durduruldu/donduruldu!");
                    return true;
                }
            }
            sender.sendMessage(ChatColor.GOLD + "Kullanım: /letil baslat veya /letil durdur");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        letilCounts.putIfAbsent(event.getPlayer().getUniqueId(), 15);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        int current = letilCounts.getOrDefault(victim.getUniqueId(), 15);
        if (current > 0) {
            letilCounts.put(victim.getUniqueId(), current - 1);
            victim.getWorld().dropItemNaturally(victim.getLocation(), createLetilItem(1));
        }
        if (current - 1 <= 0) victim.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack().isSimilar(createLetilItem(1))) {
            UUID uuid = event.getPlayer().getUniqueId();
            letilCounts.put(uuid, letilCounts.getOrDefault(uuid, 0) + 1);
            event.getItem().remove();
            event.setCancelled(true);
        }
    }

    private ItemStack createLetilItem(int amount) {
        ItemStack item = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Letil (Can Hakkı)");
        item.setItemMeta(meta);
        return item;
    }
}
