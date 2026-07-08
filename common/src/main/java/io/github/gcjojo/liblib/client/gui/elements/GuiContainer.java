package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

@Getter
public abstract class GuiContainer extends GuiElement {
    int containerWidth;
    int containerHeight;

    public GuiContainer(Screen screen, int containerWidth, int containerHeight) {
        super(screen);
        this.containerWidth = containerWidth;
        this.containerHeight = containerHeight;
    }

    public float getContainerWidth() {
        return this.containerWidth * scale.x;
    }

    public float getContainerHeight() {
        return this.containerHeight * scale.y;
    }

    // TODO Fix bounds going in wrong direction
    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i((int) (containerWidth * 0.5f), (int) (containerHeight * 0.5f), containerWidth, containerHeight);
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {

    }
}
