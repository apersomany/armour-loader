package dev.aperso.loader.armour.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin {
	@Inject(method = "isValidPath", at = @At("HEAD"), cancellable = true)
	private static void isValidPath(String path, CallbackInfoReturnable<Boolean> cir) {
		if (path.startsWith("models/armour")) {
			cir.setReturnValue(true);
		}
	}
}
