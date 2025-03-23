package ovh.astarivi.mobs.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import ovh.astarivi.mobs.AstarMobs;


public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(AstarMobs.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> ASTARMOBS_TAG = TABS.register(
            AstarMobs.MOD_ID + "_tab",
            () -> CreativeTabRegistry.create(
                    Component.translatable("category.astarmobs"),
                    () -> new ItemStack(ItemRegistry.CARIBOU_SPAWN_EGG.get())
            )
    );

    public static void init() {
        TABS.register();
    }
}
