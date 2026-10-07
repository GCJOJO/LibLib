package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.client.gui.ScissorAwareMultiLineEditBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

import java.util.function.Consumer;

public class GuiMultilineTextEdit extends GuiElement {
    protected ScissorAwareMultiLineEditBox multilineEditBox;

    public GuiMultilineTextEdit(Screen screen, Font font, int width, int height, Consumer<String> setValue) {
        super(screen);
        setup(font, width, height, "", setValue);
    }

    public GuiMultilineTextEdit(Screen screen, Font font, int width, int height, String value, Consumer<String> setValue) {
        super(screen);
        setup(font, width, height, value, setValue);
    }

    private void setup(Font font, int width, int height, String text, Consumer<String> setValue) {
        setSize(new Vec2(width, height));
        // Font font, int x, int y, int width, int height, Component placeholder, Component message
        multilineEditBox = new ScissorAwareMultiLineEditBox(font, (int)(-width * 0.5f), (int)(-height * 0.5f), width, height, Component.empty(), Component.literal(text));
        multilineEditBox.setValueListener(setValue);
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
        multilineEditBox.render(graphics, (int) mouseX, (int) mouseY, partialTick);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean inside = isMouseOver(mouseX, mouseY);
        this.isFocused = inside;
        multilineEditBox.setFocused(inside);
        return multilineEditBox.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return multilineEditBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return multilineEditBox.charTyped(codePoint, modifiers);
    }

    public String getValue() { return multilineEditBox.getValue(); }
    public void setValue(String s) { multilineEditBox.setValue(s); }
}
