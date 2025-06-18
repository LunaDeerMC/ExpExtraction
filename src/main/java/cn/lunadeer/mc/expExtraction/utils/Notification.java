package cn.lunadeer.mc.expExtraction.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import static cn.lunadeer.mc.expExtraction.Helper.formatString;

public class Notification {

    public static Component info(String message, Object... args) {
        return Component.text(formatString(message, args), TextColor.color(0, 255, 216));
    }

    public static Component error(String message, Object... args) {
        return Component.text(formatString(message, args), TextColor.color(255, 126, 174));
    }

    public static Component warn(String message, Object... args) {
        return Component.text(formatString(message, args), TextColor.color(255, 230, 129));
    }
}
