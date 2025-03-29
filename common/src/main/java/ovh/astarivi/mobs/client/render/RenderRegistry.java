package ovh.astarivi.mobs.client.render;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import ovh.astarivi.mobs.registry.EntityRegistry;


public class RenderRegistry {
    public static void init() {
        EntityRendererRegistry.register(
                EntityRegistry.BEAR,
                ctx -> GenericEntityRenderer.Builder
                        .ofAnimatedHead(ctx)
                        .setBabyCapable(true)
                        .setLayerSupport(true)
                        .withScaleFactor(1.1F)
                        .build()
        );
        EntityRendererRegistry.register(
                EntityRegistry.CARIBOU,
                ctx -> GenericEntityRenderer.Builder
                        .ofAnimatedHead(ctx)
                        .setBabyCapable(true)
                        .setLayerSupport(true)
                        .setGlowingLayerSupport(true)
                        .withScaleFactor(1.0F)
                        .build()
        );
        EntityRendererRegistry.register(
                EntityRegistry.DEER,
                ctx -> GenericEntityRenderer.Builder
                        .ofAnimatedHead(ctx)
                        .setBabyCapable(true)
                        .setLayerSupport(true)
                        .withScaleFactor(1.0F)
                        .build()
        );
    }
}
