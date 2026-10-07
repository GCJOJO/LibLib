package io.github.gcjojo.liblib.client.gui.elements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec2;

public class GuiTextInput extends GuiElement {
    private GuiNineSliced textFieldTexture;
    private GuiNineSliced textFieldHightlightedTexture;

    protected String text;

    public GuiTextInput(Screen screen) {
        super(screen);
        setup(0, 0);
    }

    @Override
    protected void setSizeInternal(Vec2 newSize) {

    }

    public GuiTextInput(Screen screen, int width, int height)
    {
        super(screen);
        setup(width, height);
    }

    private void setup(int width, int height) {
        setSize(new Vec2(width, height));

        textFieldTexture = new GuiNineSliced(getScreen(), GuiNineSliced.TEXT_FIELD, 0, 0, width, height);
        textFieldHightlightedTexture = new GuiNineSliced(getScreen(), GuiNineSliced.TEXT_FIELD_HIGHLIGHTED, 0, 0, width, height);
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {

    }

    @Override
    public void tick() {

    }
}
