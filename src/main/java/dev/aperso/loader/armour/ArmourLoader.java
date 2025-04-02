package dev.aperso.loader.armour;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ArmourLoader implements ModInitializer {
public static final String MOD_ID = "armour-loader";
public static final Logger LOGGER = LoggerFactory.getLogger("ArmourLoader");

@Override
	public void onInitialize() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
				public ResourceLocation getFabricId() {
				return ResourceLocation.fromNamespaceAndPath(MOD_ID, "models/armour");
			}

			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				Map<ResourceLocation, Resource> found = resourceManager.listResources("models/armour", location -> location.getPath().endsWith(".armour"));
				for (Map.Entry<ResourceLocation, Resource> entry : found.entrySet()) {
                    try (InputStream stream = entry.getValue().open()) {
						Armour armour = ArmourReader.read(new DataInputStream(new BufferedInputStream(stream)));
						Armour.REGISTRY.put(entry.getKey().getPath(), armour);
                    } catch (Exception exception) {
                        LOGGER.error("Failed to load armour: {}", entry.getKey(), exception);
                    }
                }
			}
		});
	}
}