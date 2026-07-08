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

@Getter
@Setter
public class GuiText extends GuiElement {
    private static final Font font = Minecraft.getInstance().font;
    protected Component contents;
    protected TextHorizontalAlignment horizontalAlignment;
    protected TextVerticalAlignment verticalAlignment;

    public GuiText(Screen screen, Component contents) {
        super(screen);
        this.contents = contents;
        this.horizontalAlignment = TextHorizontalAlignment.Left;
        this.verticalAlignment = TextVerticalAlignment.Top;
        this.color = Color.WHITE;
    }

    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    public float getContentsWidth() {
        return font.width(contents);
    }

    @Override
    public float getContentsHeight() {
        return font.lineHeight;
    }

    @Override
    public boolean supportsShaderColor() {
        return true;
    }

    @Override
    public void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
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

        pose.translate(-getContentsWidth() * horizontalAlignmentMultiplier, -getContentsHeight() * verticalAlignmentMultiplier, 0.0f);

        graphics.drawString(font, contents, 0, 0, 0xFFFFFFFF);
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
