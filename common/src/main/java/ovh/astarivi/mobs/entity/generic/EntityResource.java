package ovh.astarivi.mobs.entity.generic;

import net.minecraft.resources.ResourceLocation;
import ovh.astarivi.mobs.AstarMobs;
import java.util.List;
import java.util.stream.IntStream;


public enum EntityResource {
    BEAR("bear", 2, 1),
    CARIBOU("caribou", 4, 4),
    DEER("deer", 1, 4);

    public final String id;
    public final ResourceLocation model;
    public final ResourceLocation animation;
    public final List<ResourceLocation> textureVariants;
    public final List<ResourceLocation> textureOverlays;

    EntityResource(String id, int variants, int overlays) {
        this.id = id;
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

        if (overlays > 0) {
            this.textureOverlays = IntStream.range(0, overlays)
                    .mapToObj(variant -> ResourceLocation.fromNamespaceAndPath(
                            AstarMobs.MOD_ID,
                            "textures/entity/%s/overlay/%d.png".formatted(id, variant)
                    )).toList();
        } else {
            this.textureOverlays = List.of();
        }
    }

    EntityResource(String id, int variants) {
        this(id, 1, 0);
    }

    EntityResource(String id) {
        this(id, 1);
    }

    public int getVariantCount() {
        return this.textureVariants.size();
    }

    public int getLayerCount() {
        return this.textureOverlays.size();
    }
}
