package ovh.astarivi.mobs.registry;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import ovh.astarivi.mobs.AstarMobs;
import ovh.astarivi.mobs.entity.BearEntity;
import ovh.astarivi.mobs.entity.CaribouEntity;
import ovh.astarivi.mobs.entity.DeerEntity;


public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(AstarMobs.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<BearEntity>> BEAR = ENTITIES.register("bear", () ->
            EntityType.Builder.of(BearEntity::new, MobCategory.CREATURE)
                    .sized(1.65F, 1.35F)
                    .eyeHeight(1.0F)
                    .build(
                            ResourceKey.create(
                                    Registries.ENTITY_TYPE,
                                    ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "bear")
                            )
                    )
    );

    public static final RegistrySupplier<EntityType<CaribouEntity>> CARIBOU = ENTITIES.register("caribou", () ->
            EntityType.Builder.of(CaribouEntity::new, MobCategory.CREATURE)
                    .sized(1.2F, 2.0F)
                    .eyeHeight(1.55F)
                    .build(
                            ResourceKey.create(
                                    Registries.ENTITY_TYPE,
                                    ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "caribou")
                            )
                    )
    );

    public static final RegistrySupplier<EntityType<DeerEntity>> DEER = ENTITIES.register("deer", () ->
            EntityType.Builder.of(DeerEntity::new, MobCategory.CREATURE)
                    .sized(1F, 1.5F)
                    .eyeHeight(1.35F)
                    .build(
                            ResourceKey.create(
                                    Registries.ENTITY_TYPE,
                                    ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "deer")
                            )
                    )
    );

    private static void initAttributes() {
        EntityAttributeRegistry.register(BEAR, BearEntity::createAttributes);
        EntityAttributeRegistry.register(DEER, DeerEntity::createAttributes);
        EntityAttributeRegistry.register(CARIBOU, CaribouEntity::createAttributes);
    }

    private static void initSpawns() {
        SpawnPlacementsRegistry.register(BEAR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.BEAR_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(BEAR.get(), 6, 1, 2)));

        SpawnPlacementsRegistry.register(DEER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.DEER_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(DEER.get(), 8, 1, 4)));

        SpawnPlacementsRegistry.register(CARIBOU, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CaribouEntity::checkSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.CARIBOU_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(CARIBOU.get(), 8, 1, 4)));
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.CARIBOU_NETHER_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(CARIBOU.get(), 1, 2, 3)));
    }

    public static void init() {
        ENTITIES.register();
        initAttributes();
        initSpawns();
    }
}
