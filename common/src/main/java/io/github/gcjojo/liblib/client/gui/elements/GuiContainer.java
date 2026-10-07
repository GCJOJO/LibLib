package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.phys.Vec2;

@Getter
public abstract class GuiContainer extends GuiElement {
    public GuiContainer(Screen screen, int containerWidth, int containerHeight) {
        super(screen);
        setSize(new Vec2(containerWidth, containerHeight));
    }

    public void setSizeInternal(Vec2 newSize) {

    }

    // TODO Fix bounds going in wrong direction
    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i((int) (getWidth() * 0.5f), (int) (getHeight() * 0.5f), (int) (getWidth() * 0.5f), (int) (getHeight() * 0.5f));
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
    }
}
