package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiSliderContainer extends GuiBoxContainer {
    private static final int SCROLLBAR_WIDTH = 6;
    protected GuiSlider scrollbar;

    public GuiSliderContainer(Screen screen, int containerWidth, int containerHeight, BoxDirection direction) {
        super(screen, containerWidth, containerHeight, direction);
        int length = direction == BoxDirection.Vertical ? this.containerHeight : this.containerWidth;
        length = (int) (length * 0.5f);

        this.scrollbar = new GuiSlider(screen, SCROLLBAR_WIDTH, length, 1, 2, 1,
                direction == BoxDirection.Vertical ? GuiSlider.SliderDirection.Vertical : GuiSlider.SliderDirection.Horizontal,
                this::onSliderValueChanged);
    }

    @Override
    public void onChildrenUpdate() {
        super.onChildrenUpdate();
        scrollbar.setEndValue(children.size() + 1);

        int length = direction == BoxDirection.Vertical ? this.containerHeight : this.containerWidth;
        scrollbar.setLength(length);
        scrollbar.refresh();

        switch (direction) {
            case Horizontal -> scrollbar.setPosition(new Vec2(0, this.getContainerHeight() + SCROLLBAR_WIDTH));
            case Vertical -> scrollbar.setPosition(new Vec2(this.getContainerWidth() + SCROLLBAR_WIDTH, 0));
        }
    }

    @Override
    protected void drawContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.drawContents(graphics, mouseX, mouseY, partialTick);

        if (doesChildrenOverflow())
            scrollbar.drawContents(graphics, mouseX, mouseY, partialTick);
    }

    public void onSliderValueChanged(float newValue) {
        if (children.isEmpty() && !doesChildrenOverflow()) return;

        float newOffset = (-newValue / children.size()) * (direction == BoxDirection.Horizontal ? getContainerWidth() : getContainerHeight());
        this.setChildrenOffset(newOffset);
    }

    @Override
    public void tick() {
        super.tick();
        scrollbar.tick();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || scrollbar.isMouseOver(mouseX, mouseY);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (scrollbar.isMouseOver(mouseX, mouseY))
                scrollbar.mouseClickedContent(mouseX, mouseY, button);
            else
                mouseClickedContent(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        if (doesChildrenOverflow())
            scrollbar.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (doesChildrenOverflow())
            scrollbar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
}
