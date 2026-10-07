package io.github.gcjojo.liblib.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Vector3f;

// Claude Slop
public class ScissorAwareMultiLineEditBox extends MultiLineEditBox {

    public ScissorAwareMultiLineEditBox(Font font, int i, int j, int k, int l, Component component, Component component2) {
        super(font, i, j, k, l, component, component2);
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;

        this.renderBackground(g);

        Matrix4f m = g.pose().last().pose();
        Vector3f tl = m.transformPosition(getX() + 1, getY() + 1, 0, new Vector3f());
        Vector3f br = m.transformPosition(getX() + getWidth() - 1, getY() + getHeight() - 1, 0, new Vector3f());

        g.enableScissor((int) tl.x, (int) tl.y, (int) br.x, (int) br.y);
        g.pose().pushPose();
        g.pose().translate(0.0, -this.scrollAmount(), 0.0);
        this.renderContents(g, mouseX, mouseY, partialTick);
        g.pose().popPose();
        g.disableScissor();

        this.renderDecorations(g);
    }
}