package ovh.astarivi.mobs.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.astarivi.mobs.entity.generic.EntityResource;
import ovh.astarivi.mobs.entity.generic.GenericAnimal;
import ovh.astarivi.mobs.entity.generic.GenericAnimations;
import ovh.astarivi.mobs.entity.generic.GenericControllers;
import ovh.astarivi.mobs.entity.goal.BearFetchHoneyGoal;
import ovh.astarivi.mobs.entity.goal.InvestigateGoal;
import ovh.astarivi.mobs.registry.EntityRegistry;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;


// TODO: Allow bears to stand at the side of the beehive instead of on top when fetching honey
// TODO: Make bears non-hostile after eating honey for a set amount of time
public class BearEntity extends GenericAnimal {
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(10, 20);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(BearEntity.class, EntityDataSerializers.INT);
    private int angerTime;
    @Nullable private UUID angerTarget;
    private int warningSoundTicks;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BearEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityResource getEntityResource() {
        return EntityResource.BEAR;
    }

    @Override
    public ResourceLocation getTexture() {
        return getEntityResource().textureVariants.get(this.entityData.get(VARIANT));
    }

    // region Attributes
    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1D)
                .add(Attributes.FOLLOW_RANGE, 22.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readPersistentAngerSaveData(this.level(), compoundTag);
        this.entityData.set(VARIANT, compoundTag.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.addPersistentAngerSaveData(compoundTag);
        compoundTag.putInt("Variant", this.entityData.get(VARIANT));
    }
    // endregion

    // region AI
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BearMeleeAttackGoal());
        this.goalSelector.addGoal(2, new PanicGoal(this, 2.0F, (pathfinderMob) -> pathfinderMob.isBaby() ? DamageTypeTags.PANIC_CAUSES : DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25F));
        this.goalSelector.addGoal(5, new BearFetchHoneyGoal(this, 1D, 3));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new InvestigateGoal<>(this, 63 + 5));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new BearHurtByTargetGoal());
        this.targetSelector.addGoal(2, new BearAttackPlayersGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, DeerEntity.class, 10, true, true, null));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return EntityRegistry.BEAR.get().create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.80F;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return angerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        angerTime = i;
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return angerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uUID) {
        angerTarget = uUID;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgeableMob.AgeableMobGroupData(1.0F);
        }

        SpawnGroupData data = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);
        this.entityData.set(VARIANT, this.random.nextInt(getEntityResource().variants));
        return data;
    }

    // Alert adult bears if baby is attacked
    class BearHurtByTargetGoal extends HurtByTargetGoal {
        public BearHurtByTargetGoal() {
            super(BearEntity.this);
        }

        @Override
        public void start() {
            super.start();
            if (BearEntity.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }
        }

        @Override
        protected void alertOther(Mob mob, LivingEntity livingEntity) {
            if (mob instanceof BearEntity && !mob.isBaby()) {
                super.alertOther(mob, livingEntity);
            }
        }
    }

    class BearAttackPlayersGoal extends NearestAttackableTargetGoal<Player> {
        public BearAttackPlayersGoal() {
            super(BearEntity.this, Player.class, 20, false, true, null);
        }

        public boolean canUse() {
            if (BearEntity.this.isBaby()) {
                return false;
            }

            return super.canUse();
        }

        protected double getFollowDistance() {
            for(BearEntity bear : BearEntity.this.level().getEntitiesOfClass(BearEntity.class, BearEntity.this.getBoundingBox().inflate(8.0F, 4.0F, 8.0F))) {
                if (bear.isBaby()) {
                    return super.getFollowDistance() * (double)0.5F;
                }
            }

            return super.getFollowDistance() * (double)0.25F;
        }
    }

    class BearMeleeAttackGoal extends MeleeAttackGoal {
        public BearMeleeAttackGoal() {
            super(BearEntity.this, 1.5F, true);
        }

        protected void checkAndPerformAttack(LivingEntity livingEntity) {
            if (this.canPerformAttack(livingEntity)) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(getServerLevel(this.mob), livingEntity);
                BearEntity.this.stopTriggeredAnim("attack_controller", "attack");
            } else if (this.mob.distanceToSqr(livingEntity) < (double)((livingEntity.getBbWidth() + 3.0F) * (livingEntity.getBbWidth() + 3.0F))) {
                if (this.isTimeToAttack()) {
                    BearEntity.this.stopTriggeredAnim("attack_controller", "attack");
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    BearEntity.this.triggerAnim("attack_controller", "attack");
                    BearEntity.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                BearEntity.this.stopTriggeredAnim("attack_controller", "attack");
            }
        }

        public void stop() {
            BearEntity.this.stopTriggeredAnim("attack_controller", "attack");

            super.stop();
        }
    }
    // endregion

    // region Sounds
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.makeSound(SoundEvents.POLAR_BEAR_WARNING);
            this.warningSoundTicks = 40;
        }

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
        controllerRegistrar.add(new AnimationController<>(this, GenericControllers.WALK.getName(), 5, this::walkCycle
        ).triggerableAnim("investigate", GenericAnimations.INVESTIGATE.getRawAnimation()));

        controllerRegistrar.add(new AnimationController<>(this, GenericControllers.ATTACK.getName(), 3, event -> {
            swinging = false;
            return PlayState.STOP;
        }).triggerableAnim(GenericAnimations.ATTACK.getName(), GenericAnimations.ATTACK.getRawAnimation()
        ).triggerableAnim(GenericAnimations.EAT.getName(), GenericAnimations.EAT.getRawAnimation()));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    // endregion
}
