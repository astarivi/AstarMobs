package ovh.astarivi.mobs.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import ovh.astarivi.mobs.entity.generic.EntityResourceProvider;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class GenericEntityRenderer<T extends Mob & GeoAnimatable & EntityResourceProvider> extends GenericRenderer<T> {
    private final boolean hasBaby;

    public GenericEntityRenderer(EntityRendererProvider.Context ctx, GeoModel<T> modelProvider, boolean hasBaby) {
        super(ctx, modelProvider);
        this.hasBaby = hasBaby;
    }

    public static <T extends Mob & GeoAnimatable & EntityResourceProvider> GenericEntityRenderer<T> of(EntityRendererProvider.Context ctx, GeoModel<T> modelProvider) {
        return new GenericEntityRenderer<>(ctx, modelProvider, false);
    }

    public static <T extends Mob & GeoAnimatable & EntityResourceProvider> GenericEntityRenderer<T> ofGeneric(EntityRendererProvider.Context ctx) {
        return of(ctx, new GenericModel<>());
    }

    public static <T extends Mob & GeoAnimatable & EntityResourceProvider> GenericEntityRenderer<T> ofBaby(EntityRendererProvider.Context ctx, GeoModel<T> modelProvider) {
        return new GenericEntityRenderer<>(ctx, modelProvider, true);
    }

    public static <T extends Mob & GeoAnimatable & EntityResourceProvider> GenericEntityRenderer<T> ofBabyGeneric(EntityRendererProvider.Context ctx) {
        return ofBaby(ctx, new GenericModel<>());
    }
}
