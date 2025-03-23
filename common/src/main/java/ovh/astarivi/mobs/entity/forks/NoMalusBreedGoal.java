package ovh.astarivi.mobs.entity.forks;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;


/// This class is a direct copy of the original BreedGoal goal, but allows for NoMalusAnimals instead of plain
/// animals.
public class NoMalusBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range((double)8.0F).ignoreLineOfSight();
    protected final NoMalusAnimal animal;
    private final Class<? extends NoMalusAnimal> partnerClass;
    protected final ServerLevel level;
    @Nullable
    protected NoMalusAnimal partner;
    private int loveTime;
    private final double speedModifier;

    public NoMalusBreedGoal(NoMalusAnimal animal, double d) {
        this(animal, d, animal.getClass());
    }

    public NoMalusBreedGoal(NoMalusAnimal animal, double d, Class<? extends NoMalusAnimal> class_) {
        this.animal = animal;
        this.level = getServerLevel(animal);
        this.partnerClass = class_;
        this.speedModifier = d;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60 && !this.partner.isPanicking();
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < (double)9.0F) {
            this.breed();
        }

    }

    @Nullable
    private NoMalusAnimal getFreePartner() {
        List<? extends NoMalusAnimal> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate((double)8.0F));
        double d = Double.MAX_VALUE;
        NoMalusAnimal animal = null;

        for(NoMalusAnimal animal2 : list) {
            if (this.animal.canMate(animal2) && !animal2.isPanicking() && this.animal.distanceToSqr(animal2) < d) {
                animal = animal2;
                d = this.animal.distanceToSqr(animal2);
            }
        }

        return animal;
    }

    protected void breed() {
        this.animal.spawnChildFromBreeding(this.level, this.partner);
    }
}
