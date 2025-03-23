package ovh.astarivi.mobs.entity.forks;

import java.util.List;

import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

public class NoMalusFollowParentGoal extends Goal {
    public static final int HORIZONTAL_SCAN_RANGE = 8;
    public static final int VERTICAL_SCAN_RANGE = 4;
    public static final int DONT_FOLLOW_IF_CLOSER_THAN = 3;
    private final NoMalusAnimal animal;
    @Nullable
    private NoMalusAnimal parent;
    private final double speedModifier;
    private int timeToRecalcPath;

    public NoMalusFollowParentGoal(NoMalusAnimal animal, double d) {
        this.animal = animal;
        this.speedModifier = d;
    }

    public boolean canUse() {
        if (this.animal.getAge() >= 0) {
            return false;
        } else {
            List<? extends NoMalusAnimal> list = this.animal.level().getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate((double)8.0F, (double)4.0F, (double)8.0F));
            NoMalusAnimal animal = null;
            double d = Double.MAX_VALUE;

            for(NoMalusAnimal animal2 : list) {
                if (animal2.getAge() >= 0) {
                    double e = this.animal.distanceToSqr(animal2);
                    if (!(e > d)) {
                        d = e;
                        animal = animal2;
                    }
                }
            }

            if (animal == null) {
                return false;
            } else if (d < (double)9.0F) {
                return false;
            } else {
                this.parent = animal;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        if (this.animal.getAge() >= 0) {
            return false;
        } else if (!this.parent.isAlive()) {
            return false;
        } else {
            double d = this.animal.distanceToSqr(this.parent);
            return !(d < (double)9.0F) && !(d > (double)256.0F);
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.parent = null;
    }

    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.animal.getNavigation().moveTo(this.parent, this.speedModifier);
        }
    }
}
