package ovh.astarivi.mobs.entity.generic;

import net.minecraft.resources.ResourceLocation;
import ovh.astarivi.mobs.AstarMobs;

import java.util.List;
import java.util.stream.IntStream;


public enum EntityResource {
    BEAR("bear", 2),
    DEER("deer");

    public final String id;
    public final int variants;
    public final ResourceLocation model;
    public final ResourceLocation animation;
    public final List<ResourceLocation> textureVariants;

    EntityResource(String id, int variants) {
        this.id = id;
        this.variants = variants;
        this.model = ResourceLocation.fromNamespaceAndPath(
                AstarMobs.MOD_ID,
                "geo/%s.geo.json".formatted(id)
        );
        this.animation = ResourceLocation.fromNamespaceAndPath(
                AstarMobs.MOD_ID,
                "animations/%s.animation.json".formatted(id)
        );

        this.textureVariants = IntStream.range(0, variants)
                .mapToObj(variant -> ResourceLocation.fromNamespaceAndPath(
                        AstarMobs.MOD_ID,
                        "textures/entity/%s/%d.png".formatted(id, variant)
                )).toList();
    }

    EntityResource(String id) {
        this(id, 1);
    }
}
