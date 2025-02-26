package ovh.astarivi.mobs.client.render;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import ovh.astarivi.mobs.registry.EntityRegistry;

public class RenderRegistry {
    public static void init() {
        EntityRendererRegistry.register(EntityRegistry.BEAR, GenericEntityRenderer::ofBabyGeneric);
    }
}
