package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.math.Color;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
public class GuiImage extends GuiElement {
    protected ResourceLocation imagePath;
    protected int imageWidth;
    protected int imageHeight;

    public GuiImage(Screen screen, ResourceLocation imagePath, int imageWidth, int imageHeight) {
        super(screen);
        this.imagePath = imagePath;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.color = Color.WHITE;
    }

    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    public float getContentsWidth() {
        return imageWidth;
    }

    @Override
    public float getContentsHeight() {
        return imageHeight;
    }

    @Override
    public boolean supportsShaderColor() {
        return true;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        graphics.blit(imagePath, -imageWidth / 2, -imageHeight / 2, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    public void tick() {

    }
}
