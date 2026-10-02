package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
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

        ResourceLocation progressBarTexture = progressBarColor.getSpriteLocation();
        ResourceLocation backgroundBarTexture = backgroundBarColor.getSpriteLocation();

        progressBar = new GuiNineSliced(this.screen, progressBarTexture, 0, 0, (int) getProgressBarLength(), thickness);
        backgroundBar = new GuiNineSliced(this.screen, backgroundBarTexture, 0, 0, length, thickness);

        if (direction == ProgressBarDirection.Vertical) {
            progressBar.setAngle(90.0f);
            progressBar.setRotationPivot(new Vec2(0.0f, 0.5f));

            backgroundBar.setAngle(90.0f);
            backgroundBar.setRotationPivot(new Vec2(0.0f, 0.5f));
        }
    }

    public void setCurrentValue(float newValue) {
        this.currentValue = MathUtils.clamp(newValue, startValue, endValue);
        progressBar.setNineSliceWidth((int) getProgressBarLength());
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
        White(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/white_progress")),
        Gray(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/white_background")),
        Purple(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/purple_progress")),
        DarkPurple(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/purple_background")),
        Yellow(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/yellow_progress")),
        DarkYellow(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/yellow_background")),
        Green(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/green_progress")),
        DarkGreen(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/green_background")),
        Red(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/red_progress")),
        DarkRed(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/red_background")),
        Blue(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/blue_progress")),
        DarkBlue(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/blue_background")),
        Pink(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/pink_progress")),
        DarkPink(ResourceLocation.tryBuild(LibLib.MOD_ID, "progress_bars/pink_background"));

        @Getter
        private final ResourceLocation spriteLocation;

        BarColor(ResourceLocation spriteLocation) {
            this.spriteLocation = spriteLocation;
        }
    }
}
