package io.github.gcjojo.liblib.client.gui.elements;

import io.github.gcjojo.liblib.math.Color;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

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
    protected int imageWidth;
    protected int imageHeight;
    protected ImageHorizontalAlignment imageHorizontalAlignment;
    protected ImageVerticalAlignment imageVerticalAlignment;

    public GuiImage(Screen screen, ResourceLocation imagePath, int imageWidth, int imageHeight) {
        super(screen);
        this.imagePath = imagePath;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.color = Color.WHITE;

        imageHorizontalAlignment = ImageHorizontalAlignment.CENTER;
        imageVerticalAlignment = ImageVerticalAlignment.CENTER;
    }

    public GuiImage(Screen screen, ResourceLocation imagePath, int imageWidth, int imageHeight,
                    ImageHorizontalAlignment horizontalAlignment, ImageVerticalAlignment verticalAlignment) {
        super(screen);
        this.imagePath = imagePath;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.color = Color.WHITE;

        imageHorizontalAlignment = horizontalAlignment;
        imageVerticalAlignment = verticalAlignment;
    }

    @Override
    public Rect2i getBoundingBox() {
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    public float getContentsWidth() {
        return imageWidth;
    }

    @Override
    public float getContentsHeight() {
        return imageHeight;
    }

    @Override
    public boolean supportsShaderColor() {
        return true;
    }

    @Override
    protected void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick) {
        int x = 0;
        int y = 0;

        switch (imageHorizontalAlignment) {
            case CENTER -> x = -imageWidth / 2;
            case RIGHT -> x = -imageWidth;
        }

        switch (imageVerticalAlignment) {
            case CENTER -> y = -imageHeight / 2;
            case BOTTOM -> y = -imageHeight;
        }

        graphics.blit(imagePath, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
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
