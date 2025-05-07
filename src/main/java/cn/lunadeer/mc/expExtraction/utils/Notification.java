package cn.lunadeer.mc.expExtraction.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Notification {

    public static Component info(String message) {
        return Component.text(message, TextColor.color(0, 255, 216));
    }

    public static Component error(String message) {
        return Component.text(message, TextColor.color(255, 126, 174));
    }

    public static Component warn(String message) {
        return Component.text(message, TextColor.color(255, 230, 129));
    }
}
