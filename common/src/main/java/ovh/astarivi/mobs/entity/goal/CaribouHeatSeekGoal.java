package ovh.astarivi.mobs.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import ovh.astarivi.mobs.entity.CaribouEntity;

import java.util.ArrayList;
import java.util.EnumSet;

public class CaribouHeatSeekGoal extends MoveToBlockGoal {
    private static final ArrayList<BlockPos> offsets = precomputeOffsets();
    private final CaribouEntity caribou;
    private boolean shouldStop = false;

    public CaribouHeatSeekGoal(CaribouEntity caribou, double d, int i) {
        super(caribou, d, i);
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        this.caribou = caribou;
    }

    private static @NotNull ArrayList<BlockPos> precomputeOffsets() {
        ArrayList<BlockPos> offsets = new ArrayList<>();
        // How many blocks to look for upwards
        int maxYDifference = -1;
        int searchRange = 24;

        for (int y = 0; y >= maxYDifference; y--) {
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
        return 1;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        BlockState state = levelReader.getBlockState(blockPos);

        return state.is(Blocks.MAGMA_BLOCK) || (state.getBlock() instanceof LiquidBlock && state.getFluidState().is(Fluids.LAVA));
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
        return caribou.isNetherVariant() && caribou.level().dimension() != Level.NETHER && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !shouldStop && super.canContinueToUse();
    }

    @Override
    public void stop() {
        super.stop();
        shouldStop = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (isReachedTarget()) {
            shouldStop = true;
        }
    }
}
