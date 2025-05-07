package cn.lunadeer.mc.expExtraction.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing color codes in strings.
 */
public class ColorParser {

    /**
     * Converts a string with color codes into a TextComponent.
     *
     * @param text the string with color codes
     * @return the TextComponent with the parsed colors
     */
    public static TextComponent getComponentType(String text) {
        text = parseGradient(text);
        String[] parts = text.split("&#");
        List<TextComponent> components = new ArrayList<>();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            TextColor color = TextColor.fromHexString("#ffffff");
            String content;
            if (part.length() >= 6 && part.substring(0, 6).matches("^[0-9a-fA-F]{6}$")) {
                String color_str = part.substring(0, 6);
                color = TextColor.fromHexString("#" + color_str);
                content = part.substring(6);
            } else {
                content = part;
            }
            components.add(Component.text(content, color));
        }
        TextComponent.Builder title_component = Component.text();
        for (TextComponent component : components) {
            title_component.append(component);
        }
        return title_component.build();
    }

    /**
     * Converts a string with color codes into a Bukkit-compatible color string.
     *
     * @param text the string with color codes
     * @return the Bukkit-compatible color string
     */
    public static String getBukkitType(String text) {
        text = parseGradient(text);
        String title = "&f" + text + "&f";
        title = title.replaceAll("&#", "#");
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(title);
        while (matcher.find()) {
            String hexCode = matcher.group();
            StringBuilder builder = new StringBuilder("&x");
            for (char c : hexCode.substring(1).toCharArray()) {
                builder.append('&').append(c);
            }
            title = title.replace(hexCode, builder.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    /**
     * Removes color codes from a string, returning plain text.
     *
     * @param text the string with color codes
     * @return the plain text string
     */
    public static String getPlainText(String text) {
        text = parseGradient(text);
        String[] parts = text.split("&#");
        StringBuilder res = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            String content;
            if (part.length() >= 6 && part.substring(0, 6).matches("^[0-9a-fA-F]{6}$")) {
                content = part.substring(6);
            } else {
                content = part;
            }
            res.append(content);
        }
        return res.toString();
    }

    /**
     * Parses gradient color syntax in a string.
     * <gradient:#2D8CF0:#19BE6B:#FF9900>text</gradient> is converted to &#2d8cf0t&#279acae&#22a9a4x&#1cb77et&#3ab95ct&#7cae3dt&#bda41fn&#ff9900t.
     *
     * @param text the string with gradient color syntax
     * @return the string with parsed gradient colors
     */
    private static String parseGradient(String text) {
        if (!text.contains("<gradient:")) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int startIndex = 0;

        while (startIndex < text.length()) {
            int openTagIndex = text.indexOf("<gradient:", startIndex);
            if (openTagIndex == -1) {
                result.append(text.substring(startIndex));
                break;
            }

            result.append(text, startIndex, openTagIndex);

            int closeTagIndex = text.indexOf("</gradient>", openTagIndex);
            if (closeTagIndex == -1) {
                closeTagIndex = text.length();
                text += "</gradient>";
            }

            String gradientPart = text.substring(openTagIndex + 10, text.indexOf(">", openTagIndex));
            String[] colors = gradientPart.split(":");
            String content = text.substring(text.indexOf(">", openTagIndex) + 1, closeTagIndex);

            result.append(applyGradient(content, colors));

            startIndex = closeTagIndex + "</gradient>".length();
        }
        return result.toString();
    }

    /**
     * Applies gradient colors to a string.
     *
     * @param content the string content
     * @param colors  the array of color codes
     * @return the string with applied gradient colors
     */
    private static String applyGradient(String content, String[] colors) {
        StringBuilder gradientText = new StringBuilder();
        int length = content.length();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            Color color = interpolateColor(colors, ratio);
            String hexColor = String.format("#%06x", color.getRGB() & 0xFFFFFF);
            gradientText.append("&#").append(hexColor.substring(1)).append(content.charAt(i));
        }

        return gradientText.toString();
    }

    /**
     * Interpolates between colors based on a ratio.
     *
     * @param colors the array of color codes
     * @param ratio  the ratio for interpolation
     * @return the interpolated color
     */
    private static Color interpolateColor(String[] colors, float ratio) {
        int segment = (int) (ratio * (colors.length - 1));

        if (segment >= colors.length - 1) {
            return Color.decode(colors[colors.length - 1]);
        }

        float segmentRatio = ratio * (colors.length - 1) - segment;

        Color color1 = Color.decode(colors[segment]);
        Color color2 = Color.decode(colors[segment + 1]);

        int red = (int) (color1.getRed() * (1 - segmentRatio) + color2.getRed() * segmentRatio);
        int green = (int) (color1.getGreen() * (1 - segmentRatio) + color2.getGreen() * segmentRatio);
        int blue = (int) (color1.getBlue() * (1 - segmentRatio) + color2.getBlue() * segmentRatio);

        return new Color(red, green, blue);
    }

}