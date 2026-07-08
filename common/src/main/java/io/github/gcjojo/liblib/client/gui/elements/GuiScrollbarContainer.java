package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec2;

// TODO Fix not scrolling when dragging mouse or clicking on the scrollbar
@Getter
@Setter
public class GuiScrollbarContainer extends GuiBoxContainer {
    private static final int SCROLLBAR_WIDTH = 6;
    protected GuiSlider scrollbar;

    public GuiScrollbarContainer(Screen screen, int containerWidth, int containerHeight, BoxDirection direction) {
        super(screen, containerWidth, containerHeight, direction);
        int length = direction == BoxDirection.Vertical ? (int) getContainerHeight() : (int) getContainerWidth();
        length = (int) (length * 0.5f);

        this.scrollbar = new GuiSlider(screen, SCROLLBAR_WIDTH, length, 0, 1, 0.05f,
                direction == BoxDirection.Vertical ? GuiSlider.SliderDirection.Vertical : GuiSlider.SliderDirection.Horizontal,
                this::onSliderValueChanged);
    }

    public float getSliderValue() {
        return this.scrollbar.currentValue;
    }

    public void setSliderValue(float newValue) {
        this.scrollbar.currentValue = newValue;
        onSliderValueChanged(newValue);
    }

    public float getContentsSize() {
        return direction == BoxDirection.Vertical ? getContentsHeight() : getContentsWidth();
    }

    public float getContainerSize() {
        return direction == BoxDirection.Vertical ? getContainerHeight() : getContainerWidth();
    }

    @Override
    public void onChildrenUpdate() {
        super.onChildrenUpdate();
        //scrollbar.setStep((float) 1 / getContentsSize());

        int length = direction == BoxDirection.Vertical ? this.containerHeight : this.containerWidth;
        scrollbar.setLength(length);
        scrollbar.refresh();

        switch (direction) {
            case Horizontal -> scrollbar.setPosition(new Vec2(0, this.getContainerHeight()));
            case Vertical -> scrollbar.setPosition(new Vec2(this.getContainerWidth() * 0.5f, 0));
        }
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        scrollbar.setVisible(doesChildrenOverflow());
        super.drawContents(graphics, mouseX, mouseY, partialTick);

        scrollbar.draw(graphics, mouseX - this.position.x, mouseY - this.position.y, partialTick, this.position);
    }

    public void onSliderValueChanged(float newValue) {
        if (children.isEmpty() && !doesChildrenOverflow()) return;

        float newOffset = (-newValue) * (getContentsSize() - getContainerSize() * 0.9f);
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

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (scrollbar.isMouseOver(mouseX, mouseY))
                return scrollbar.mouseClickedContent(mouseX, mouseY, button);
            return mouseClickedContent(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (doesChildrenOverflow())
            return scrollbar.mouseScrolled(mouseX, mouseY, delta);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (doesChildrenOverflow())
            return scrollbar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return false;
    }
}
