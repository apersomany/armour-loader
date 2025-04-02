package dev.aperso.loader.armour.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.aperso.loader.armour.ArmourRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(
        LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean onLeftArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo ci
    ) {
        poseStack.pushPose();
        if (onLeftArm) poseStack.scale(-1, 1, -1);
        poseStack.scale(1f / 16f, 1f / 16f, -1f / 16f);
        poseStack.translate(-1, -3, -3);
        if (ArmourRenderer.onRender(itemStack, poseStack, multiBufferSource, "armourers:sword.base", light)) ci.cancel();
        poseStack.popPose();
    }
}
