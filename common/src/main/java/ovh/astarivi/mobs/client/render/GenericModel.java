package ovh.astarivi.mobs.client.render;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ovh.astarivi.mobs.entity.generic.EntityResourceProvider;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class GenericModel<T extends EntityResourceProvider & GeoAnimatable> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        return animatable.getModel();
    }

    @Override
    public ResourceLocation getTextureResource(T animatable, @Nullable GeoRenderer<T> renderer) {
        return animatable.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animatable.getAnimation();
    }
}
