package io.github.gcjojo.liblib.client.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec2;

@Getter
@Setter
public abstract class GuiElement {
    protected Vec2 position = Vec2.ZERO;
    protected float angle = 0.0f;
    protected Vec2 scale = Vec2.ONE;
    protected Color color;
    protected Vec2 rotationPivot = new Vec2(0.5f, 0.5f);

    protected Screen screen;

    public GuiElement(Screen screen) {
        this.screen = screen;
    }

    public float getWidth() {
        return getContentsWidth() * scale.x;
    }

    public float getHeight() {
        return getContentsHeight() * scale.y;
    }

    public abstract float getContentsWidth();

    public abstract float getContentsHeight();

    public abstract boolean supportsShaderColor();

    public void draw(GuiGraphics graphics) {
        if (color.alpha < 8) return;
        if (color.alpha >= 255) color.alpha = 255;

        if (supportsShaderColor()) {
            if (color.alpha != 255.0f)
                RenderSystem.enableBlend();

            RenderSystem.setShaderColor((float) color.red / 255, (float) color.blue / 255, (float) color.green / 255, (float) color.alpha / 255);
        }

        float rotationPivotX = getWidth() * (rotationPivot.x - 0.5f);
        float rotationPivotY = getHeight() * (rotationPivot.y - 0.5f);

        // Draw Rotation Pivot
        /*graphics.fill((int) position.x + (int) rotationPivotX - 2,
                (int) position.y + (int) rotationPivotY - 2,
                (int) position.x + (int) rotationPivotX + 2,
                (int) position.y + (int) rotationPivotY + 2,
                0xFFFF0000);*/

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        poseStack.translate(position.x, position.y, 0.0f);
        poseStack.scale(scale.x, scale.y, 1.0f);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(angle), rotationPivotX, rotationPivotY, 0.0f);

        drawContents(graphics);

        poseStack.popPose();

        if (supportsShaderColor() && color.alpha != 255)
            RenderSystem.disableBlend();
    }

    protected abstract void drawContents(GuiGraphics graphics);

    public void setAlpha(int newAlpha) {
        color.alpha = MathUtils.clamp(newAlpha, 0, 255);
    }

    // Merci Claude Slop
    public Vec2 screenToLocal(Vec2 screenPos) {
        float pivotWorldX = position.x + rotationPivot.x;
        float pivotWorldY = position.y + rotationPivot.y;

        double relX = screenPos.x - pivotWorldX;
        double relY = screenPos.y - pivotWorldY;

        relX /= scale.x;
        relY /= scale.y;

        double rad = Math.toRadians(-angle); // inverse rotation
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        float localX = (float) (relX * cos - relY * sin) + pivotWorldX;
        float localY = (float) (relX * sin + relY * cos) + pivotWorldY;

        return new Vec2(localX, localY);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        Vec2 localPos = screenToLocal(new Vec2((float) mouseX, (float) mouseY));
        return localPos.x >= position.x && localPos.x <= position.x + getWidth() &&
                localPos.y >= position.y && localPos.y <= position.y + getHeight();
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) mouseClickedContent(mouseX, mouseY, button);
    }

    protected abstract void mouseClickedContent(double mouseX, double mouseY, int button);
}
