package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.math.Color;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

@Getter
@Setter
public class GuiColorRect extends GuiElement {
    protected int left;
    protected int bottom;
    protected int right;
    protected int top;

    public GuiColorRect(Screen screen, int left, int bottom, int right, int top, Color color) {
        super(screen);
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
        this.color = color;
    }

    @Override
    public float getContentsWidth() {
        return right - left;
    }

    @Override
    public float getContentsHeight() {
        return top - bottom;
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        graphics.fill(left, bottom, right, top, color.getColorInt());
    }

    @Override
    public void tick() {

    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {

    }

    @Override
    protected void mouseClickedContent(double mouseX, double mouseY, int button) {

    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

    }
}
