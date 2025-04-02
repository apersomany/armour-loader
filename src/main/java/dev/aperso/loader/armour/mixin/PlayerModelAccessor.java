package dev.aperso.loader.armour.mixin;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerModel.class)
public interface PlayerModelAccessor {
    @Accessor
    ModelPart getJacket();

    @Accessor
    ModelPart getCloak();

    @Accessor
    ModelPart getLeftSleeve();

    @Accessor
    ModelPart getRightSleeve();

    @Accessor
    ModelPart getLeftPants();

    @Accessor
    ModelPart getRightPants();
}
