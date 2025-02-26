package ovh.astarivi.mobs.client.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GenericRenderer<T extends LivingEntity & GeoAnimatable> extends GeoEntityRenderer<T> {
    public GenericRenderer(EntityRendererProvider.Context ctx, GeoModel<T> modelProvider) {
        super(ctx, modelProvider);
    }

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return model.getTextureResource(animatable, this);
    }

    @Override
    public @Nullable RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
