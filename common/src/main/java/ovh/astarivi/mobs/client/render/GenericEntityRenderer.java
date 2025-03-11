package ovh.astarivi.mobs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.astarivi.mobs.entity.generic.EntityResourceProvider;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;


public class GenericEntityRenderer<T extends Mob & GeoAnimatable & EntityResourceProvider> extends GenericRenderer<T> {
    protected final boolean babyCapable;
    protected float scaleFactor = 1.0F;
    protected float babyScaleFactor = 0.5F;

    public GenericEntityRenderer(EntityRendererProvider.Context ctx, GeoModel<T> modelProvider, boolean hasBaby) {
        super(ctx, modelProvider);
        this.babyCapable = hasBaby;
    }

    private GenericEntityRenderer(Builder<T> builder) {
        super(builder.ctx, builder.modelProvider);
        this.babyCapable = builder.babyCapable;
        this.scaleFactor = builder.scaleFactor;
        this.babyScaleFactor = builder.babyScaleFactor;

        if (builder.layerSupport) {
            addRenderLayer(new GenericGeoLayer<>(this));
        }
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);
        if (isReRender) {
            return;
        }

        float newScaleFactor = animatable.isBaby() && babyCapable ? scaleFactor * babyScaleFactor : scaleFactor;
        poseStack.scale(newScaleFactor, newScaleFactor, newScaleFactor);
    }

    public static class Builder<T extends Mob & GeoAnimatable & EntityResourceProvider> {
        private final EntityRendererProvider.Context ctx;
        private final GeoModel<T> modelProvider;
        private boolean babyCapable = false;
        private boolean layerSupport = false;
        private float scaleFactor = 1.0f;
        private float babyScaleFactor = 0.5f;

        public Builder(EntityRendererProvider.Context ctx, GeoModel<T> modelProvider) {
            this.ctx = ctx;
            this.modelProvider = modelProvider;
        }

        @Contract("_ -> new")
        public static <T extends Mob & GeoAnimatable & EntityResourceProvider> @NotNull Builder<T> ofGeneric(EntityRendererProvider.Context ctx) {
            return new Builder<>(ctx, new GenericModel<>());
        }

        @Contract("_ -> new")
        public static <T extends Mob & GeoAnimatable & EntityResourceProvider> @NotNull Builder<T> ofAnimatedHead(EntityRendererProvider.Context ctx) {
            return new Builder<>(ctx, new GenericHeadRotatingModel<>());
        }

        public Builder<T> setBabyCapable(boolean babyCapable) {
            this.babyCapable = babyCapable;
            return this;
        }

        public Builder<T> withScaleFactor(float scaleFactor) {
            this.scaleFactor = scaleFactor;
            return this;
        }

        public Builder<T> withBabyScaleFactor(float babyScaleFactor) {
            this.babyScaleFactor = babyScaleFactor;
            return this;
        }

        public Builder<T> setLayerSupport(boolean value) {
            this.layerSupport = value;
            return this;
        }

        public GenericEntityRenderer<T> build() {
            return new GenericEntityRenderer<>(this);
        }
    }
}
