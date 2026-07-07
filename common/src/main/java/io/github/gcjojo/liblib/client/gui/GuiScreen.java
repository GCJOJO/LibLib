package io.github.gcjojo.liblib.client.gui;

import io.github.gcjojo.liblib.client.gui.elements.GuiElement;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GuiScreen extends Screen {
    List<GuiElement> elements = new ArrayList<>();
    boolean renderBackground = true;
    int currentTick = 0;

    public GuiScreen(Component component) {
        super(component);
    }

    public void addElement(GuiElement newElement) {
        this.elements.add(newElement);
    }

    public void removeElement(GuiElement removedElement) {
        this.elements.remove(removedElement);
    }

    public void clearElements() {
        this.elements.clear();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
        elements.clear();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (renderBackground)
            renderBackground(graphics);

        elements.forEach(element -> element.draw(graphics, mouseX, mouseY, partialTick));
    }

    @Override
    public void tick() {
        currentTick++;
        elements.forEach(GuiElement::tick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        elements.forEach(element -> element.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        elements.forEach(element -> element.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        elements.forEach(element -> element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        elements.forEach(element -> {
            if (element.isMouseOver(mouseX, mouseY))
                element.mouseScrolled(mouseX, mouseY, scroll);
        });

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }
}
