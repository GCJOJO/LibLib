package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class GuiContainer extends GuiElement {
    protected List<GuiElement> children = new ArrayList<>();
    int containerWidth;
    int containerHeight;

    public GuiContainer(Screen screen, int containerWidth, int containerHeight) {
        super(screen);
        this.containerWidth = containerWidth;
        this.containerHeight = containerHeight;
    }

    public float getContainerWidth() {
        return this.containerWidth * scale.x;
    }

    public float getContainerHeight() {
        return this.containerHeight * scale.y;
    }

    public void addChild(GuiElement child) {
        children.add(child);
        onChildrenUpdate();
    }

    public Optional<GuiElement> getChild(int i) {
        if (i >= children.size()) return Optional.empty();
        return Optional.ofNullable(children.get(i));
    }

    public void removeChild(GuiElement child) {
        children.remove(child);
        onChildrenUpdate();
    }

    public void clearChildren() {
        children.clear();
        onChildrenUpdate();
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        graphics.enableScissor((int) this.position.x, (int) this.position.y, (int) this.position.x + containerWidth, (int) this.position.y + containerHeight);
        children.forEach(child -> child.draw(graphics, mouseX - this.position.x, mouseY - this.position.y, partialTick));
        graphics.disableScissor();
    }

    public abstract void onChildrenUpdate();
}
