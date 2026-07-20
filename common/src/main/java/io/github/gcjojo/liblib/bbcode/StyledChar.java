package io.github.gcjojo.liblib.bbcode;

import java.util.List;

public record StyledChar(char character, TextStyle style, List<TextEffect> effects) {
}
