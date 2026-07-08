package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

// TODO Fix button width and height not repositioning the button correctly
@Getter
@Setter
public class GuiButton extends GuiElement {
    protected GuiText text;
    protected GuiNineSliced inactiveNineSlice;
    protected GuiNineSliced activeNineSlice;
    protected GuiNineSliced hoveredNineSlice;

    protected int textPadding = 10;
    protected int buttonWidth = 0;
    protected int buttonHeight = 0;

    protected boolean isActive = true;
    protected GuiButtonClicked callback = null;

    public GuiButton(Screen screen, Component textContents) {
        super(screen);
        setup(textContents);
    }

    public GuiButton(Screen screen, Component textContents, GuiButtonClicked callback) {
        super(screen);
        setup(textContents);
        this.callback = callback;
    }

    @Override
    public Rect2i getBoundingBox() {
        //return new Rect2i((int) (-buttonWidth * 0.5f), (int) (-buttonHeight * 0.5f), (int) (buttonWidth), (int) (buttonHeight));
        return new Rect2i(-1, 0, (int) (buttonWidth * 0.5f) + 1, (int) (buttonHeight * 0.5f) + 2);
    }

    private void setup(Component textContents) {
        text = new GuiText(screen, textContents);
        text.setHorizontalAlignment(GuiText.TextHorizontalAlignment.Center);
        text.setVerticalAlignment(GuiText.TextVerticalAlignment.Center);

        buttonWidth = getMinimumButtonWidth();
        buttonHeight = getMinimumButtonHeight();

        int buttonX = (int) (-buttonWidth * 0.5f);
        int buttonY = (int) (-buttonHeight * 0.5f);

        inactiveNineSlice = new GuiNineSliced(screen, GuiNineSliced.WIDGETS_ATLAS, buttonX, buttonY, buttonWidth, buttonHeight, 20, 4, 200, 20, 0, getAtlasTextureY(0));
        activeNineSlice = new GuiNineSliced(screen, GuiNineSliced.WIDGETS_ATLAS, buttonX, buttonY, buttonWidth, buttonHeight, 20, 4, 200, 20, 0, getAtlasTextureY(1));
        hoveredNineSlice = new GuiNineSliced(screen, GuiNineSliced.WIDGETS_ATLAS, buttonX, buttonY, buttonWidth, buttonHeight, 20, 4, 200, 20, 0, getAtlasTextureY(2));

        inactiveNineSlice.setVisible(false);
        activeNineSlice.setVisible(false);
        hoveredNineSlice.setVisible(false);

        addChild(inactiveNineSlice);
        addChild(activeNineSlice);
        addChild(hoveredNineSlice);
        addChild(text);
    }

    public int getMinimumButtonWidth() {
        return (int) text.getContentsWidth() + textPadding;
    }

    public int getMinimumButtonHeight() {
        return (int) text.getContentsHeight() + textPadding;
    }

    public void setButtonWidth(float newWidth) {
        buttonWidth = (int) Math.max(newWidth, getMinimumButtonWidth());
    }

    public void setButtonHeight(float newHeight) {
        buttonHeight = (int) Math.max(newHeight, getMinimumButtonHeight());
    }

    @Override
    public float getContentsWidth() {
        return buttonWidth;
    }

    @Override
    public float getContentsHeight() {
        return buttonHeight;
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
        inactiveNineSlice.setNineSliceWidth(getButtonWidth());
        inactiveNineSlice.setNineSliceHeight(getButtonHeight());

        activeNineSlice.setNineSliceWidth(getButtonWidth());
        activeNineSlice.setNineSliceHeight(getButtonHeight());

        hoveredNineSlice.setNineSliceWidth(getButtonWidth());
        hoveredNineSlice.setNineSliceHeight(getButtonHeight());
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
