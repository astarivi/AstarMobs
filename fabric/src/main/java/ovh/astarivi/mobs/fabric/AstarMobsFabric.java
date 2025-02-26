package ovh.astarivi.mobs.fabric;

import net.fabricmc.api.ModInitializer;

import ovh.astarivi.mobs.AstarMobs;

public final class AstarMobsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        AstarMobs.init();
    }
}
