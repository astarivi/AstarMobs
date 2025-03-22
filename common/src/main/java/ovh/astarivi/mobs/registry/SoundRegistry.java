package ovh.astarivi.mobs.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import ovh.astarivi.mobs.AstarMobs;


public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(AstarMobs.MOD_ID, Registries.SOUND_EVENT);
    public static final RegistrySupplier<SoundEvent> CARIBOU_AMBIENT = SOUND_EVENTS.register("caribou_ambient", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "caribou_ambient")));
    public static final RegistrySupplier<SoundEvent> CARIBOU_HURT = SOUND_EVENTS.register("caribou_hurt", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, "caribou_hurt")));


    public static void init() {
        SOUND_EVENTS.register();
    }
}
