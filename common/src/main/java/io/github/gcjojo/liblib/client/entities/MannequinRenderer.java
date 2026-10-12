package io.github.gcjojo.liblib.client.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.gcjojo.liblib.entity.Mannequin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MannequinRenderer extends LivingEntityRenderer<Mannequin, PlayerModel<Mannequin>> {

    private final PlayerModel<Mannequin> wideModel;
    private final PlayerModel<Mannequin> slimModel;


    public MannequinRenderer(EntityRendererProvider.Context context, boolean slim) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.wideModel = this.model;
        this.slimModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);


        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ArrowLayer<>(context, this));
        //this.addLayer(new Deadmau5EarsLayer(this));
        //this.addLayer(new CapeLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
        this.addLayer(new SpinAttackEffectLayer<>(this, context.getModelSet()));
        this.addLayer(new BeeStingerLayer<>(this));
    }

    @Override
    public void render(Mannequin mannequin, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        this.model = getSkin(mannequin).model() == PlayerSkin.Model.SLIM ? this.slimModel : this.wideModel;
        this.setModelProperties(mannequin);
        super.render(mannequin, yaw, partialTick, poseStack, buffer, light);
    }

    private void setModelProperties(Mannequin mannequin) {
        PlayerModel<Mannequin> playerModel = this.getModel();
        if (mannequin.isSpectator()) {
            playerModel.setAllVisible(false);
            playerModel.head.visible = true;
            playerModel.hat.visible = true;
        } else {
            playerModel.setAllVisible(true);
            playerModel.hat.visible = mannequin.isModelPartShown(PlayerModelPart.HAT);
            playerModel.jacket.visible = mannequin.isModelPartShown(PlayerModelPart.JACKET);
            playerModel.leftPants.visible = mannequin.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playerModel.rightPants.visible = mannequin.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playerModel.leftSleeve.visible = mannequin.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playerModel.rightSleeve.visible = mannequin.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playerModel.crouching = mannequin.isCrouching();
            HumanoidModel.ArmPose armPose = getArmPose(mannequin, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose armPose2 = getArmPose(mannequin, InteractionHand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                armPose2 = mannequin.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (mannequin.getMainArm() == HumanoidArm.RIGHT) {
                playerModel.rightArmPose = armPose;
                playerModel.leftArmPose = armPose2;
            } else {
                playerModel.rightArmPose = armPose2;
                playerModel.leftArmPose = armPose;
            }
        }
    }

    private static HumanoidModel.ArmPose getArmPose(Mannequin mannequin, InteractionHand interactionHand) {
        ItemStack itemStack = mannequin.getItemInHand(interactionHand);
        if (itemStack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (mannequin.getUsedItemHand() == interactionHand && mannequin.getUseItemRemainingTicks() > 0) {
                UseAnim useAnim = itemStack.getUseAnimation();
                if (useAnim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useAnim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useAnim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useAnim == UseAnim.CROSSBOW && interactionHand == mannequin.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useAnim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (useAnim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }

                if (useAnim == UseAnim.BRUSH) {
                    return HumanoidModel.ArmPose.BRUSH;
                }
            } else if (!mannequin.swinging && itemStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    public PlayerSkin getSkin(Mannequin mannequin) {
        return Minecraft.getInstance().getSkinManager().getInsecureSkin(mannequin.getProfile().gameProfile());
    }

    public @NotNull ResourceLocation getTextureLocation(Mannequin mannequin) {
        return getSkin(mannequin).texture();
    }

    protected void scale(Mannequin mannequin, PoseStack poseStack, float f) {
        float g = 0.9375F;
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void renderNameTag(Mannequin mannequin, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f) {
        //super.renderNameTag(mannequin, component, poseStack, multiBufferSource, i, f);
    }

    protected void setupRotations(Mannequin mannequin, PoseStack poseStack, float f, float g, float h, float i) {
        float j = mannequin.getSwimAmount(h);
        float k = mannequin.getViewXRot(h);
        if (mannequin.isFallFlying()) {
            super.setupRotations(mannequin, poseStack, f, g, h, i);
            float l = (float)mannequin.getFallFlyingTicks() + h;
            float m = Mth.clamp(l * l / 100.0F, 0.0F, 1.0F);
            if (!mannequin.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(m * (-90.0F - k)));
            }

            Vec3 vec3 = mannequin.getViewVector(h);
            Vec3 vec32 = mannequin.getDeltaMovement();
            double d = vec32.horizontalDistanceSqr();
            double e = vec3.horizontalDistanceSqr();
            if (d > (double)0.0F && e > (double)0.0F) {
                double n = (vec32.x * vec3.x + vec32.z * vec3.z) / Math.sqrt(d * e);
                double o = vec32.x * vec3.z - vec32.z * vec3.x;
                poseStack.mulPose(Axis.YP.rotation((float)(Math.signum(o) * Math.acos(n))));
            }
        } else if (j > 0.0F) {
            super.setupRotations(mannequin, poseStack, f, g, h, i);
            float l = mannequin.isInWater() ? -90.0F - k : -90.0F;
            float m = Mth.lerp(j, 0.0F, l);
            poseStack.mulPose(Axis.XP.rotationDegrees(m));
            if (mannequin.isVisuallySwimming()) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupRotations(mannequin, poseStack, f, g, h, i);
        }

    }
}
