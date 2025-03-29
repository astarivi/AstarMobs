package ovh.astarivi.mobs.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import ovh.astarivi.mobs.AstarMobs;


public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(AstarMobs.MOD_ID, Registries.SOUND_EVENT);
    public static final RegistrySupplier<SoundEvent> CARIBOU_AMBIENT = registerSound("caribou_ambient");
    public static final RegistrySupplier<SoundEvent> CARIBOU_HURT = registerSound("caribou_hurt");
    public static final RegistrySupplier<SoundEvent> DEER_BABY_AMBIENT = registerSound("deer_baby_ambient");
    public static final RegistrySupplier<SoundEvent> DEER_BABY_HURT = registerSound("deer_baby_hurt");
    public static final RegistrySupplier<SoundEvent> DEER_FEMALE_AMBIENT = registerSound("deer_female_ambient");
    public static final RegistrySupplier<SoundEvent> DEER_FEMALE_HURT = registerSound("deer_female_hurt");
    public static final RegistrySupplier<SoundEvent> DEER_AMBIENT = registerSound("deer_ambient");
    public static final RegistrySupplier<SoundEvent> DEER_HURT = registerSound("deer_hurt");

    private static RegistrySupplier<SoundEvent> registerSound(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AstarMobs.MOD_ID, id)));
    }

    public static void init() {
        SOUND_EVENTS.register();
    }
}
