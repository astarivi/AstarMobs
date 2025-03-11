package ovh.astarivi.mobs.entity.generic;

import net.minecraft.resources.ResourceLocation;


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
}
