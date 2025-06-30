package cn.lunadeer.mc.expExtraction;

import cn.lunadeer.mc.expExtraction.utils.ColorParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Helper {
    /**
     * 为玩家生成一个包含指定经验值的经验瓶，并将其掉落在玩家头顶方块上方。
     *
     * @param player 目标玩家
     * @param amount 需要封装进经验瓶的经验值数量
     */
    public static void generateBottle(Player player, int amount) {
        setExperienceOf(player, getExperienceOf(player) - amount);
        ItemStack expBottle = new ItemStack(org.bukkit.Material.EXPERIENCE_BOTTLE, 1);
        ItemMeta meta = expBottle.getItemMeta();
        meta.getPersistentDataContainer().set(ExpExtraction.key, PersistentDataType.INTEGER, amount);
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
    }

    /**
     * 获取玩家当前的总经验值。
     * 经验值计算方式为：每升一级所需经验值递增2点，初始为7点。
     * 公式：totalExp = sum(7 + 2 * i) for i in [0, level-1] + 当前等级进度 * 当前等级所需经验
     *
     * @param player 目标玩家
     * @return 玩家当前的总经验值
     */
    public static int getExperienceOf(Player player) {
        return player.calculateTotalExperiencePoints();
    }

    /**
     * 设置玩家的总经验值。
     * 会根据总经验值自动计算并设置玩家的等级和当前等级进度。
     *
     * @param player   目标玩家
     * @param totalExp 需要设置的总经验值
     */
    public static void setExperienceOf(Player player, int totalExp) {
        player.setExperienceLevelAndProgress(totalExp);
    }

    public static String formatString(String str, Object... args) {
        String formatStr = str;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                args[i] = "[null for formatString (args[" + i + "])]";
            }
            formatStr = formatStr.replace("{" + i + "}", args[i].toString());
        }
        return formatStr;
    }
}
