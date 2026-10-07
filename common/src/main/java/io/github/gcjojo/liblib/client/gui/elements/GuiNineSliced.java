package io.github.gcjojo.liblib.client.gui.elements;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiNineSliced extends GuiElement {
    public static final ResourceLocation BUTTON = ResourceLocation.tryParse("widget/button");
    public static final ResourceLocation BUTTON_DISABLED = ResourceLocation.tryParse("widget/button_disabled");
    public static final ResourceLocation BUTTON_HOVERED = ResourceLocation.tryParse("widget/button_highlighted");

    public static final ResourceLocation TEXT_FIELD = ResourceLocation.tryParse("widget/text_field");
    public static final ResourceLocation TEXT_FIELD_HIGHLIGHTED = ResourceLocation.tryParse("widget/text_field_highlighted");

    protected ResourceLocation atlasLocation;
    protected int nineSliceWidth;
    protected int nineSliceHeight;

    public GuiNineSliced(Screen screen, ResourceLocation atlasLocation, int x, int y, int nineSliceWidth, int nineSliceHeight) {
        super(screen);
        this.atlasLocation = atlasLocation;
        this.setPosition(new Vec2(x, y));
        this.nineSliceWidth = nineSliceWidth;
        this.nineSliceHeight = nineSliceHeight;
    }

    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i(0, 0, 0, 0);
    }

    public float getContentsWidth() {
        return nineSliceWidth;
    }

    public float getContentsHeight() {
        return nineSliceHeight;
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
        try {
            //graphics.blitSprite(atlasLocation, nineSliceWidth, nineSliceHeight, sliceWidth, sliceHeight, uWidth, vHeight, textureX, textureY);
            graphics.blitSprite(atlasLocation, 0, 0, nineSliceWidth, nineSliceHeight);
             
        } catch (ArithmeticException e) {
            //LibLib.getLogger().warn("Error when drawing GuiNineSliced");
        }
    }

    @Override
    public void tick() {

    }


}
