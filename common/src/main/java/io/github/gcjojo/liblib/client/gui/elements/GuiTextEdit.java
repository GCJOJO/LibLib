package io.github.gcjojo.liblib.client.gui.elements;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

import java.util.function.Consumer;

public class GuiTextEdit extends GuiElement {
    protected EditBox editBox;

    public GuiTextEdit(Screen screen, Font font, int width, int height, String value, Consumer<String> setValue) {
        super(screen);
        setSize(new Vec2(width, height));
        editBox = new EditBox(font, (int)(-width * 0.5f), (int)(-height * 0.5f), width, height, editBox, Component.literal(value));
        editBox.setMaxLength(256);
        editBox.setResponder(setValue);
    }

    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i(-1, 0, (int) (getWidth() * 0.5f) + 1, (int) (getHeight() * 0.5f) + 2);
    }

    @Override
    protected void setSizeInternal(Vec2 newSize) {

    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        editBox.renderWidget(graphics, (int)mouseX, (int)mouseY, partialTick);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean inside = isMouseOver(mouseX, mouseY);
        this.isFocused = inside;
        editBox.setFocused(inside);
        return editBox.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return editBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return editBox.charTyped(codePoint, modifiers);
    }

    public String getValue() { return editBox.getValue(); }
    public void setValue(String s) { editBox.setValue(s); }
}
