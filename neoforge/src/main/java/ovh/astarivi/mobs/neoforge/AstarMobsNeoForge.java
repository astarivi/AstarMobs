package ovh.astarivi.mobs.neoforge;

import dev.architectury.utils.EnvExecutor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

import ovh.astarivi.mobs.AstarMobs;

@Mod(AstarMobs.MOD_ID)
public final class AstarMobsNeoForge {
    public AstarMobsNeoForge() {
        AstarMobs.init();
        EnvExecutor.runInEnv(Dist.CLIENT, () -> AstarMobs::initClient);
    }
}
