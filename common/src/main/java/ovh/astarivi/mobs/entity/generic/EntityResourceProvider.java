package ovh.astarivi.mobs.entity.generic;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;


public interface EntityResourceProvider {
    EntityResource getEntityResource();

    default ResourceLocation getModel() {
        return getEntityResource().model;
    }

    default ResourceLocation getAnimation() {
        return getEntityResource().animation;
    }

    default ResourceLocation getTexture() {
        return getEntityResource().textureVariants.getFirst();
    }

    @Nullable
    default ResourceLocation getDisplayLayer() {
        return null;
    }

    default boolean shouldDisplayLayer() {
        return false;
    }
}
