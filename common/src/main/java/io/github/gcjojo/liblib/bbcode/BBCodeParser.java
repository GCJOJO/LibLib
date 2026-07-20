package io.github.gcjojo.liblib.bbcode;

import io.github.gcjojo.liblib.math.Color;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Ok là j'avoue j'ai demandé de l'aide à ClaudeSlop
public class BBCodeParser {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\[(/?)(\\w+)((?:\\s+\\w+=\\S+)*)]|([^\\[]+)");
    private static final Set<String> STYLE_TAGS = Set.of("b", "i", "u", "s", "color");

    public static List<BBNode> parse(String input) {
        Deque<List<BBNode>> childrenStack = new ArrayDeque<>();
        Deque<PendingTag> tagStack = new ArrayDeque<>();

        childrenStack.push(new ArrayList<>());

        Matcher matcher = TOKEN_PATTERN.matcher(input);
        while (matcher.find()) {
            String closingSlash = matcher.group(1);
            String tagName = matcher.group(2);
            String rawParams = matcher.group(3);
            String plainText = matcher.group(4);

            if (plainText != null) {
                childrenStack.peek().add(new BBNode.TextNode(plainText));
                continue;
            }

            boolean isClosing = "/".equals(closingSlash);
            if (isClosing) {
                if (childrenStack.size() > 1) {
                    List<BBNode> closedChildren = childrenStack.pop();
                    PendingTag opened = tagStack.pop();
                    childrenStack.peek().add(new BBNode.TagNode(opened.name(), opened.params(), closedChildren));
                }
            } else {
                Map<String, String> params = parseParams(rawParams);
                tagStack.push(new PendingTag(tagName, params));
                childrenStack.push(new ArrayList<>());
            }
        }

        // Close any opened tag at the end of the string
        while (childrenStack.size() > 1) {
            List<BBNode> closedChildren = childrenStack.pop();
            PendingTag openedTag = tagStack.pop();
            childrenStack.peek().add(new BBNode.TagNode(openedTag.name(), openedTag.params(), closedChildren));
        }

        return childrenStack.pop();
    }

    public static List<StyledChar> flatten(List<BBNode> nodes) {
        List<StyledChar> result = new ArrayList<>();
        flattenRec(nodes, TextStyle.DEFAULT, List.of(), result);
        return result;
    }

    private static void flattenRec(List<BBNode> nodes, TextStyle currentStyle, List<TextEffect> currentEffects, List<StyledChar> output) {
        for (BBNode node : nodes) {
            if (node instanceof BBNode.TextNode textNode) {
                for (char c : textNode.text().toCharArray())
                    output.add(new StyledChar(c, currentStyle, currentEffects));
            } else if (node instanceof BBNode.TagNode tagNode) {
                if (STYLE_TAGS.contains(tagNode.tagName().toLowerCase())) {
                    TextStyle newStyle = applyStyleTag(currentStyle, tagNode.tagName(), tagNode.params());
                    flattenRec(tagNode.children(), newStyle, currentEffects, output);

                } else {
                    TextEffect effect = resolveEffect(tagNode.tagName(), tagNode.params(), tagNode.children());
                    List<TextEffect> newEffects = effect != null ? append(currentEffects, effect) : currentEffects;
                    flattenRec(tagNode.children(), currentStyle, newEffects, output);
                }
            }
        }
    }

    private static List<TextEffect> append(List<TextEffect> base, TextEffect toAdd) {
        List<TextEffect> newList = new ArrayList<>(base);
        newList.add(toAdd);
        return newList;
    }

    private static TextStyle applyStyleTag(TextStyle base, String tagName, Map<String, String> params) {
        return switch (tagName.toLowerCase()) {
            case "b" -> base.withBold(true);
            case "i" -> base.withItalic(true);
            case "u" -> base.withUnderline(true);
            case "s" -> base.withStrikethrough(true);
            case "color" -> {
                String raw = params.isEmpty() ? null : params.values().iterator().next();
                if (raw == null) yield base;
                yield base.withColor(Color.fromString(raw));
            }
            default -> base;
        };
    }

    private static TextEffect resolveEffect(String tagName, Map<String, String> params, List<BBNode> children) {
        return switch (tagName.toLowerCase()) {
            case "shake" -> TextEffects.shake(getFloat(params, "intensity", 2.0f), getFloat(params, "speed", 1.0f));
            case "wave" -> TextEffects.wave(
                    getFloat(params, "amplitude", 3.0f), getFloat(params, "frequency", 0.3f), getFloat(params, "speed", 5.0f));
            case "tornado" -> TextEffects.tornado(
                    getFloat(params, "radius", 3.0f), getFloat(params, "speed", 4.0f));
            case "rainbow" -> TextEffects.rainbow(
                    getFloat(params, "speed", 0.3f), getFloat(params, "saturation", 0.8f), getFloat(params, "brightness", 1.0f));
            case "gradient" -> {
                int length = countChars(children); // besoin de la taille totale du contenu !
                Color colorStart = Color.fromString(params.getOrDefault("from", "#FFFFFFFF"));
                Color colorEnd = Color.fromString(params.getOrDefault("to", "#000000FF"));
                yield TextEffects.gradient(colorStart, colorEnd, length);
            }
            default -> BBCodeEffectRegistry.create(tagName, params);
        };
    }

    private static int countChars(List<BBNode> nodes) {
        int count = 0;
        for (BBNode node : nodes) {
            if (node instanceof BBNode.TextNode textNode) {
                count += textNode.text().length();
            } else if (node instanceof BBNode.TagNode tagNode) {
                count += countChars(tagNode.children());
            }
        }
        return count;
    }

    private static float getFloat(Map<String, String> params, String key, float def) {
        try {
            return params.containsKey(key) ? Float.parseFloat(params.get(key)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }


    private static Map<String, String> parseParams(String raw) {
        Map<String, String> result = new HashMap<>();
        if (raw == null || raw.isBlank()) return result;
        for (String pair : raw.trim().split("\\s+")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) result.put(keyValue[0], keyValue[1].replace("\"", ""));
        }
        return result;
    }

    private record PendingTag(String name, Map<String, String> params) {
    }
}
