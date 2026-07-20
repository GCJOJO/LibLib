package io.github.gcjojo.liblib.bbcode;

import java.util.List;
import java.util.Map;

// Ok là j'avoue j'ai demandé de l'aide à ClaudeSlop
public sealed interface BBNode {
    record TextNode(String text) implements BBNode {

    }

    record TagNode(String tagName, Map<String, String> params, List<BBNode> children) implements BBNode {

    }
}

