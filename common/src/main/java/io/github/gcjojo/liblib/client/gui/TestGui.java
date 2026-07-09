package io.github.gcjojo.liblib.client.gui;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.client.gui.elements.*;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.tween.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class TestGui extends GuiScreen {
    GuiText helloWorldText;
    GuiColorRect colorRect;
    GuiImage dirtImage;
    GuiButton activeButton;
    GuiButton inactiveButton;
    GuiScrollbarContainer boxContainer;
    GuiScrollbarContainer boxContainer2;
    GuiProgressBar progressBar;

    int seconds = 0;

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
        //addElement(colorRect);

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

        boxContainer = new GuiScrollbarContainer(this, (int) (this.width * 0.12f), (int) (this.height * 0.4f), GuiBoxContainer.BoxDirection.Vertical);
        boxContainer.setPosition(new Vec2(10, 10));
        //boxContainer.setPosition(new Vec2((int) (this.width * 0.5f), (int) (this.height * 0.5f)));
        for (int i = 0; i <= 24; i++) {
            GuiText childText = new GuiText(this, Component.literal(String.format("Text %s", i)));
            boxContainer.addChild(childText);
        }
        for (int i = 0; i <= 24; i++) {
            String buttonName = String.format("Button %s", i);
            GuiButton childButton = new GuiButton(this, Component.literal(buttonName), () ->
                    LibLib.getLogger().info("Clicked on {}", buttonName));
            childButton.setDrawOffset(new Vec2(childButton.getButtonWidth() * 0.5f, childButton.getButtonHeight() * 0.5f));
            boxContainer.addChild(childButton);
        }

        addElement(boxContainer);

        boxContainer2 = new GuiScrollbarContainer(this, (int) (this.width * 0.12f), (int) (this.height * 0.4f), GuiBoxContainer.BoxDirection.Vertical);
        boxContainer2.setPosition(new Vec2(10, this.height * 0.5f));

        for (int i = 0; i <= 25; i++) {
            GuiText childText = new GuiText(this, Component.literal(String.format("Text %s", i)));
            boxContainer2.addChild(childText);
        }
        addElement(boxContainer2);

        progressBar = new GuiProgressBar(this, 0, 50, 25, 6, this.width / 2, GuiProgressBar.ProgressBarDirection.Horizontal, GuiProgressBar.BarColor.Green, GuiProgressBar.BarColor.DarkPurple);
        //progressBar.setPosition(new Vec2(this.width * 0.5f - progressBar.getLength() * 0.5f, this.height * 0.95f));

        addElement(progressBar);

        TweenSequence sequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);
        sequence.setParallel(true);

        TweenProperty<Vec2> posTweenProperty = sequence.tweenProperty(progressBar::getPosition, progressBar::setPosition, Interpolator.VEC2)
                .values(new Vec2(this.width * 0.5f - progressBar.getLength() * 0.5f, this.height + 100), new Vec2(this.width * 0.5f - progressBar.getLength() * 0.5f, this.height * 0.95f))
                .duration(1.0f)
                .easing(Easing.Cubic.EASE_IN_OUT);

        TweenProperty<Float> sliderContainerTweenProperty =
                sequence.tweenProperty(boxContainer::getSliderValue, boxContainer::setSliderValue, Interpolator.FLOAT)
                        .values(0.0f, 0.50f)
                        .duration(2.0f)
                        .easing(Easing.Cubic.EASE_IN_OUT);

        sequence.play();
    }

    @Override
    public void tick() {
        super.tick();
        helloWorldText.setAngle(helloWorldText.getAngle() + 0.25f);
        dirtImage.setAngle(dirtImage.getAngle() - 0.25f);

        if (currentTick % 20 == 0) seconds++;

        float progressBar2Value = progressBar.getStartValue() + (float) Math.cos(getCurrentTick() * 0.10) * Math.abs(progressBar.getStartValue() - progressBar.getEndValue());
        progressBar.setCurrentValue(progressBar2Value);

        //LibLib.getLogger().info("Pos : {}, {}", progressBar.getPosition().x, progressBar.getPosition().y);
    }
}
