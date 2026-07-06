package io.github.gcjojo.liblib.client.gui;

import io.github.gcjojo.liblib.client.gui.elements.GuiColorRect;
import io.github.gcjojo.liblib.client.gui.elements.GuiImage;
import io.github.gcjojo.liblib.client.gui.elements.GuiText;
import io.github.gcjojo.liblib.math.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class TestGui extends GuiScreen {
    public TestGui(Component component) {
        super(component);
    }

    @Override
    public void init() {
        clearElements();

        GuiText text = new GuiText(this, Component.literal("Hello World !"));
        text.setScale(new Vec2(2.0f, 2.0f));
        text.setAngle(25);
        text.setPosition(new Vec2((float) this.width * 0.5f, (float) this.height * 0.5f));
        text.setAlpha(180);
        text.setVerticalAlignment(GuiText.TextVerticalAlignment.Center);
        text.setHorizontalAlignment(GuiText.TextHorizontalAlignment.Center);

        addElement(text);

        GuiColorRect colorRect = new GuiColorRect(this, (int) (this.width * 0.25f), (int) (this.height * 0.7),
                (int) (this.width * 0.75f), (int) (this.height * 0.90), new Color(15, 15, 15, 255));
        addElement(colorRect);

        GuiImage image = new GuiImage(this, ResourceLocation.tryParse("minecraft:textures/block/dirt.png"), 32, 32);
        image.setPosition(new Vec2(this.width * 0.9f, this.height * 0.2f));
        image.setScale(new Vec2(2.0f, 2.0f));
        image.setAngle(-25f);
        image.setAlpha(180);

        addElement(image);
    }
}
