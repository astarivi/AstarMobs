package ovh.astarivi.mobs.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import ovh.astarivi.mobs.AstarMobs;

public final class AstarMobsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AstarMobs.initClient();
    }
}
