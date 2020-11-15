package com.youtube.propromp.pushworldborder;

public enum BorderBehaviour {
    NONE,
    EVERYONE_IN_BORDER,
    ONLY_LEADER,
    MAJOR_HALF,
    USE_MOVING,
    ;

    public static BorderBehaviour from(String text) {
        try {
            return valueOf(text.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return NONE;
    }
}
