package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
public class GuiNineSliced extends GuiElement {
    protected ResourceLocation atlasLocation;
    protected int x;
    protected int y;
    protected int nineSliceWidth;
    protected int nineSliceHeight;
    protected int sliceWidth;
    protected int sliceHeight;
    protected int uWidth;
    protected int vHeight;
    protected int textureX;
    protected int textureY;

    public GuiNineSliced(Screen screen, ResourceLocation atlasLocation, int x, int y, int nineSliceWidth, int nineSliceHeight,
                         int sliceWidth, int sliceHeight,
                         int uWidth, int vHeight,
                         int textureX, int textureY) {
        super(screen);
        this.atlasLocation = atlasLocation;
        this.x = x;
        this.y = y;
        this.nineSliceWidth = nineSliceWidth;
        this.nineSliceHeight = nineSliceHeight;
        this.sliceWidth = sliceWidth;
        this.sliceHeight = sliceHeight;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    @Override
    public float getContentsWidth() {
        return nineSliceWidth;
    }

    @Override
    public float getContentsHeight() {
        return nineSliceHeight;
    }

    @Override
    public boolean supportsShaderColor() {
        return false;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        graphics.blitNineSliced(atlasLocation, x, y, nineSliceWidth, nineSliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, textureX, textureY);
    }

    @Override
    public void tick() {

    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double delta) {

    }

    @Override
    protected void mouseClickedContent(double mouseX, double mouseY, int button) {

    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

    }
}
