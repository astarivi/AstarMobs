package ovh.astarivi.mobs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import ovh.astarivi.mobs.AstarMobs;

public class TagRegistry {
    public static TagKey<Biome> BEAR_BIOMES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "bear_spawns"));
}
