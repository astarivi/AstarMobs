package ovh.astarivi.mobs.entity.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import ovh.astarivi.mobs.entity.generic.GenericAnimations;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.EnumSet;


public class InvestigateGoal<T extends Mob & GeoEntity> extends Goal {
    private final T mob;
    private final double animationLength;
    private final float chance;
    private double remainingTime;

    public InvestigateGoal(T instance, double animationTickLength, float chanceMultiplier) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.mob = instance;
        this.animationLength = animationTickLength;
        chance = 0.000167F * chanceMultiplier;
    }

    public InvestigateGoal(T instance, double animationTickLength) {
        this(instance, animationTickLength, 1.0F);
    }

    @Override
    public boolean canUse() {
        // Should play every 5 mins on average
        return !mob.isInWater() && mob.getRandom().nextFloat() < chance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.remainingTime >= 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
        mob.stopInPlace();
        remainingTime = animationLength;
        mob.triggerAnim(GenericAnimations.WALK.getName(), GenericAnimations.INVESTIGATE.getName());
    }

    public void tick() {
        --this.remainingTime;
    }
}
