package ovh.astarivi.mobs.registry;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import ovh.astarivi.mobs.AstarMobs;
import ovh.astarivi.mobs.entity.BearEntity;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(AstarMobs.MOD_ID, Registries.ENTITY_TYPE);

    public static final ResourceKey<EntityType<?>> BEAR_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "bear")
    );

    public static final RegistrySupplier<EntityType<BearEntity>> BEAR = ENTITIES.register("bear", () ->
            EntityType.Builder.of(BearEntity::new, MobCategory.CREATURE)
                    .sized(1.65F, 1.3F)
                    .eyeHeight(0.9F)
                    .build(BEAR_KEY)
    );

    private static void initAttributes() {
        EntityAttributeRegistry.register(BEAR, BearEntity::createAttributes);
    }

    private static void initSpawns() {
        SpawnPlacementsRegistry.register(BEAR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.BEAR_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(BEAR.get(), 10, 1, 3)));
    }

    public static void init() {
        ENTITIES.register();
        initAttributes();
        initSpawns();
    }
}
