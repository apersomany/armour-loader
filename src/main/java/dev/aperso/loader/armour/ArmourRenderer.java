package dev.aperso.loader.armour;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;

public class ArmourRenderer {
    @Unique
    private static final RenderType ARMOUR_RENDER_TYPE = RenderType.create(
        "armour",
        DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
        VertexFormat.Mode.QUADS,
        1536,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_TEXT_BACKGROUND_SHADER) // this looks so wrong, but hey, it works
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setTextureState(RenderStateShard.NO_TEXTURE)
            .setCullState(RenderStateShard.NO_CULL)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .createCompositeState(false)
    );

    private static final Vector3f[][] CUBE_FACES = new Vector3f[6][4];

    static {
        CUBE_FACES[0] = new Vector3f[] {
            new Vector3f(0, 1, 0),
            new Vector3f(1, 1, 0),
            new Vector3f(1, 1, 1),
            new Vector3f(0, 1, 1)
        };
        CUBE_FACES[1] = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(1, 0, 0),
            new Vector3f(1, 0, 1),
            new Vector3f(0, 0, 1)
        };
        CUBE_FACES[2] = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(1, 0, 0),
            new Vector3f(1, 1, 0),
            new Vector3f(0, 1, 0)
        };
        CUBE_FACES[3] = new Vector3f[] {
            new Vector3f(0, 0, 1),
            new Vector3f(1, 0, 1),
            new Vector3f(1, 1, 1),
            new Vector3f(0, 1, 1)
        };
        CUBE_FACES[4] = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(0, 0, 1),
            new Vector3f(0, 1, 1),
            new Vector3f(0, 1, 0)
        };
        CUBE_FACES[5] = new Vector3f[] {
            new Vector3f(1, 0, 0),
            new Vector3f(1, 0, 1),
            new Vector3f(1, 1, 1),
            new Vector3f(1, 1, 0)
        };
    }

    public static boolean onRender(ItemStack itemStack, PoseStack poseStack, MultiBufferSource multiBufferSource, String part, int light) {
        CUBE_FACES[0] = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(1, 0, 0),
            new Vector3f(1, 0, 1),
            new Vector3f(0, 0, 1)
        };
        CUBE_FACES[1] = new Vector3f[] {
            new Vector3f(0, 1, 0),
            new Vector3f(1, 1, 0),
            new Vector3f(1, 1, 1),
            new Vector3f(0, 1, 1)
        };
        CUBE_FACES[2] = new Vector3f[] {
            new Vector3f(0, 0, 1),
            new Vector3f(1, 0, 1),
            new Vector3f(1, 1, 1),
            new Vector3f(0, 1, 1)
        };
        CUBE_FACES[3] = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(1, 0, 0),
            new Vector3f(1, 1, 0),
            new Vector3f(0, 1, 0)
        };
        CUBE_FACES[4] = new Vector3f[] {
            new Vector3f(0, 0, 0),
            new Vector3f(0, 0, 1),
            new Vector3f(0, 1, 1),
            new Vector3f(0, 1, 0)
        };
        CUBE_FACES[5] = new Vector3f[] {
            new Vector3f(1, 0, 0),
            new Vector3f(1, 0, 1),
            new Vector3f(1, 1, 1),
            new Vector3f(1, 1, 0)
        };
        Armour armour = Armour.of(itemStack);
        if (armour == null) return false;
        Armour.Quad[][] quads = armour.parts().get(part);
        if (quads == null) return false;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(ARMOUR_RENDER_TYPE);
        Matrix4f matrix = poseStack.last().pose();
        for (int i = 0; i < CUBE_FACES.length; i++) {
            for (Armour.Quad quad : quads[i]) {
                for (Vector3f vertex : CUBE_FACES[i]) {
                    Vector3f translated = new Vector3f(quad.x(), quad.y(),quad.z()).negate().add(vertex);
                    vertexConsumer
                        .addVertex(matrix.transformPosition(translated))
                        .setColor(quad.color() - 0x7F000000 * (quad.t() >> 1))
                        .setLight(light * (quad.t() & 1 ^ 1) | 0xFF * (quad.t() & 1));
                }
            }
        }
        return true;
    }
}