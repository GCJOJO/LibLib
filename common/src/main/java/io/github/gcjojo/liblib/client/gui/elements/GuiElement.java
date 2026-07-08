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
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public abstract class GuiElement {
    public static boolean DEBUG_DRAW_PIVOT_POINT = false;
    public static boolean DEBUG_DRAW_BOUNDING_BOX = false;
    public static boolean DEBUG_DRAW_SCISSORS = false;

    protected Vec2 position = Vec2.ZERO;
    protected Vec2 drawOffset = Vec2.ZERO;
    protected float angle = 0.0f;
    protected Vec2 scale = Vec2.ONE;
    protected Color color = Color.WHITE;
    protected Vec2 rotationPivot = new Vec2(0.5f, 0.5f);
    protected boolean isVisible = true;

    protected List<GuiElement> children = new ArrayList<>();

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

    public Rect2i getBoundingBox() {
        return new Rect2i(0, 0, (int) getWidth(), (int) getHeight());
    }

    public void draw(GuiGraphics graphics, double mouseX, double mouseY, float partialTick, Vec2 parentPosition) {
        if (!isVisible() || color.alpha < 8) return;
        if (color.alpha >= 255) color.alpha = 255;

        if (supportsShaderColor()) {
            if (color.alpha != 255.0f)
                RenderSystem.enableBlend();

            RenderSystem.setShaderColor((float) color.red / 255, (float) color.blue / 255, (float) color.green / 255, (float) color.alpha / 255);
        }

        float rotationPivotX = getWidth() * (rotationPivot.x - 0.5f);
        float rotationPivotY = getHeight() * (rotationPivot.y - 0.5f);

        // Draw Rotation Pivot
        if (DEBUG_DRAW_PIVOT_POINT)
            graphics.fill((int) drawOffset.x + (int) position.x + (int) rotationPivotX - 2,
                    (int) drawOffset.y + (int) position.y + (int) rotationPivotY - 2,
                    (int) drawOffset.x + (int) position.x + (int) rotationPivotX + 2,
                    (int) drawOffset.y + (int) position.y + (int) rotationPivotY + 2,
                    0xAAFF0000);


        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();


        poseStack.translate(drawOffset.x + this.position.x, drawOffset.y + this.position.y, 0.0f);
        poseStack.scale(scale.x, scale.y, 1.0f);
        //Merci ClaudeSlop
        poseStack.rotateAround(Axis.ZP.rotationDegrees(angle), rotationPivotX, rotationPivotY, 0.0f);
        drawContents(graphics, mouseX, mouseY, partialTick);

        Vec2 finalPosition = new Vec2(drawOffset.x + position.x + parentPosition.x, drawOffset.y + position.y + parentPosition.y);
        graphics.enableScissor((int) (finalPosition.x - getBoundingBox().getWidth()), (int) (finalPosition.y - getBoundingBox().getHeight()),
                (int) (finalPosition.x + getBoundingBox().getWidth()), (int) (finalPosition.y + getBoundingBox().getHeight()));
        try {
            children.forEach(child -> {
                child.draw(graphics, mouseX, mouseY, partialTick, new Vec2(finalPosition.x, finalPosition.y));
            });
        } finally {
            graphics.disableScissor();
        }


        poseStack.popPose();
        if (DEBUG_DRAW_BOUNDING_BOX) {
            graphics.fill((int) (drawOffset.x + this.position.x - getBoundingBox().getWidth()), (int) (drawOffset.y + this.position.y - getBoundingBox().getHeight()),
                    (int) (drawOffset.x + this.position.x + getBoundingBox().getWidth()), (int) (drawOffset.y + this.position.y + getBoundingBox().getHeight()), 0xAA00FFFF);
        }

        if (DEBUG_DRAW_SCISSORS) {
            graphics.fill((int) (drawOffset.x + this.position.x - getBoundingBox().getWidth()), (int) (drawOffset.y + this.position.y - getBoundingBox().getHeight()),
                    (int) (drawOffset.x + this.position.x + getBoundingBox().getWidth()), (int) (drawOffset.y + this.position.y + getBoundingBox().getHeight()), 0xAAFF00FF);
        }

        if (supportsShaderColor() && color.alpha != 255)
            RenderSystem.disableBlend();
    }

    protected abstract void drawContents(GuiGraphics graphics, double mouseX, double mouseY, float partialTick);

    public abstract void tick();

    public void setAlpha(int newAlpha) {
        color.alpha = MathUtils.clamp(newAlpha, 0, 255);
    }

    public void addChild(GuiElement child) {
        children.add(child);
        onChildrenUpdate();
    }

    public Optional<GuiElement> getChild(int i) {
        if (i >= children.size()) return Optional.empty();
        return Optional.ofNullable(children.get(i));
    }

    public void removeChild(GuiElement child) {
        children.remove(child);
        onChildrenUpdate();
    }

    public void clearChildren() {
        children.clear();
        onChildrenUpdate();
    }

    public void onChildrenUpdate() {
    }

    // Merci Claude Slop
    public Vec2 localToScreen(Vec2 localPos) {
        float pivotWorldX = position.x + rotationPivot.x;
        float pivotWorldY = position.y + rotationPivot.y;

        float worldLocalX = localPos.x + position.x;
        float worldLocalY = localPos.y + position.y;

        double relX = worldLocalX - pivotWorldX;
        double relY = worldLocalY - pivotWorldY;

        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double rotX = relX * cos - relY * sin;
        double rotY = relX * sin + relY * cos;

        rotX *= scale.x;
        rotY *= scale.y;

        float screenX = (float) (rotX + pivotWorldX);
        float screenY = (float) (rotY + pivotWorldY);

        return new Vec2(screenX, screenY);
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

        return new Vec2(localX - this.position.x, localY - this.position.y);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        //return getBoundingBox().contains((int) (mouseX - this.position.x), (int) (mouseY - this.position.y));
        return mouseX >= drawOffset.x + this.position.x - getBoundingBox().getWidth() && mouseX <= drawOffset.x + this.position.x + getBoundingBox().getWidth() &&
                mouseY >= drawOffset.y + this.position.y - getBoundingBox().getHeight() && mouseY <= drawOffset.y + this.position.y + getBoundingBox().getHeight();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) return mouseClickedContent(mouseX, mouseY, button);
        return false;
    }

    protected boolean mouseClickedContent(double mouseX, double mouseY, int button) {
        if (children.isEmpty()) return false;
        Vec2 localPos = screenToLocal(new Vec2((float) mouseX, (float) mouseY));
        for (GuiElement child : children) {
            if (child.mouseClicked(localPos.x, localPos.y, button)) return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (children.isEmpty() || !isMouseOver(mouseX, mouseY)) return false;
        Vec2 localPos = screenToLocal(new Vec2((float) mouseX, (float) mouseY));
        for (GuiElement child : children) {
            if (child.mouseReleased(localPos.x, localPos.y, button)) return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (children.isEmpty()) return false;
        Vec2 localPos = screenToLocal(new Vec2((float) mouseX, (float) mouseY));
        Vec2 localDeltaPos = screenToLocal(new Vec2((float) deltaX, (float) deltaX));
        for (GuiElement child : children) {
            if (child.mouseDragged(localPos.x, localPos.y, button, localDeltaPos.x, localDeltaPos.y)) return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (children.isEmpty()) return false;
        Vec2 localPos = screenToLocal(new Vec2((float) mouseX, (float) mouseY));
        for (GuiElement child : children) {
            if (child.mouseScrolled(localPos.x, localPos.y, delta)) return true;
        }
        return false;
    }
}
