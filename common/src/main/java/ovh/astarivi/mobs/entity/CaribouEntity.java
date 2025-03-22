package ovh.astarivi.mobs.entity;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.astarivi.mobs.entity.generic.EntityResource;
import ovh.astarivi.mobs.entity.generic.GenericAnimal;
import ovh.astarivi.mobs.entity.generic.GenericAnimations;
import ovh.astarivi.mobs.entity.generic.GenericControllers;
import ovh.astarivi.mobs.entity.goal.CaribouHeatSeekGoal;
import ovh.astarivi.mobs.registry.EntityRegistry;
import ovh.astarivi.mobs.registry.SoundRegistry;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class CaribouEntity extends GenericAnimal {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(CaribouEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANTLER_TICKS = SynchedEntityData.defineId(CaribouEntity.class, EntityDataSerializers.INT);
    private static final Ingredient BREEDING_INGREDIENT_OVERWORLD = Ingredient.of(Items.APPLE);
    private static final Ingredient BREEDING_INGREDIENT_WARPED = Ingredient.of(Items.WARPED_FUNGUS);
    private static final Ingredient BREEDING_INGREDIENT_CRIMSON = Ingredient.of(Items.CRIMSON_FUNGUS);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CaribouEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityResource getEntityResource() {
        return EntityResource.CARIBOU;
    }

    @Override
    public ResourceLocation getTexture() {
        return getEntityResource().textureVariants.get(this.entityData.get(VARIANT));
    }

    @Override
    public boolean shouldDisplayLayer() {
        return true;
    }

    @Override
    public boolean shouldGlow() {
        return isNetherVariant();
    }

    public ResourceLocation getDisplayLayer() {
        return getEntityResource().textureOverlays.get(getAntlerGrowStage());
    }

    // region Attributes
    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.TEMPT_RANGE, 10.0D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 4D)
                .add(Attributes.FOLLOW_RANGE, 15.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(ANTLER_TICKS, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(VARIANT, compoundTag.getInt("Variant"));
        this.entityData.set(ANTLER_TICKS, compoundTag.getInt("Antler"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", this.entityData.get(VARIANT));
        compoundTag.putInt("Antler", getAntlerTicks());
    }

    public int getAntlerGrowStage() {
        int antlerGrowTicks = getAntlerGrowTicks();
        int stages = antlerGrowTicks / 4;

        return (getAntlerTicks() % antlerGrowTicks) / stages;
    }

    public int getAntlerGrowTicks() {
        return isSterile() ? 20_000 : 32_000;
    }

    public boolean isSterile() {
        return getVariant() == 2;
    }

    public boolean isNetherVariant() {
        int variant = getVariant();
        return variant == 0 || variant == 1 || variant == 2;
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int val) {
        this.entityData.set(VARIANT, val);
    }

    public int getAntlerTicks() {
        return this.entityData.get(ANTLER_TICKS);
    }

    public void setAntlerTicks(int val) {
        this.entityData.set(ANTLER_TICKS, val);
    }
    // endregion

    // region AI
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5F, false));
        this.goalSelector.addGoal(2, new PanicGoal(this, 2.0F));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0F));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25F, this::isFood, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25F));
        this.goalSelector.addGoal(6, new CaribouHeatSeekGoal(this, 1F, 3));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        int variant = getVariant();

        if (variant == 0) {
            return BREEDING_INGREDIENT_CRIMSON.test(itemStack);
        } else if (variant == 1) {
            return BREEDING_INGREDIENT_WARPED.test(itemStack);
        } else {
            return BREEDING_INGREDIENT_OVERWORLD.test(itemStack);
        }
    }

    @Override
    public boolean canMate(Animal animal) {
        return animal instanceof CaribouEntity partnerCaribou
                && this != animal
                && this.isInLove()
                && partnerCaribou.isInLove()
                && !this.isSterile()
                && !partnerCaribou.isSterile()
                && this.isNetherVariant() == partnerCaribou.isNetherVariant();
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        if (!(ageableMob instanceof CaribouEntity caribouParent)) return null;

        CaribouEntity offspring = EntityRegistry.CARIBOU.get().create(serverLevel, EntitySpawnReason.BREEDING);
        if (offspring == null) return null;

        int ourVariant = getVariant();
        int theirVariant = caribouParent.getVariant();

        if (ourVariant == theirVariant) {
            offspring.setVariant(ourVariant);
        } else if ((ourVariant == 0 && theirVariant == 1) || (ourVariant == 1 && theirVariant == 0)) {
            offspring.setVariant(2);
            var maxHealth = offspring.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(20.0D);
            }

            offspring.setHealth(20.0F);
        } else {
            offspring.setVariant(random.nextBoolean() ? ourVariant : theirVariant);
        }

        return offspring;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {

    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uUID) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            int antlerTicks = getAntlerTicks();

            if (!isBaby()) {
                final int antlerGrowTicks = getAntlerGrowTicks();

                if (antlerTicks < antlerGrowTicks) {
                    setAntlerTicks(antlerTicks + 1);
                }

                if (antlerTicks >= antlerGrowTicks && random.nextInt(6000) == 0) {
                    dropAntlers(serverLevel, SoundSource.NEUTRAL);
                }
            }

            if (isNetherVariant() && (this.level().dimension() != Level.NETHER || isInWaterOrRain()) && random.nextInt(100) == 0) {
                BlockPos belowPos = this.blockPosition().below();
                Block blockBelow = this.level().getBlockState(belowPos).getBlock();

                if (!isInLava() && (blockBelow != Blocks.MAGMA_BLOCK)) {
                    hurtServer(serverLevel, this.damageSources().dryOut(), 1.0F);
                    playSound(SoundEvents.FIRE_EXTINGUISH, 0.3f, 1f);
                    this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(@NotNull Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.is(Items.SHEARS)) {
            if (this.level() instanceof ServerLevel serverLevel) {
                if (getAntlerGrowStage() == 3) {
                    this.dropAntlers(serverLevel, SoundSource.PLAYERS);
                    this.gameEvent(GameEvent.SHEAR, player);
                    itemStack.hurtAndBreak(1, player, getSlotForHand(interactionHand));
                    return InteractionResult.SUCCESS_SERVER;
                }
            }

            return InteractionResult.CONSUME;
        } else {
            return super.mobInteract(player, interactionHand);
        }
    }

    public void dropAntlers(@NotNull ServerLevel serverLevel, SoundSource soundSource) {
        serverLevel.playSound(null, this, SoundEvents.BONE_BLOCK_PLACE, soundSource, 1.0F, 1.0F);

        ItemStack dropStack = new ItemStack(
                isNetherVariant() ? Items.QUARTZ : Items.BONE,
                this.random.nextInt(2) + 1
        );

        for (int i = 0; i < dropStack.getCount(); i++) {
            ItemEntity itemEntity = this.spawnAtLocation(serverLevel, dropStack.copyWithCount(1), 1.0F);
            if (itemEntity != null) {
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.1F,
                        this.random.nextFloat() * 0.05F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.1F
                ));
            }
        }

        setAntlerTicks(0);
    }

    @Override
    protected @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0F, 1.35F, (this.getBbWidth() * 0.4F));
    }

    @Override
    public boolean canBeLeashed() {
        return !isSterile();
    }

    @Override
    public boolean fireImmune() {
        return isNetherVariant();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgeableMob.AgeableMobGroupData(1.0F);
        }

        SpawnGroupData data = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);

        if (serverLevelAccessor.getLevel().dimension() == Level.NETHER) {
            ResourceKey<Biome> biomeKey = serverLevelAccessor.getBiome(blockPosition()).unwrapKey().orElse(null);
            if (biomeKey == Biomes.WARPED_FOREST){
                this.entityData.set(VARIANT, 1);
            } else {
                this.entityData.set(VARIANT, 0);
            }
        } else {
            int randomValue = this.random.nextInt(getEntityResource().getVariantCount() - 3);

            this.entityData.set(VARIANT, randomValue + 3);
        }

        this.entityData.set(ANTLER_TICKS, this.random.nextInt(getAntlerGrowTicks()));

        return data;
    }
    // endregion

    // region Sounds
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundRegistry.CARIBOU_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegistry.CARIBOU_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundRegistry.CARIBOU_HURT.get();
    }
    // endregion

    // region Animations
    private <E extends GeoAnimatable> PlayState walkCycle(software.bernie.geckolib.animation.AnimationState<E> event) {
        if (event.isMoving()) {
            return event.setAndContinue(GenericAnimations.WALK.getRawAnimation());
        } else {
            return event.setAndContinue(GenericAnimations.IDLE.getRawAnimation());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(
                this,
                GenericControllers.WALK.getName(),
                5,
                this::walkCycle
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    // endregion
}
