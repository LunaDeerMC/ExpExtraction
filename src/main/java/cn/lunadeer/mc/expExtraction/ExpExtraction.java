package cn.lunadeer.mc.expExtraction;

import cn.lunadeer.mc.expExtraction.utils.Notification;
import cn.lunadeer.mc.expExtraction.utils.bStatsMetrics;
import cn.lunadeer.mc.expExtraction.utils.configuration.ConfigurationManager;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

import static cn.lunadeer.mc.expExtraction.Helper.generateBottle;
import static cn.lunadeer.mc.expExtraction.Helper.getExperienceOf;

public final class ExpExtraction extends JavaPlugin implements CommandExecutor, Listener {

    public final static NamespacedKey key = new NamespacedKey("exp_extraction", "exp");

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            ConfigurationManager.load(Configuration.class, new File(getDataFolder(), "config.yml"));
        } catch (Exception e) {
            getLogger().warning("配置文件加载失败，使用默认配置");
        }
        new bStatsMetrics(this, 26205);
        Objects.requireNonNull(this.getCommand("extract")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        new bStatsMetrics(this, 26231);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onExpBottleUse(PlayerLaunchProjectileEvent event) {
        ItemStack item = event.getItemStack();
        if (item.getType() != org.bukkit.Material.EXPERIENCE_BOTTLE) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            return;
        }
        int amount = meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0);
        Player player = event.getPlayer();
        player.giveExp(amount, true);
        player.sendMessage(Notification.info("你从经验瓶中获得了 {0} 点经验值", amount));
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!Configuration.commandExtraction.enable) return true;
        if (!(sender instanceof Player player)) {
            getLogger().warning("只有玩家可以使用此命令");
            return true;
        }
        // Handle command execution
        int amount;
        if (args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Notification.error("请输入一个有效的整数"));
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(Notification.error("请输入一个非负整数"));
                return true;
            }
            if (amount > getExperienceOf(player)) {
                sender.sendMessage(Notification.error("你没有足够的经验值"));
                return true;
            }
        } else {
            amount = getExperienceOf(player);
        }
        if (amount == 0) {
            sender.sendMessage(Notification.error("你没有足够的经验值"));
            return true;
        }
        if (amount > Configuration.commandExtraction.maximum) {
            sender.sendMessage(Notification.warn("每个附魔瓶最多只能容纳 {0} 点经验值", Configuration.commandExtraction.maximum));
            amount = Configuration.commandExtraction.maximum;
        }
        generateBottle(player, amount);
        player.sendMessage(Notification.info("成功提取 {0} 点经验值到经验瓶", amount));
        return true;
    }

    @EventHandler
    public void onPlayerEnchantTable(PlayerInteractEvent event) {
        if (!Configuration.commandExtraction.enable) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != org.bukkit.Material.ENCHANTING_TABLE) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != org.bukkit.Material.GLASS_BOTTLE) {
            return;
        }
        int amount = Configuration.enchantingTable.amount;
        if (amount > getExperienceOf(player)) {
            amount = getExperienceOf(player);
        }
        if (amount <= 0) {
            player.sendMessage(Notification.error("你没有足够的经验值"));
            return;
        }
        generateBottle(player, amount);
        player.sendMessage(Notification.info("成功使用附魔台提取 {0} 点经验值到经验瓶", amount));
        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(org.bukkit.Material.AIR));
        }
        event.setCancelled(true);
    }
}
