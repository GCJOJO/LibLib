package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screens.Screen;

@Getter
@Setter
public class GuiSliderContainer extends GuiBoxContainer {
    private static final int SCROLLBAR_WIDTH = 6;
    protected GuiSlider scrollbar;

    public GuiSliderContainer(Screen screen, int containerWidth, int containerHeight, BoxDirection direction) {
        super(screen, containerWidth, containerHeight, direction);
        this.scrollbar = new GuiSlider(screen, SCROLLBAR_WIDTH, this.containerHeight, 0, 1, 1,
                direction == BoxDirection.Vertical ? GuiSlider.SliderDirection.Vertical : GuiSlider.SliderDirection.Horizontal,
                this::onSliderValueChanged);
    }

    @Override
    public void onChildrenUpdate() {
        super.onChildrenUpdate();
        scrollbar.setEndValue(children.size());
    }

    public void onSliderValueChanged(float newValue) {
        if (children.isEmpty()) return;

        float newOffset = (-newValue / children.size()) * (direction == BoxDirection.Horizontal ? getContainerWidth() : getContentsHeight());
        this.setChildrenOffset(newValue);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollbar.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        scrollbar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
