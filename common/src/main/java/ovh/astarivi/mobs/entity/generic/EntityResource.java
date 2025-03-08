package ovh.astarivi.mobs.entity.generic;

import net.minecraft.resources.ResourceLocation;
import ovh.astarivi.mobs.AstarMobs;


public enum EntityResource {
    BEAR("bear"),
    DEER("deer");

    public final String id;
    public final ResourceLocation model;
    public final ResourceLocation texture;
    public final ResourceLocation animation;

    EntityResource(String id) {
        this.id = id;
        this.model = ResourceLocation.fromNamespaceAndPath(
                AstarMobs.MOD_ID,
                "geo/%s.geo.json".formatted(id)
        );
        this.texture = ResourceLocation.fromNamespaceAndPath(
                AstarMobs.MOD_ID,
                "textures/entity/%s.png".formatted(id)
        );
        this.animation = ResourceLocation.fromNamespaceAndPath(
                AstarMobs.MOD_ID,
                "animations/%s.animation.json".formatted(id)
        );
    }
}
