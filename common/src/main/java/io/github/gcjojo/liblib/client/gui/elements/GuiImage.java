package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.math.Color;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public class GuiImage extends GuiElement {
    // TODO Remove
    /*public static ResourceLocation BOSS_BAR_BLUE_BACKGROUND = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/blue_background.png");
    public static ResourceLocation BOSS_BAR_BLUE_PROGRESS = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/blue_progress.png");
    public static ResourceLocation BOSS_BAR_GREEN_BACKGROUND = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/green_background.png");
    public static ResourceLocation BOSS_BAR_GREEN_PROGRESS = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/green_progress.png");
    public static ResourceLocation BOSS_BAR_PINK_BACKGROUND = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/pink_background.png");
    public static ResourceLocation BOSS_BAR_PINK_PROGRESS = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/pink_progress.png");
    public static ResourceLocation BOSS_BAR_PURPLE_BACKGROUND = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/purple_background.png");
    public static ResourceLocation BOSS_BAR_PURPLE_PROGRESS = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/purple_progress.png");
    public static ResourceLocation BOSS_BAR_RED_BACKGROUND = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/red_background.png");
    public static ResourceLocation BOSS_BAR_RED_PROGRESS = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/red_progress.png");
    public static ResourceLocation BOSS_BAR_WHITE_BACKGROUND = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/white_background.png");
    public static ResourceLocation BOSS_BAR_WHITE_PROGRESS = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/white_progress.png");
    public static ResourceLocation BOSS_BAR_YELLOW_BACKGROUND = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/yellow_background.png");
    public static ResourceLocation BOSS_BAR_YELLOW_PROGRESS = ResourceLocation.tryParse("textures/gui/sprites/boss_bar/yellow_progress.png");*/
    protected ResourceLocation imagePath;
    protected ImageHorizontalAlignment imageHorizontalAlignment;
    protected ImageVerticalAlignment imageVerticalAlignment;

    public GuiImage(Screen screen, ResourceLocation imagePath, int imageWidth, int imageHeight) {
        super(screen);
        this.imagePath = imagePath;
        this.size = new Vec2(imageWidth, imageHeight);
        this.color = Color.WHITE;

        imageHorizontalAlignment = ImageHorizontalAlignment.CENTER;
        imageVerticalAlignment = ImageVerticalAlignment.CENTER;
    }

    public GuiImage(Screen screen, ResourceLocation imagePath, int imageWidth, int imageHeight,
                    ImageHorizontalAlignment horizontalAlignment, ImageVerticalAlignment verticalAlignment) {
        super(screen);
        this.imagePath = imagePath;
        this.size = new Vec2(imageWidth, imageHeight);
        this.color = Color.WHITE;

        imageHorizontalAlignment = horizontalAlignment;
        imageVerticalAlignment = verticalAlignment;
    }

    @Override
    protected void setSizeInternal(Vec2 newSize) {
        resize();
    }

    public void resize() {

    }

    @Override
    public boolean supportsShaderColor() {
        return true;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        float x = 0;
        float y = 0;

        switch (imageHorizontalAlignment) {
            case CENTER -> x = -getWidth() / 2;
            case RIGHT -> x = -getWidth();
        }

        switch (imageVerticalAlignment) {
            case CENTER -> y = -getHeight() / 2;
            case BOTTOM -> y = -getHeight();
        }

        graphics.blit(imagePath, (int)x, (int)y, 0, 0, (int)getWidth(), (int)getHeight(), (int)getWidth(), (int)getHeight());
    }

    @Override
    public void tick() {

    }

    public enum ImageVerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    public enum ImageHorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }
}
