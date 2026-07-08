package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiProgressBar extends GuiElement {
    protected float startValue;
    protected float endValue;
    protected float currentValue;
    protected int thickness;
    protected int length;
    protected ProgressBarDirection direction;

    protected BarColor progressBarColor = BarColor.White;
    protected BarColor backgroundBarColor = BarColor.Gray;

    GuiNineSliced progressBar;
    GuiNineSliced backgroundBar;

    public GuiProgressBar(Screen screen, float startValue, float endValue, float currentValue, int thickness, int length, ProgressBarDirection direction) {
        super(screen);
        setup(startValue, endValue, currentValue, thickness, length, direction);
    }

    public GuiProgressBar(Screen screen, float startValue, float endValue, float currentValue, int thickness, int length, ProgressBarDirection direction,
                          BarColor progressBarColor, BarColor backgroundBarColor) {
        super(screen);
        this.progressBarColor = progressBarColor;
        this.backgroundBarColor = backgroundBarColor;
        setup(startValue, endValue, currentValue, thickness, length, direction);
    }

    public static int getBarTextureY(BarColor color) {
        int barIndex = color.getColorY();
        return barIndex * 5;
    }

    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i(0, 0, 0, 0);
    }

    public void setup(float startValue, float endValue, float currentValue, int thickness, int length, ProgressBarDirection direction) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.currentValue = MathUtils.clamp(currentValue, startValue, endValue);
        this.thickness = thickness;
        this.length = length;
        this.direction = direction;

        int progressBarTextureY = getBarTextureY(this.progressBarColor);
        int backgroundBarTextureY = getBarTextureY(this.backgroundBarColor);

        switch (this.direction) {
            case Vertical -> {
                progressBar = new GuiNineSliced(this.screen, GuiNineSliced.BARS_ATLAS, 0, 0, thickness, (int) getProgressBarLength(), 2, 2, 182, 5, 0, progressBarTextureY);
                backgroundBar = new GuiNineSliced(this.screen, GuiNineSliced.BARS_ATLAS, 0, 0, thickness, length, 2, 2, 182, 5, 0, backgroundBarTextureY);
            }
            case Horizontal -> {
                progressBar = new GuiNineSliced(this.screen, GuiNineSliced.BARS_ATLAS, 0, 0, (int) getProgressBarLength(), thickness, 2, 2, 182, 5, 0, progressBarTextureY);
                backgroundBar = new GuiNineSliced(this.screen, GuiNineSliced.BARS_ATLAS, 0, 0, length, thickness, 2, 2, 182, 5, 0, backgroundBarTextureY);
            }
        }
    }

    public void setCurrentValue(float newValue) {
        this.currentValue = MathUtils.clamp(newValue, startValue, endValue);
        switch (this.direction) {
            case Vertical -> progressBar.setNineSliceHeight((int) getProgressBarLength());
            case Horizontal -> progressBar.setNineSliceWidth((int) getProgressBarLength());
        }
    }

    public float getProgressBarLength() {
        return (this.currentValue / Math.abs(endValue - startValue)) * (float) length;
    }

    @Override
    public float getContentsWidth() {
        return direction == ProgressBarDirection.Horizontal ? length : thickness;
    }

    @Override
    public float getContentsHeight() {
        return direction == ProgressBarDirection.Vertical ? length : thickness;
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        backgroundBar.draw(graphics, mouseX - this.position.x, mouseY - this.position.y, partialTick, Vec2.ZERO);
        progressBar.draw(graphics, mouseX - this.position.x, mouseY - this.position.y, partialTick, Vec2.ZERO);
    }

    @Override
    public void tick() {

    }

    /*@Override
    protected void mouseClickedContent(double mouseX, double mouseY, int button) {
        progressBar.mouseClickedContent(mouseX - this.position.x, mouseY - this.position.y, button);
        backgroundBar.mouseClickedContent(mouseX - this.position.x, mouseY - this.position.y, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        progressBar.mouseReleased(mouseX - this.position.x, mouseY - this.position.y, button);
        backgroundBar.mouseReleased(mouseX - this.position.x, mouseY - this.position.y, button);
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        progressBar.mouseDragged(mouseX - this.position.x, mouseY - this.position.y, button, deltaX - this.position.x, deltaY - this.position.y);
        backgroundBar.mouseDragged(mouseX - this.position.x, mouseY - this.position.y, button, deltaX - this.position.x, deltaY - this.position.y);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        progressBar.mouseScrolled(mouseX - this.position.x, mouseY - this.position.y, delta);
        backgroundBar.mouseScrolled(mouseX - this.position.x, mouseY - this.position.y, delta);
    }*/

    public enum ProgressBarDirection {
        Vertical,
        Horizontal
    }

    public enum BarColor {
        White(13),
        Gray(12),
        Purple(11),
        DarkPurple(10),
        Yellow(9),
        DarkYellow(8),
        Green(7),
        DarkGreen(6),
        Orange(5),
        DarkOrange(4),
        Blue(3),
        DarkBlue(2),
        Pink(1),
        DarkPink(0);

        @Getter
        private final int colorY;

        BarColor(int colorY) {
            this.colorY = colorY;
        }
    }
}
