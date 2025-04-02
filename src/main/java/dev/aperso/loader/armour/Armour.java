package dev.aperso.loader.armour;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.HashMap;
import java.util.Map;

public record Armour(
    String type,
    Map<String, Object> props,
    // 0xSSXXYYZZFFRRGGBB: S is for side and F is for Flag
    Map<String, Quad[][]> parts
) {
    public record Quad(
        byte t,
        byte x,
        byte y,
        byte z,
        int color
    ) {}

    public static final Map<String, Armour> REGISTRY = new HashMap<>();

    public static Armour of(ItemStack itemStack) {
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        return Armour.REGISTRY.get(data.copyTag().getString("armour"));
    }

    public boolean isTrue(String prop) {
        Object object = props.get(prop);
        return object != null && (boolean) object;
    }
}
