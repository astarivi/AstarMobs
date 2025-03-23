package ovh.astarivi.mobs.registry;

import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import ovh.astarivi.mobs.AstarMobs;


public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(AstarMobs.MOD_ID, Registries.ITEM);
    // Spawn Eggs
    public static final ResourceKey<Item> BEAR_SPAWN_EGG_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "bear_spawn_egg")
    );
    public static final RegistrySupplier<Item> BEAR_SPAWN_EGG = ITEMS.register(
            "bear_spawn_egg",
            () -> new ArchitecturySpawnEggItem(
                    EntityRegistry.BEAR,
                    new Item.Properties()
                            .setId(BEAR_SPAWN_EGG_ID)
                            .arch$tab(TabRegistry.ASTARMOBS_TAG))
    );

    // Foods
    public static final ResourceKey<Item> RAW_BEAR_MEAT_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "raw_bear_meat")
    );
    public static final ResourceKey<Item> COOKED_BEAR_MEAT_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "cooked_bear_meat")
    );
    public static final ResourceKey<Item> RAW_VENISON_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "raw_venison")
    );
    public static final ResourceKey<Item> COOKED_VENISON_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "cooked_venison")
    );
    public static final RegistrySupplier<Item> RAW_BEAR_MEAT = ITEMS.register(
            "raw_bear_meat",
            () -> new Item(
                    new Item.Properties()
                            .setId(RAW_BEAR_MEAT_ID)
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
                            .arch$tab(TabRegistry.ASTARMOBS_TAG)
            )
    );
    public static final RegistrySupplier<Item> COOKED_BEAR_MEAT = ITEMS.register(
            "cooked_bear_meat",
            () -> new Item(
                    new Item.Properties()
                            .setId(COOKED_BEAR_MEAT_ID)
                            .food(new FoodProperties.Builder()
                                            .nutrition(10)
                                            .saturationModifier(0.875F)
                                            .build()
                            )
                            .arch$tab(TabRegistry.ASTARMOBS_TAG)
            )
    );
    public static final RegistrySupplier<Item> RAW_VENISON = ITEMS.register(
            "raw_venison",
            () -> new Item(
                    new Item.Properties()
                            .setId(RAW_VENISON_ID)
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
                            .arch$tab(TabRegistry.ASTARMOBS_TAG)
            )
    );
    public static final RegistrySupplier<Item> COOKED_VENISON = ITEMS.register(
            "cooked_venison",
            () -> new Item(
                    new Item.Properties()
                            .setId(COOKED_VENISON_ID)
                            .food(new FoodProperties.Builder()
                                    .nutrition(8)
                                    .saturationModifier(0.812F)
                                    .build()
                            )
                            .arch$tab(TabRegistry.ASTARMOBS_TAG)
            )
    );

    public static void init() {
        ITEMS.register();
    }
}
