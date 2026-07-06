package io.github.gcjojo.liblib.client.gui;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.client.gui.elements.*;
import io.github.gcjojo.liblib.math.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class TestGui extends GuiScreen {
    GuiText helloWorldText;
    GuiColorRect colorRect;
    GuiImage dirtImage;
    GuiButton activeButton;
    GuiButton inactiveButton;
    GuiBoxContainer boxContainer;

    public TestGui(Component component) {
        super(component);
    }

    @Override
    public void init() {
        clearElements();

        helloWorldText = new GuiText(this, Component.literal("Hello World !"));
        helloWorldText.setScale(new Vec2(2.0f, 2.0f));
        helloWorldText.setPosition(new Vec2((float) this.width * 0.5f, (float) this.height * 0.5f));
        helloWorldText.setAlpha(180);
        helloWorldText.setVerticalAlignment(GuiText.TextVerticalAlignment.Center);
        helloWorldText.setHorizontalAlignment(GuiText.TextHorizontalAlignment.Center);

        addElement(helloWorldText);

        colorRect = new GuiColorRect(this, (int) (this.width * 0.25f), (int) (this.height * 0.7),
                (int) (this.width * 0.75f), (int) (this.height * 0.90), new Color(15, 15, 15, 255));
        addElement(colorRect);

        dirtImage = new GuiImage(this, ResourceLocation.tryParse("minecraft:textures/block/dirt.png"), 32, 32);
        dirtImage.setPosition(new Vec2(this.width * 0.9f, this.height * 0.2f));
        dirtImage.setScale(new Vec2(2.0f, 2.0f));
        dirtImage.setAlpha(180);

        addElement(dirtImage);

        activeButton = new GuiButton(this, Component.literal("Hey there !"), () -> LibLib.getLogger().info("Button Pressed !"));
        activeButton.setPosition(new Vec2(this.width * 0.5f, this.height * 0.1f));
        addElement(activeButton);

        inactiveButton = new GuiButton(this, Component.literal("I'm inactive !"));
        inactiveButton.setPosition(new Vec2(this.width * 0.5f, this.height * 0.2f));
        inactiveButton.setActive(false);
        addElement(inactiveButton);

        boxContainer = new GuiSliderContainer(this, (int) (this.width * 0.12f), (int) (this.height * 0.4f), GuiBoxContainer.BoxDirection.Vertical);
        boxContainer.setPosition(new Vec2(10, 10));
        for (int i = 0; i <= 25; i++) {
            GuiText childText = new GuiText(this, Component.literal(String.format("Text %s", i)));
            boxContainer.addChild(childText);
        }
        addElement(boxContainer);

    }

    @Override
    public void tick() {
        super.tick();
        helloWorldText.setAngle(helloWorldText.getAngle() + 0.25f);
        dirtImage.setAngle(dirtImage.getAngle() - 0.25f);
    }
}
