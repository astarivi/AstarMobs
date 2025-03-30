package ovh.astarivi.mobs.registry;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import ovh.astarivi.mobs.AstarMobs;


public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(AstarMobs.MOD_ID, Registries.ITEM);

    // region Spawn Eggs
    public static final RegistrySupplier<Item> BEAR_SPAWN_EGG = registerSpawnEgg(
            "bear_spawn_egg",
            EntityRegistry.BEAR
    );
    public static final RegistrySupplier<Item> CARIBOU_SPAWN_EGG = registerSpawnEgg(
            "caribou_spawn_egg",
            EntityRegistry.CARIBOU
    );
    public static final RegistrySupplier<Item> DEER_SPAWN_EGG = registerSpawnEgg(
            "deer_spawn_egg",
            EntityRegistry.DEER
    );
    // endregion

    // region Foods
    public static final RegistrySupplier<Item> RAW_BEAR_MEAT = registerItem(
            "raw_bear_meat",
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                                    .nutrition(4)
                                    .saturationModifier(0.35F)
                                    .build(),
                            Consumable.builder()
                                    .onConsume(new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(MobEffects.HUNGER, 60 * 20, 1), 0.50F)
                                    )
                                    .build()
                    )
    );
    public static final RegistrySupplier<Item> COOKED_BEAR_MEAT = registerItem(
            "cooked_bear_meat",
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(10)
                            .saturationModifier(0.875F)
                            .build()
                    )
    );
    public static final RegistrySupplier<Item> RAW_VENISON = registerItem(
            "raw_venison",
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                                    .nutrition(4)
                                    .saturationModifier(0.35F)
                                    .build(),
                            Consumable.builder()
                                    .onConsume(new ApplyStatusEffectsConsumeEffect(
                                            new MobEffectInstance(MobEffects.HUNGER, 60 * 20, 1), 0.50F)
                                    )
                                    .build()
                    )
    );
    public static final RegistrySupplier<Item> COOKED_VENISON = registerItem(
            "cooked_venison",
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(9)
                            .saturationModifier(0.85F)
                            .build()
                    )
    );
    public static final RegistrySupplier<Item> CHARRED_VENISON = registerItem(
            "charred_venison",
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationModifier(0.25F)
                            .build())
    );
    // endregion

    private static RegistrySupplier<Item> registerSpawnEgg(String id, RegistrySupplier<? extends EntityType<? extends Mob>> entityType) {
        return ITEMS.register(
                id,
                () -> new ArchitecturySpawnEggItem(
                        entityType,
                        new Item.Properties()
                                .setId(
                                        ResourceKey.create(
                                                Registries.ITEM,
                                                ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, id)
                                        )
                                )
                                .arch$tab(TabRegistry.ASTARMOBS_TAG))
        );
    }

    private static RegistrySupplier<Item> registerItem(String id, Item.Properties properties) {
        return ITEMS.register(
                id,
                () -> new Item(
                        properties
                                .setId(
                                    ResourceKey.create(
                                            Registries.ITEM,
                                            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, id)
                                    )
                                )
                                .arch$tab(TabRegistry.ASTARMOBS_TAG)
                )
        );
    }

    public static void init() {
        ITEMS.register();
        CHARRED_VENISON.listen((item) -> FuelRegistry.register(200, item));
    }
}
