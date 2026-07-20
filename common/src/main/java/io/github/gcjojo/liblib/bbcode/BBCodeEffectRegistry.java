package io.github.gcjojo.liblib.bbcode;

import java.util.HashMap;
import java.util.Map;

// Merci ClaudeSlop
public class BBCodeEffectRegistry {
    private static final Map<String, EffectFactory> registry = new HashMap<>();

    public static void register(String tagName, EffectFactory factory) {
        registry.put(tagName.toLowerCase(), factory);
    }

    public static TextEffect create(String tagName, Map<String, String> params) {
        EffectFactory factory = registry.get(tagName.toLowerCase());
        return factory != null ? factory.create(params) : null;
    }

    @FunctionalInterface
    public interface EffectFactory {
        TextEffect create(Map<String, String> params);
    }
}
