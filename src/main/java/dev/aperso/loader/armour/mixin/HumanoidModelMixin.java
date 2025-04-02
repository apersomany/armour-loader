package dev.aperso.loader.armour.mixin;

import dev.aperso.loader.armour.Armour;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
    @Shadow @Final public ModelPart head;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftLeg;
    @Shadow @Final public ModelPart rightLeg;

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/LivingEntity;FFF)V", at = @At("HEAD"))
    private void onPrepareMobModel(LivingEntity livingEntity, float f, float g, float h, CallbackInfo ci) {
        Armour head = Armour.of(livingEntity.getItemBySlot(EquipmentSlot.HEAD));
        if (head != null) {
            if (head.isTrue("overrideModelHead")) {
                this.head.visible = false;
            }
        }
        Armour chest = Armour.of(livingEntity.getItemBySlot(EquipmentSlot.CHEST));
        if (chest != null) {
            if (chest.isTrue("overrideModelChest")) {
                body.visible = false;
                if (this instanceof PlayerModelAccessor accessor) {
                    accessor.getJacket().visible = false;
                    accessor.getCloak().visible = false;
                }
            }
            if (chest.isTrue("overrideModelArmLeft")) {
                leftArm.visible = false;
                if (this instanceof PlayerModelAccessor accessor) {
                    accessor.getLeftSleeve().visible = false;
                }
            }
            if (chest.isTrue("overrideModelArmRight")) {
                rightArm.visible = false;
                if (this instanceof PlayerModelAccessor accessor) {
                    accessor.getRightSleeve().visible = false;
                }
            }
        }
        Armour legs = Armour.of(livingEntity.getItemBySlot(EquipmentSlot.LEGS));
        if (legs != null) {
            if (legs.isTrue("overrideModelLegLeft")) {
                leftLeg.visible = false;
                if (this instanceof PlayerModelAccessor accessor) {
                    accessor.getLeftPants().visible = false;
                }
            }
            if (legs.isTrue("overrideModelLegRight")) {
                rightLeg.visible = false;
                if (this instanceof PlayerModelAccessor accessor) {
                    accessor.getRightPants().visible = false;
                }
            }
        }
        Armour feet = Armour.of(livingEntity.getItemBySlot(EquipmentSlot.FEET));
        if (feet != null) {
            if (feet.isTrue("overrideModelLegLeft")) {
                leftLeg.visible = false;
                if (this instanceof PlayerModelAccessor accessor) {
                    accessor.getLeftPants().visible = false;
                }
            }
            if (feet.isTrue("overrideModelLegRight")) {
                rightLeg.visible = false;
                if (this instanceof PlayerModelAccessor accessor) {
                    accessor.getRightPants().visible = false;
                }
            }
        }
    }
}
