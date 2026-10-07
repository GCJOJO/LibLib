package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiBoxContainer extends GuiContainer {
    protected BoxDirection direction;
    protected float childrenMargin = 5.0f;

    protected float childrenOffset = 0.0f;
    protected boolean childrenDirty = false;

    public GuiBoxContainer(Screen screen, int containerWidth, int containerHeight, BoxDirection direction) {
        super(screen, containerWidth, containerHeight);
        this.direction = direction;
    }

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
            width += children.get(i).getWidth() + getChildrenMargin();
        }

        return width;
    }

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
            height += children.get(i).getHeight() + getChildrenMargin();
        }

        return height;
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    public boolean isChildrenVisible(GuiElement child) {
        if (direction == BoxDirection.Horizontal)
            return child.position.x + child.getWidth() >= 0 && child.position.x <= this.getWidth();
        return child.position.y + child.getHeight() >= 0 && child.position.y <= this.getHeight();
    }

    @Override
    public void tick() {
        if (childrenDirty)
            recalculateChildrenPosition();

        children.forEach(child -> {
            boolean visible = isChildrenVisible(child);
            child.setVisible(visible);
            if (visible)
                child.tick();
        });
    }

    public boolean doesChildrenOverflow() {
        if (direction == BoxDirection.Horizontal)
            return getContentsWidth() > this.getWidth();
        return getContentsHeight() > this.getHeight();
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
                    newPosition = new Vec2(offset, 0);
                    offset += child.getWidth() + childrenMargin;
                    break;
                }
                case Vertical -> {
                    newPosition = new Vec2(0, offset);
                    offset += child.getHeight() + childrenMargin;
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
