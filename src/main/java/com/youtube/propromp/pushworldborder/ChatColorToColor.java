package com.youtube.propromp.pushworldborder;

import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;

import static org.bukkit.ChatColor.*;

public class ChatColorToColor {
    private static ImmutableMap<ChatColor, Integer> colorMap = ImmutableMap.<ChatColor, Integer>builder()
            .put(BLACK, 0)
            .put(DARK_BLUE, 170)
            .put(DARK_GREEN, 43520)
            .put(DARK_AQUA, 43690)
            .put(DARK_RED, 11141120)
            .put(DARK_PURPLE, 11141290)
            .put(GOLD, 16755200)
            .put(GRAY, 11184810)
            .put(DARK_GRAY, 5592405)
            .put(BLUE, 5592575)
            .put(GREEN, 5635925)
            .put(AQUA, 5636095)
            .put(RED, 16733525)
            .put(LIGHT_PURPLE, 16733695)
            .put(YELLOW, 16777045)
            .put(WHITE, 16777215)
            .build();

    public static Color getColor(ChatColor color) {
        int color_int = colorMap.getOrDefault(color, 16777215);
        return Color.fromRGB(color_int >> 16 & 255, color_int >> 8 & 255, color_int & 255);
    }
}
