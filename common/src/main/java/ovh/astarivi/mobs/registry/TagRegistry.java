package ovh.astarivi.mobs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import ovh.astarivi.mobs.AstarMobs;


public class TagRegistry {
    public static TagKey<Biome> BEAR_BIOMES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "bear_spawns"));
    public static TagKey<Biome> DEER_BIOMES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "deer_spawns"));
    public static TagKey<Biome> CARIBOU_BIOMES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "caribou_spawns"));
    public static TagKey<Biome> CARIBOU_NETHER_BIOMES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "caribou_nether_spawns"));
}
