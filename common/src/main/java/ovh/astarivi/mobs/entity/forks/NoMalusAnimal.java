package ovh.astarivi.mobs.entity.forks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


/// This class is a direct copy of the original Animal class, but with the pathfinding
/// malus attributes disabled, as some of our animals like fire.
public abstract class NoMalusAnimal extends AgeableMob {
    protected static final int PARENT_AGE_AFTER_BREEDING = 6000;
    private int inLove;
    @Nullable
    private UUID loveCause;

    protected NoMalusAnimal(EntityType<? extends NoMalusAnimal> entityType, Level level) {
        super(entityType, level);
        // Leave this commented
//        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
//        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAnimalAttributes() {
        return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, (double)10.0F);
    }

    protected void customServerAiStep(ServerLevel serverLevel) {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        super.customServerAiStep(serverLevel);
    }

    public void aiStep() {
        super.aiStep();
        if (this.getAge() != 0) {
            this.inLove = 0;
        }

        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX((double)1.0F), this.getRandomY() + (double)0.5F, this.getRandomZ((double)1.0F), d, e, f);
            }
        }

    }

    protected void actuallyHurt(ServerLevel serverLevel, DamageSource damageSource, float f) {
        this.resetLove();
        super.actuallyHurt(serverLevel, damageSource, f);
    }

    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return levelReader.getBlockState(blockPos.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : levelReader.getPathfindingCostFromLightLevels(blockPos);
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            compoundTag.putUUID("LoveCause", this.loveCause);
        }

    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.inLove = compoundTag.getInt("InLove");
        this.loveCause = compoundTag.hasUUID("LoveCause") ? compoundTag.getUUID("LoveCause") : null;
    }

    public static boolean checkAnimalSpawnRules(EntityType<? extends NoMalusAnimal> entityType, LevelAccessor levelAccessor, EntitySpawnReason entitySpawnReason, BlockPos blockPos, RandomSource randomSource) {
        boolean bl = EntitySpawnReason.ignoresLightRequirements(entitySpawnReason) || isBrightEnoughToSpawn(levelAccessor, blockPos);
        return levelAccessor.getBlockState(blockPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && bl;
    }

    protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
        return blockAndTintGetter.getRawBrightness(blockPos, 0) > 8;
    }

    public int getAmbientSoundInterval() {
        return 120;
    }

    public boolean removeWhenFarAway(double d) {
        return false;
    }

    protected int getBaseExperienceReward(ServerLevel serverLevel) {
        return 1 + this.random.nextInt(3);
    }

    public abstract boolean isFood(ItemStack itemStack);

    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (this.isFood(itemStack)) {
            int i = this.getAge();
            if (!this.level().isClientSide && i == 0 && this.canFallInLove()) {
                this.usePlayerItem(player, interactionHand, itemStack);
                this.setInLove(player);
                this.playEatingSound();
                return InteractionResult.SUCCESS_SERVER;
            }

            if (this.isBaby()) {
                this.usePlayerItem(player, interactionHand, itemStack);
                this.ageUp(getSpeedUpSecondsWhenFeeding(-i), true);
                this.playEatingSound();
                return InteractionResult.SUCCESS;
            }

            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }

        return super.mobInteract(player, interactionHand);
    }

    protected void playEatingSound() {
    }

    protected void usePlayerItem(Player player, InteractionHand interactionHand, ItemStack itemStack) {
        int i = itemStack.getCount();
        UseRemainder useRemainder = (UseRemainder)itemStack.get(DataComponents.USE_REMAINDER);
        itemStack.consume(1, player);
        if (useRemainder != null) {
            boolean var10003 = player.hasInfiniteMaterials();
            Objects.requireNonNull(player);
            ItemStack itemStack2 = useRemainder.convertIntoRemainder(itemStack, i, var10003, player::handleExtraItemsCreatedOnUse);
            player.setItemInHand(interactionHand, itemStack2);
        }

    }

    public boolean canFallInLove() {
        return this.inLove <= 0;
    }

    public void setInLove(@Nullable Player player) {
        this.inLove = 600;
        if (player != null) {
            this.loveCause = player.getUUID();
        }

        this.level().broadcastEntityEvent(this, (byte)18);
    }

    public void setInLoveTime(int i) {
        this.inLove = i;
    }

    public int getInLoveTime() {
        return this.inLove;
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        } else {
            Player player = this.level().getPlayerByUUID(this.loveCause);
            return player instanceof ServerPlayer ? (ServerPlayer)player : null;
        }
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public boolean canMate(NoMalusAnimal animal) {
        if (animal == this) {
            return false;
        } else if (animal.getClass() != this.getClass()) {
            return false;
        } else {
            return this.isInLove() && animal.isInLove();
        }
    }

    public void spawnChildFromBreeding(ServerLevel serverLevel, NoMalusAnimal animal) {
        AgeableMob ageableMob = this.getBreedOffspring(serverLevel, animal);
        if (ageableMob != null) {
            ageableMob.setBaby(true);
            ageableMob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            this.finalizeSpawnChildFromBreeding(serverLevel, animal, ageableMob);
            serverLevel.addFreshEntityWithPassengers(ageableMob);
        }
    }

    public void finalizeSpawnChildFromBreeding(ServerLevel serverLevel, NoMalusAnimal animal, @Nullable AgeableMob ageableMob) {
        Optional.ofNullable(this.getLoveCause()).or(() -> Optional.ofNullable(animal.getLoveCause())).ifPresent((serverPlayer) -> {
            serverPlayer.awardStat(Stats.ANIMALS_BRED);
//            CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, this, animal, ageableMob);
        });
        this.setAge(6000);
        animal.setAge(6000);
        this.resetLove();
        animal.resetLove();
        serverLevel.broadcastEntityEvent(this, (byte)18);
        if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

    }

    public void handleEntityEvent(byte b) {
        if (b == 18) {
            for(int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX((double)1.0F), this.getRandomY() + (double)0.5F, this.getRandomZ((double)1.0F), d, e, f);
            }
        } else {
            super.handleEntityEvent(b);
        }

    }
}

