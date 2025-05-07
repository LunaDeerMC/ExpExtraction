package cn.lunadeer.mc.expExtraction;

import cn.lunadeer.mc.expExtraction.utils.ColorParser;
import cn.lunadeer.mc.expExtraction.utils.Notification;
import cn.lunadeer.mc.expExtraction.utils.configuration.ConfigurationManager;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

public final class ExpExtraction extends JavaPlugin implements CommandExecutor, Listener {

    private final static NamespacedKey key = new NamespacedKey("exp_extraction", "exp");

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            ConfigurationManager.load(Configuration.class, new File(getDataFolder(), "config.yml"));
        } catch (Exception e) {
            getLogger().warning("配置文件加载失败，使用默认配置");
        }
        Objects.requireNonNull(this.getCommand("extract")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
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
        player.sendMessage(Notification.info("你从经验瓶中获得了 " + amount + " 点经验值"));
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
        if (amount > Configuration.maximum) {
            sender.sendMessage(Notification.warn("每个附魔瓶最多只能容纳 " + Configuration.maximum + " 点经验值"));
            amount = Configuration.maximum;
        }
        // Perform the experience extraction
        setExperienceOf(player, getExperienceOf(player) - amount);
        ItemStack expBottle = new ItemStack(org.bukkit.Material.EXPERIENCE_BOTTLE, 1);
        ItemMeta meta = expBottle.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, amount);
        meta.displayName(ColorParser.getComponentType(
                "&#5debff[&#55eeec手&#4df2d9工&#46f5c7附&#3ef8b4魔&#36fca1瓶&#2eff8e]"
        ).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("· 内含： " + amount + " 点经验值", TextColor.color(103, 255, 123)).decoration(TextDecoration.ITALIC, false),
                Component.text("· 来自： " + player.getName(), TextColor.color(103, 255, 123)).decoration(TextDecoration.ITALIC, false)
        ));
        meta.setMaxStackSize(99);
        meta.setEnchantmentGlintOverride(true);
        expBottle.setItemMeta(meta);
        player.getLocation().getWorld().dropItemNaturally(player.getLocation().getBlock().getRelative(BlockFace.UP).getLocation(), expBottle);
        player.sendMessage(Notification.info("成功提取 " + amount + " 点经验值到经验瓶"));
        return true;
    }

    private static int getExperienceOf(Player player) {
        int level = player.getLevel();
        float experience = player.getExp();
        int totalExp = 0;
        int levelNeeded = 7;
        for (int i = 0; i < level; i++) {
            totalExp += levelNeeded;
            levelNeeded += 2;
        }
        totalExp += (int) (experience * levelNeeded);
        return totalExp;
    }

    private static void setExperienceOf(Player player, int totalExp) {
        int level = 0;
        int levelNeeded = 7;
        while (totalExp >= levelNeeded) {
            totalExp -= levelNeeded;
            level++;
            levelNeeded += 2;
        }
        player.setLevel(level);
        player.setExp((float) totalExp / levelNeeded);
    }
}
