package cn.lunadeer.mc.expExtraction;

import cn.lunadeer.mc.expExtraction.utils.configuration.Comments;
import cn.lunadeer.mc.expExtraction.utils.configuration.ConfigurationFile;

public class Configuration extends ConfigurationFile {

    @Comments({
            "每个附魔瓶可容纳的最大经验值",
    })
    public static int maximum = 2000;

}
