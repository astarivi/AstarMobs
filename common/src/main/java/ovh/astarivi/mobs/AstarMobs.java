package ovh.astarivi.mobs;

import ovh.astarivi.mobs.registry.EntityRegistry;
import ovh.astarivi.mobs.client.render.RenderRegistry;
import ovh.astarivi.mobs.registry.ItemRegistry;
import ovh.astarivi.mobs.registry.TabRegistry;

public final class AstarMobs {
    public static final String MOD_ID = "astarmobs";

    public static void init() {
        EntityRegistry.init();
        ItemRegistry.init();
        TabRegistry.init();
    }

    public static void initClient() {
        RenderRegistry.init();
    }
}
