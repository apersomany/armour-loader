package dev.aperso.loader.armour.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.aperso.loader.armour.ArmourRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Unique
    private void pushPose(ModelPart modelPart, PoseStack poseStack) {
        poseStack.pushPose();
        modelPart.translateAndRotate(poseStack);
        poseStack.scale(-1f / 16f, -1f / 16f, -1f / 16f);
        poseStack.translate(-1, -1, -1);
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;usesInnerModel(Lnet/minecraft/world/entity/EquipmentSlot;)Z"), cancellable = true)
    private void onRenderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int light, HumanoidModel<?> humanoidModel, CallbackInfo ci) throws Exception {
        ItemStack itemStack = livingEntity.getItemBySlot(equipmentSlot);
        if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == equipmentSlot) {
            if (EquipmentSlot.HEAD == equipmentSlot) {
                pushPose(humanoidModel.head, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:head.base", light)) ci.cancel();
                poseStack.popPose();
            }
            if (EquipmentSlot.CHEST == equipmentSlot) {
                pushPose(humanoidModel.body, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:chest.base", light)) ci.cancel();
                poseStack.popPose();
                pushPose(humanoidModel.leftArm, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:chest.leftArm", light)) ci.cancel();
                poseStack.popPose();
                pushPose(humanoidModel.rightArm, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:chest.rightArm", light)) ci.cancel();
                poseStack.popPose();
            }
            if (EquipmentSlot.LEGS == equipmentSlot) {
                pushPose(humanoidModel.leftLeg, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:legs.leftLeg", light)) ci.cancel();
                poseStack.popPose();
                pushPose(humanoidModel.rightLeg, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:legs.rightLeg", light)) ci.cancel();
                poseStack.popPose();
            }
            if (EquipmentSlot.FEET == equipmentSlot) {
                pushPose(humanoidModel.leftLeg, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:feet.leftFoot", light)) ci.cancel();
                poseStack.popPose();
                pushPose(humanoidModel.rightLeg, poseStack);
                if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:feet.rightFoot", light)) ci.cancel();
                poseStack.popPose();
            }
        }
    }
}
