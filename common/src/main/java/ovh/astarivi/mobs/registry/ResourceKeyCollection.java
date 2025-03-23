package ovh.astarivi.mobs.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import ovh.astarivi.mobs.AstarMobs;

public class ResourceKeyCollection {
    // region Entities
    public static final ResourceKey<EntityType<?>> BEAR_ID = ResourceKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "bear")
    );
    public static final ResourceKey<EntityType<?>> CARIBOU_ID = ResourceKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "caribou")
    );
    public static final ResourceKey<EntityType<?>> DEER_ID = ResourceKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "deer")
    );

    // endregion

    // region Items
    public static final ResourceKey<Item> BEAR_SPAWN_EGG_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "bear_spawn_egg")
    );
    public static final ResourceKey<Item> CARIBOU_SPAWN_EGG_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "caribou_spawn_egg")
    );
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
    public static final ResourceKey<Item> CHARRED_VENISON_ID = ResourceKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "charred_venison")
    );
    // endregion
}
