package ovh.astarivi.mobs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import ovh.astarivi.mobs.entity.generic.EntityResourceProvider;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;


public class GenericGeoLayer<T extends Mob & GeoAnimatable & EntityResourceProvider> extends GeoRenderLayer<T> {
    public GenericGeoLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        if (!animatable.shouldDisplayLayer()) {
            return;
        }

        ResourceLocation texture = animatable.getDisplayLayer();

        if (texture == null) {
            return;
        }

        RenderType layer = RenderType.entityCutoutNoCull(texture);

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, layer, bufferSource.getBuffer(layer), partialTick, packedLight, packedOverlay, getRenderer().getRenderColor(animatable, partialTick, packedLight).argbInt());
    }
}
