package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiSlider extends GuiElement {
    protected final ResourceLocation BARS_ATLAS = ResourceLocation.tryParse("textures/gui/bars.png");
    protected int thickness;
    protected int length;
    protected float startValue;
    protected float endValue;
    protected float step;
    protected SliderDirection direction;

    protected float currentValue;
    protected boolean scrolling = false;
    protected GuiNineSliced sliderButton;
    protected GuiNineSliced sliderBackground;

    protected SliderValueUpdated sliderValueUpdated = null;

    public GuiSlider(Screen screen, int thickness, int length, float startValue, float endValue, float step, SliderDirection direction) {
        super(screen);
        setup(thickness, length, startValue, endValue, step, direction);
    }

    public GuiSlider(Screen screen, int thickness, int length, float startValue, float endValue, float step, SliderDirection direction, SliderValueUpdated sliderValueUpdated) {
        super(screen);
        setup(thickness, length, startValue, endValue, step, direction);
        this.sliderValueUpdated = sliderValueUpdated;
    }

    private void setup(int thickness, int length, float startValue, float endValue, float step, SliderDirection direction) {
        this.thickness = thickness;
        this.length = length;
        this.startValue = startValue;
        this.endValue = endValue;
        this.step = MathUtils.clamp(step, 0, Math.abs(endValue - startValue));

        if (this.endValue == this.startValue)
            this.endValue += this.step;

        this.currentValue = startValue;
        this.direction = direction;

        int buttonLength = (int) ((step / Math.abs(endValue - startValue)) * length);
        if (buttonLength < 10) buttonLength = 10;

        switch (this.direction) {
            case Vertical -> {
                sliderButton = new GuiNineSliced(this.screen, BARS_ATLAS, 0, 0, thickness, buttonLength, 2, 2, 182, 5, 0, 65);
                sliderBackground = new GuiNineSliced(this.screen, BARS_ATLAS, 0, 0, thickness, length + buttonLength, 2, 2, 182, 5, 0, 60);
            }
            case Horizontal -> {
                sliderButton = new GuiNineSliced(this.screen, BARS_ATLAS, 0, 0, buttonLength, thickness, 2, 2, 182, 5, 0, 65);
                sliderBackground = new GuiNineSliced(this.screen, BARS_ATLAS, 0, 0, length + buttonLength, thickness, 2, 2, 182, 5, 0, 60);
            }
        }
    }

    @Override
    public float getContentsWidth() {
        return 0;
    }

    @Override
    public float getContentsHeight() {
        return 0;
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        sliderBackground.draw(graphics, mouseX, mouseY, partialTick);
        sliderButton.draw(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        float newButtonPosition = currentValue / Math.abs(endValue - startValue) * length;
        switch (direction) {
            case Horizontal -> {
                sliderButton.setPosition(new Vec2(newButtonPosition, 0));
            }
            case Vertical -> {
                sliderButton.setPosition(new Vec2(0, newButtonPosition));
            }
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        float deltaPercentage = (float) (-delta * step);

        currentValue += deltaPercentage;
        currentValue = MathUtils.clamp(currentValue, startValue, endValue);
        if (this.sliderValueUpdated != null)
            this.sliderValueUpdated.onSliderValueUpdated(currentValue);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return sliderButton.isMouseOver(mouseX - this.position.x, mouseY - this.position.y) ||
                sliderBackground.isMouseOver(mouseX - this.position.x, mouseY - this.position.y);
    }

    @Override
    protected void mouseClickedContent(double mouseX, double mouseY, int button) {
        // Clicking on button
        if (sliderButton.isMouseOver(mouseX - this.position.x, mouseY - this.position.y)) {
            this.scrolling = button == 0;
            return;
        }
        // Clicking on background
        this.clickBackground(mouseX - this.position.x, mouseY - this.position.y, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.scrolling = false;
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            float mouseSpeed = (endValue - startValue) / length;
            double moved = direction == SliderDirection.Vertical ? deltaY * mouseSpeed : deltaX * mouseSpeed;
            currentValue += (float) moved;
            currentValue = MathUtils.clamp(currentValue, startValue, endValue);
            if (this.sliderValueUpdated != null)
                this.sliderValueUpdated.onSliderValueUpdated(currentValue);
        }
    }

    // Convert to a value
    protected void clickBackground(double mouseX, double mouseY, int button) {
        
    }

    public enum SliderDirection {
        Horizontal,
        Vertical
    }

    public interface SliderValueUpdated {
        public void onSliderValueUpdated(float currentValue);
    }
}
