package io.github.gcjojo.liblib.client.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.gcjojo.liblib.bbcode.*;
import io.github.gcjojo.liblib.math.Color;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

// Ok là j'avoue j'ai demandé de l'aide à ClaudeSlop
// TODO Fix Element size and make auto wrapping text
@Getter
public class GuiRichText extends GuiText {
    private float tick = 0.0f;
    private List<StyledChar> styledCharacters = new ArrayList<>();

    public GuiRichText(Screen screen, Component text) {
        super(screen, text);
        this.text = text;
        refreshCharacters();
    }

    public void refreshCharacters() {
        final String textString = text.getString();
        List<BBNode> nodes = BBCodeParser.parse(textString);
        styledCharacters = BBCodeParser.flatten(nodes);
    }

    public void setText(Component newText) {
        this.text = newText;
        refreshCharacters();
    }

    @Override
    public float getContentsWidth() {
        float maxWidth = 0.0f;
        float lineWidth = 0.0f;
        for (StyledChar c : styledCharacters) {
            if (c.character() == '\n') {
                maxWidth = Math.max(maxWidth, lineWidth);
                lineWidth = 0.0f;
                continue;
            }

            Style style = Style.EMPTY
                    .withBold(c.style().bold())
                    .withItalic(c.style().italic())
                    .withUnderlined(c.style().underline())
                    .withStrikethrough(c.style().strikethrough());
            lineWidth += font.width(FormattedCharSequence.forward(String.valueOf(c.character()), style));
        }

        return maxWidth;
    }

    @Override
    public float getContentsHeight() {
        float height = 0.0f;
        final float lineHeight = font.lineHeight;
        for (StyledChar c : styledCharacters) {
            if (c.character() != '\n') continue;
            height += lineHeight;
        }
        return height;
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    public char getLastDrawCharacter() {
        if (drawnCharacters > -1 && drawnCharacters < styledCharacters.size())
            return styledCharacters.get(drawnCharacters).character();
        return styledCharacters.get(styledCharacters.size() - 1).character();
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        tick += partialTick;
        final float time = tick * 0.05f;
        final float lineHeight = font.lineHeight;
        float cursorX = 0;
        float cursorY = 0;
        final PoseStack poseStack = graphics.pose();

        for (int i = 0; i < styledCharacters.size(); i++) {
            if (drawnCharacters > -1 && i > drawnCharacters)
                break;

            StyledChar c = styledCharacters.get(i);
            if (c.character() == '\n') {
                cursorX = 0;
                cursorY += lineHeight;
                continue;
            }

            TextEffect.CharTransform transform = TextEffects.combine(c.effects(), i, time, c.style().color() != null ? c.style().color() : Color.WHITE);

            Style style = Style.EMPTY
                    .withBold(c.style().bold())
                    .withItalic(c.style().italic())
                    .withUnderlined(c.style().underline())
                    .withStrikethrough(c.style().strikethrough());

            poseStack.pushPose();

            float horizontalAlignmentMultiplier;

            switch (horizontalAlignment) {
                case Center -> horizontalAlignmentMultiplier = 0.5f;
                case Right -> horizontalAlignmentMultiplier = 1.0f;
                default -> horizontalAlignmentMultiplier = 0.0f;
            }

            float verticalAlignmentMultiplier = 0.0f;
            switch (verticalAlignment) {
                case Center -> verticalAlignmentMultiplier = 0.5f;
                case Bottom -> verticalAlignmentMultiplier = 1.0f;
                default -> verticalAlignmentMultiplier = 0.0f;
            }
            // TODO Maybe do this per line
            poseStack.translate(-getContentsWidth() * horizontalAlignmentMultiplier, -getContentsHeight() * verticalAlignmentMultiplier, 0.0f);

            poseStack.translate(cursorX + transform.offsetX(), cursorY + transform.offsetY(), 0.0f);
            poseStack.scale(transform.scale(), transform.scale(), 1.0f);
            if (transform.rotationDegrees() != 0)
                poseStack.rotateAround(Axis.ZP.rotationDegrees(transform.rotationDegrees()), 0, 0, 0);

            String sc = String.valueOf(c.character());
            graphics.drawString(font, Component.literal(sc).withStyle(style), 0, 0, transform.color().getColorInt());

            poseStack.popPose();
            cursorX += font.width(FormattedCharSequence.forward(sc, style));
        }
    }

    @Override
    public void tick() {

    }


}
