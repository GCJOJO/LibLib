package io.github.gcjojo.liblib.client.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gcjojo.liblib.math.Color;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiText extends GuiElement {
    protected static final Font font = Minecraft.getInstance().font;
    protected Component text;
    protected int drawnCharacters = -1;
    protected TextHorizontalAlignment horizontalAlignment;
    protected TextVerticalAlignment verticalAlignment;

    public GuiText(Screen screen, Component text) {
        super(screen);
        this.text = text;
        this.horizontalAlignment = TextHorizontalAlignment.Left;
        this.verticalAlignment = TextVerticalAlignment.Top;
        this.color = Color.WHITE;

        this.setWidth(font.width(text));
        this.setHeight(font.lineHeight);
    }

    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    protected void setSizeInternal(Vec2 newSize) {

    }

    @Override
    public boolean supportsShaderColor() {
        return true;
    }

    public char getLastDrawCharacter() {
        String string = text.getString(drawnCharacters);
        return string.charAt(string.length() - 1);
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
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

        pose.translate(-getWidth() * horizontalAlignmentMultiplier, -getHeight() * verticalAlignmentMultiplier, 0.0f);

        Component drawnText = text;
        if (drawnCharacters > -1) {
            drawnText = Component.literal(drawnText.getString(drawnCharacters));
        }
        graphics.drawString(font, drawnText, 0, 0, 0xFFFFFFFF);
        pose.popPose();
    }

    @Override
    public void tick() {

    }

    public enum TextHorizontalAlignment {
        Left,
        Center,
        Right
    }

    public enum TextVerticalAlignment {
        Top,
        Center,
        Bottom
    }
}
