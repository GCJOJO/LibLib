package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.lwjgl.glfw.GLFW;

@Getter
@Setter
public class GuiButton extends GuiElement {
    protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    protected GuiText text;
    protected GuiNineSliced inactiveNineSlice;
    protected GuiNineSliced activeNineSlice;
    protected GuiNineSliced hoveredNineSlice;

    protected int textPadding = 10;
    protected int buttonWidth;
    protected int buttonHeight;

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

    private void setup(Component textContents) {
        text = new GuiText(screen, textContents);
        text.setHorizontalAlignment(GuiText.TextHorizontalAlignment.Center);
        text.setVerticalAlignment(GuiText.TextVerticalAlignment.Center);

        buttonWidth = getMinimumButtonWidth();
        buttonHeight = getMinimumButtonHeight();

        int posX = (int) (-buttonWidth * 0.5f);
        int posY = (int) (-buttonHeight * 0.5f);

        inactiveNineSlice = new GuiNineSliced(screen, WIDGETS_LOCATION, posX, posY, buttonWidth, buttonHeight, 20, 4, 200, 20, 0, getAtlasTextureY(0));
        activeNineSlice = new GuiNineSliced(screen, WIDGETS_LOCATION, posX, posY, buttonWidth, buttonHeight, 20, 4, 200, 20, 0, getAtlasTextureY(1));
        hoveredNineSlice = new GuiNineSliced(screen, WIDGETS_LOCATION, posX, posY, buttonWidth, buttonHeight, 20, 4, 200, 20, 0, getAtlasTextureY(2));
    }

    public int getMinimumButtonWidth() {
        return (int) text.getContentsWidth() + textPadding;
    }

    public int getMinimumButtonHeight() {
        return (int) text.getContentsHeight() + textPadding;
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
    protected void drawContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!isActive())
            inactiveNineSlice.draw(graphics, mouseX, mouseY, partialTick);
        else if (isMouseOver(mouseX, mouseY))
            hoveredNineSlice.draw(graphics, mouseX, mouseY, partialTick);
        else
            activeNineSlice.draw(graphics, mouseX, mouseY, partialTick);
        text.draw(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int posX = (int) (-buttonWidth * 0.5f);
        int posY = (int) (-buttonHeight * 0.5f);

        Vec2 localPos = screenToLocal(new Vec2((float) mouseX, (float) mouseY));
        return localPos.x >= posX + drawOffset.x + position.x && localPos.x <= posX + drawOffset.x + position.x + getWidth() &&
                localPos.y >= posY + drawOffset.y + position.y && localPos.y <= posY + drawOffset.y + position.y + getHeight();
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {

    }

    @Override
    protected void mouseClickedContent(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && callback != null)
            callback.onGuiButtonClicked();
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

    }

    private int getAtlasTextureY(int id) {
        return 46 + id * 20;
    }

    public interface GuiButtonClicked {
        public void onGuiButtonClicked();
    }
}
