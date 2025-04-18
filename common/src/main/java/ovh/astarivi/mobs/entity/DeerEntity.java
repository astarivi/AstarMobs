package ovh.astarivi.mobs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.astarivi.mobs.entity.generic.EntityResource;
import ovh.astarivi.mobs.entity.generic.GenericAnimal;
import ovh.astarivi.mobs.entity.generic.GenericAnimations;
import ovh.astarivi.mobs.entity.generic.GenericControllers;
import ovh.astarivi.mobs.registry.EntityRegistry;
import ovh.astarivi.mobs.registry.SoundRegistry;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;


public class DeerEntity extends GenericAnimal {
    private static final EntityDataAccessor<Boolean> MALE = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANTLER_TICKS = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.INT);
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.of(Items.SWEET_BERRIES);
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(5, 10);
    private int angerTime;
    @Nullable private UUID angerTarget;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DeerEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityResource getEntityResource() {
        return EntityResource.DEER;
    }

    @Override
    public boolean shouldDisplayLayer() {
        return isMale();
    }

    public ResourceLocation getDisplayLayer() {
        return getEntityResource().textureOverlays.get(getAntlerGrowStage());
    }

    // region Attributes
    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.TEMPT_RANGE, 10.0D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 10.0D);
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgeableMob.AgeableMobGroupData(1.0F);
        }

        SpawnGroupData data = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);

        if (this.random.nextBoolean()) {
            setMale(true);
            setAntlerTicks(this.random.nextInt(getAntlerGrowTicks()));
        }

        if (isBaby()) {
            setAntlerTicks(0);
        }

        return data;
    }

    public boolean isMale() {
        return this.entityData.get(MALE);
    }

    public void setMale(boolean white) {
        this.entityData.set(MALE, white);
    }

    public int getAntlerTicks() {
        return this.entityData.get(ANTLER_TICKS);
    }

    public void setAntlerTicks(int val) {
        this.entityData.set(ANTLER_TICKS, val);
    }

    public int getAntlerGrowStage() {
        int antlerGrowTicks = getAntlerGrowTicks();

        if (getAntlerTicks() >= antlerGrowTicks) {
            return 3;
        }

        int stages = antlerGrowTicks / 4;

        return (getAntlerTicks() % antlerGrowTicks) / stages;
    }

    public int getAntlerGrowTicks() {
        return 16_000;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MALE, false);
        builder.define(ANTLER_TICKS, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readPersistentAngerSaveData(this.level(), compoundTag);
        this.entityData.set(MALE, compoundTag.getBoolean("Male"));
        this.entityData.set(ANTLER_TICKS, compoundTag.getInt("Antler"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.addPersistentAngerSaveData(compoundTag);
        compoundTag.putBoolean("Male", isMale());
        compoundTag.putInt("Antler", getAntlerTicks());
    }
    // endregion

    // region AI
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(2, new PanicGoal(this, 2.0F));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0F));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.25F, BREEDING_INGREDIENT, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25F));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new DeerHurtByTargetGoal());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal(this, false));
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        AgeableMob offspring = EntityRegistry.DEER.get().create(serverLevel, EntitySpawnReason.BREEDING);

        if (isMale()) {
            setAntlerTicks(0);
        } else if (ageableMob instanceof DeerEntity matingPartner && matingPartner.isMale()) {
            matingPartner.setAntlerTicks(0);
        }

        // This should always be true. This is here for extra safety.
        if (offspring instanceof DeerEntity deerOffspring) {
            deerOffspring.setMale(this.random.nextInt(2) == 0);
            deerOffspring.setAntlerTicks(0);
        }

        return offspring;
    }

    @Override
    public boolean canMate(Animal animal) {
        if (animal == this || !this.isInLove() || !animal.isInLove()) {
            return false;
        }

        return animal instanceof DeerEntity partnerDeer
                && this.isMale() != partnerDeer.isMale()
                && (getAntlerGrowStage() == 3 || partnerDeer.getAntlerGrowStage() == 3);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        if (isMale() && getAntlerGrowStage() != 3) {
            return false;
        }

        return BREEDING_INGREDIENT.test(itemStack);
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

        if (!this.level().isClientSide) {
            if (!isBaby() && isMale()) {
                int antlerTicks = getAntlerTicks();

                if (antlerTicks < getAntlerGrowTicks()) {
                    setAntlerTicks(antlerTicks + 1);
                }
            }

            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    public class DeerHurtByTargetGoal extends HurtByTargetGoal {

        public DeerHurtByTargetGoal() {
            super(DeerEntity.this);
        }

        @Override
        public void start() {
            super.start();

            if (mob.isBaby()) {
                alertOthers();
            }

            if (mob.isBaby() || !DeerEntity.this.isMale()) {
                this.stop();
            }
        }

        @Override
        protected void alertOther(Mob mob, LivingEntity livingEntity) {
            if (mob instanceof DeerEntity deer && !deer.isBaby() && deer.isMale()) {
                super.alertOther(mob, livingEntity);
            }
        }
    }
    // endregion

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        this.triggerAnim("attack_controller", "attack");
        return super.doHurtTarget(serverLevel, entity);
    }

    // region Animations
    private <E extends GeoAnimatable> PlayState movementCycle(software.bernie.geckolib.animation.AnimationState<E> event) {
        if (event.isMoving()) {
            event.setControllerSpeed(
                    this.walkAnimation.speed(event.getPartialTick()) * 2.0F
            );
            return event.setAndContinue(GenericAnimations.WALK.getRawAnimation());
        } else {
            event.setControllerSpeed(1.0F);
            return event.setAndContinue(GenericAnimations.IDLE.getRawAnimation());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, GenericControllers.WALK.getName(), 5, this::movementCycle));
        controllers.add(new AnimationController<>(this, GenericControllers.ATTACK.getName(), 3, event -> {
            swinging = false;
            return PlayState.STOP;
        }).triggerableAnim("attack", GenericAnimations.ATTACK.getRawAnimation()));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    // endregion

    // region Sounds
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        if (random.nextFloat() < 0.7f) {
            return null;
        }

        if (isBaby()) {
            return SoundRegistry.DEER_BABY_AMBIENT.get();
        } else if (isMale()) {
            return SoundRegistry.DEER_AMBIENT.get();
        } else {
            return SoundRegistry.DEER_FEMALE_AMBIENT.get();
        }
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        if (isBaby()) {
            return SoundRegistry.DEER_BABY_HURT.get();
        } else if (isMale()) {
            return SoundRegistry.DEER_HURT.get();
        } else {
            return SoundRegistry.DEER_FEMALE_HURT.get();
        }
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return getDeathSound();
    }
    // endregion

}
