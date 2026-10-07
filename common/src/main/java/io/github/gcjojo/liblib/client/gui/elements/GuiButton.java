package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.lwjgl.glfw.GLFW;

// TODO Fix button textWidth and height not repositioning the button correctly
@Getter
@Setter
public class GuiButton extends GuiElement {
    protected GuiText text;
    protected GuiNineSliced inactiveNineSlice;
    protected GuiNineSliced activeNineSlice;
    protected GuiNineSliced hoveredNineSlice;

    protected int textPadding = 10;

    protected boolean isActive = true;
    protected GuiButtonClicked callback = null;

    public GuiButton(Screen screen, Component textContents) {
        super(screen);
        setup(textContents, 0, 0);
    }

    public GuiButton(Screen screen, Component textContents, GuiButtonClicked callback) {
        super(screen);
        setup(textContents, 0, 0);
        this.callback = callback;
    }

    @Override
    public Rect2i getBoundingBox() {
        //return new Rect2i((int) (-buttonWidth * 0.5f), (int) (-buttonHeight * 0.5f), (int) (buttonWidth), (int) (buttonHeight));
        return new Rect2i(-1, 0, (int) (getWidth() * 0.5f) + 1, (int) (getHeight() * 0.5f) + 2);
    }

    private void setup(Component textContents, float width, float height) {
        text = new GuiText(screen, textContents);
        text.setHorizontalAlignment(GuiText.TextHorizontalAlignment.Center);
        text.setVerticalAlignment(GuiText.TextVerticalAlignment.Center);

        int buttonX = (int) (-getWidth());
        int buttonY = (int) (-getHeight());

        inactiveNineSlice = new GuiNineSliced(screen, GuiNineSliced.BUTTON_DISABLED, buttonX, buttonY, (int) getWidth(), (int) getHeight());
        activeNineSlice = new GuiNineSliced(screen, GuiNineSliced.BUTTON, buttonX, buttonY, (int) getWidth(), (int) getHeight());
        hoveredNineSlice = new GuiNineSliced(screen, GuiNineSliced.BUTTON_HOVERED, buttonX, buttonY, (int) getWidth(), (int) getHeight());

        inactiveNineSlice.setVisible(false);
        activeNineSlice.setVisible(false);
        hoveredNineSlice.setVisible(false);

        addChild(inactiveNineSlice);
        addChild(activeNineSlice);
        addChild(hoveredNineSlice);
        addChild(text);

        setWidth(Math.max(width, getMinimumButtonWidth()));
        setHeight(Math.max(height, getMinimumButtonHeight()));
    }

    public int getMinimumButtonWidth() {
        return (int) text.getWidth() + textPadding;
    }

    public int getMinimumButtonHeight() {
        return (int) text.getHeight() + textPadding;
    }

    @Override
    protected void setSizeInternal(Vec2 newSize) {
        resize();
        if(newSize.x < getMinimumButtonWidth() || newSize.y < getMinimumButtonHeight())
            setSize(new Vec2(Math.max(newSize.x, getMinimumButtonWidth()), Math.max(newSize.y, getMinimumButtonHeight())));
    }

    protected void resize() {
        inactiveNineSlice.setNineSliceWidth((int) Math.max(getWidth(), getMinimumButtonHeight()));
        inactiveNineSlice.setNineSliceHeight((int) Math.max(getHeight(), getMinimumButtonHeight()));

        activeNineSlice.setNineSliceWidth((int) Math.max(getWidth(), getMinimumButtonHeight()));
        activeNineSlice.setNineSliceHeight((int) Math.max(getHeight(), getMinimumButtonHeight()));

        hoveredNineSlice.setNineSliceWidth((int) Math.max(getWidth(), getMinimumButtonHeight()));
        hoveredNineSlice.setNineSliceHeight((int) Math.max(getHeight(), getMinimumButtonHeight()));

        int buttonX = (int) (-Math.max(getWidth(), getMinimumButtonHeight()) * 0.5f);
        int buttonY = (int) (-Math.max(getHeight(), getMinimumButtonHeight()) * 0.5f);

        inactiveNineSlice.setPosition(new Vec2(buttonX, buttonY));
        activeNineSlice.setPosition(new Vec2(buttonX, buttonY));
        hoveredNineSlice.setPosition(new Vec2(buttonX, buttonY));
    }

    @Override
    public boolean supportsShaderColor() {
        return true;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        if (isMouseOver(mouseX, mouseY) && isActive) {
            activeNineSlice.setVisible(false);
            hoveredNineSlice.setVisible(true);
        } else if (isActive) {
            activeNineSlice.setVisible(true);
            hoveredNineSlice.setVisible(false);
        } else {
            inactiveNineSlice.setVisible(true);
            activeNineSlice.setVisible(false);
            hoveredNineSlice.setVisible(false);
        }
    }

    @Override
    public void tick() {

    }

    @Override
    protected boolean mouseClickedContent(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && callback != null) {
            callback.onGuiButtonClicked();
            return true;
        }
        return super.mouseClickedContent(mouseX, mouseY, button);
    }


    private int getAtlasTextureY(int id) {
        return 46 + id * 20;
    }

    public interface GuiButtonClicked {
        public void onGuiButtonClicked();
    }
}
