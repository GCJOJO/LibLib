package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiBoxContainer extends GuiContainer {
    protected BoxDirection direction;
    protected float childrenOffset = 0.0f;
    protected boolean childrenDirty = false;

    public GuiBoxContainer(Screen screen, int containerWidth, int containerHeight, BoxDirection direction) {
        super(screen, containerWidth, containerHeight);
        this.direction = direction;
    }

    @Override
    public float getContentsWidth() {
        if (direction == BoxDirection.Vertical) {
            float maxWidth = 0.0f;
            for (int i = 0; i <= children.size() - 1; i++) {
                maxWidth = Math.max(children.get(i).getWidth(), maxWidth);
            }

            return maxWidth;
        }

        float width = 0.0f;
        for (int i = 0; i <= children.size() - 1; i++) {
            width += children.get(i).getWidth();
        }

        return width;
    }

    @Override
    public float getContentsHeight() {
        if (direction == BoxDirection.Horizontal) {
            float maxHeight = 0.0f;
            for (int i = 0; i <= children.size() - 1; i++) {
                maxHeight = Math.max(children.get(i).getHeight(), maxHeight);
            }

            return maxHeight;
        }

        float height = 0.0f;
        for (int i = 0; i <= children.size() - 1; i++) {
            height += children.get(i).getHeight();
        }

        return height;
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    public void tick() {
        if (childrenDirty)
            recalculateChildrenPosition();

        children.forEach(GuiElement::tick);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {

    }

    @Override
    protected void mouseClickedContent(double mouseX, double mouseY, int button) {
        children.forEach(element -> {
            double localMouseX = mouseX - this.position.x;
            double localMouseY = mouseY - this.position.y;
            if (element.isMouseOver(localMouseX, localMouseY))
                element.mouseClickedContent(localMouseX, localMouseY, button);
        });
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

    }

    public boolean doesChildrenOverflow() {
        if (direction == BoxDirection.Horizontal)
            return getContentsWidth() > this.containerWidth;
        return getContentsHeight() > this.containerHeight;
    }

    @Override
    public void onChildrenUpdate() {
        childrenDirty = true;
    }

    public void recalculateChildrenPosition() {
        float offset = childrenOffset;
        for (int i = 0; i <= children.size() - 1; i++) {
            GuiElement child = children.get(i);
            Vec2 newPosition = Vec2.ZERO;

            switch (direction) {
                case Horizontal -> {
                    newPosition = new Vec2(position.x + offset, position.y);
                    offset += child.getContentsWidth();
                    break;
                }
                case Vertical -> {
                    newPosition = new Vec2(position.x, position.y + offset);
                    offset += child.getContentsHeight();
                    break;
                }
            }

            child.setPosition(newPosition);
        }
    }

    public enum BoxDirection {
        Vertical,
        Horizontal
    }
}
