package cn.lunadeer.mc.expExtraction;

import cn.lunadeer.mc.expExtraction.utils.configuration.Comments;
import cn.lunadeer.mc.expExtraction.utils.configuration.ConfigurationFile;
import cn.lunadeer.mc.expExtraction.utils.configuration.ConfigurationPart;

public class Configuration extends ConfigurationFile {

    @Comments({"使用 /extract 命令生成经验瓶",})
    public static CommandExtraction commandExtraction = new CommandExtraction();

    public static class CommandExtraction extends ConfigurationPart {
        public boolean enable = true;
        @Comments("每个附魔瓶可容纳的最大经验值")
        public int maximum = 2000;
    }

    @Comments({"使用空瓶 Shift+右键 点击附魔台生成经验瓶",})
    public static EnchantingTable enchantingTable = new EnchantingTable();

    public static class EnchantingTable extends ConfigurationPart {
        public boolean enable = true;
        public int amount = 100;
    }

}
