package ovh.astarivi.mobs.entity.goal;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import ovh.astarivi.mobs.entity.BearEntity;
import ovh.astarivi.mobs.entity.generic.GenericAnimations;
import ovh.astarivi.mobs.entity.generic.GenericControllers;

import java.util.ArrayList;
import java.util.EnumSet;


public class BearFetchHoneyGoal extends MoveToBlockGoal {

    private static final ArrayList<BlockPos> offsets = precomputeOffsets();
    private int tickCounter = 0;
    private final BearEntity bear;
    private boolean shouldStop = false;

    public BearFetchHoneyGoal(BearEntity bear, double d, int i) {
        super(bear, d, i);
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        this.bear = bear;
    }

    private static @NotNull ArrayList<BlockPos> precomputeOffsets() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        // How many blocks to look for upwards
        int maxYDifference = 1;
        int searchRange = 24;

        for (int y = 0; y <= maxYDifference; y++) {
            for (int r = 0; r < searchRange; r++) {
                for (int x = -r; x <= r; x++) {
                    for (int z = -r; z <= r; z++) {
                        if (Math.abs(x) == r || Math.abs(z) == r) {
                            offsets.add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }

        return offsets;
    }

    @Override
    public double acceptedDistance() {
        return 3;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        BlockState state = levelReader.getBlockState(blockPos);

        return state.hasProperty(BeehiveBlock.HONEY_LEVEL) && state.getValue(BeehiveBlock.HONEY_LEVEL) == 5;
    }

    @Override
    protected boolean findNearestBlock() {
        BlockPos blockPos = this.mob.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos offset : offsets) {
            mutable.setWithOffset(blockPos, offset);
            if (this.mob.isWithinRestriction(mutable) && this.isValidTarget(this.mob.level(), mutable)) {
                this.blockPos = mutable;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canUse() {
        return !bear.isBaby() && !bear.isPacified() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !shouldStop && super.canContinueToUse();
    }

    @Override
    public void stop() {
        super.stop();
        shouldStop = false;
        bear.stopTriggeredAnim(GenericControllers.ATTACK.getName(), GenericAnimations.EAT.getName());
    }

    @Override
    public void tick() {
        super.tick();

        if (!isReachedTarget()) {
            return;
        }

        mob.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atCenterOf(this.blockPos));

        if (tickCounter == 61) {
            tickCounter = 0;
        }

        if (tickCounter == 0) {
            bear.stopTriggeredAnim(GenericControllers.ATTACK.getName(), GenericAnimations.EAT.getName());
            bear.triggerAnim(GenericControllers.ATTACK.getName(), GenericAnimations.EAT.getName());
        }

        if (tickCounter % 10 == 0) {
            mob.playSound(SoundEvents.BEEHIVE_DRIP, 3.0f, 1f);
        }

        if (tickCounter == 60) {
            Level level = mob.level();

            BlockState state = level.getBlockState(blockPos);
            if (state.getBlock() instanceof BeehiveBlock beehiveBlock && state.getValue(BeehiveBlock.HONEY_LEVEL) == 5) {
                beehiveBlock.resetHoneyLevel(level, state, blockPos);
                BeehiveBlock.dropHoneycomb(level, blockPos);
                level.playSound(null, blockPos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 2.0f, 1f);
                bear.setPacifiedTicks(6000);
                bear.forgetCurrentTargetAndRefreshUniversalAnger();
            }

            shouldStop = true;
        }

        tickCounter++;
    }
}
