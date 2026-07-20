package io.github.gcjojo.liblib.bbcode;

import io.github.gcjojo.liblib.math.Color;

public record TextStyle(boolean bold, boolean italic, boolean underline, boolean strikethrough, Color color) {
    public static final TextStyle DEFAULT = new TextStyle(false, false, false, false, null);

    public TextStyle withBold(boolean value) {
        return new TextStyle(value, italic, underline, strikethrough, color);
    }

    public TextStyle withItalic(boolean value) {
        return new TextStyle(bold, value, underline, strikethrough, color);
    }

    public TextStyle withUnderline(boolean value) {
        return new TextStyle(bold, italic, value, strikethrough, color);
    }

    public TextStyle withStrikethrough(boolean value) {
        return new TextStyle(bold, italic, underline, value, color);
    }

    public TextStyle withColor(Color value) {
        return new TextStyle(bold, italic, underline, strikethrough, value);
    }
}
