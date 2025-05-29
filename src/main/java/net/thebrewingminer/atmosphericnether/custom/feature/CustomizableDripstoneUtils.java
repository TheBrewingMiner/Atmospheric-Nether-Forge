package net.thebrewingminer.atmosphericnether.custom.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

public class CustomizableDripstoneUtils {
    public CustomizableDripstoneUtils() {
    }

    protected static double getDripstoneHeight(double p_159624_, double p_159625_, double p_159626_, double p_159627_) {
        if (p_159624_ < p_159627_) {
            p_159624_ = p_159627_;
        }

        double $$4 = 0.384;
        double $$5 = p_159624_ / p_159625_ * 0.384;
        double $$6 = 0.75 * Math.pow($$5, 1.3333333333333333);
        double $$7 = Math.pow($$5, 0.6666666666666666);
        double $$8 = 0.3333333333333333 * Math.log($$5);
        double $$9 = p_159626_ * ($$6 - $$7 - $$8);
        $$9 = Math.max($$9, 0.0);
        return $$9 / 0.384 * p_159625_;
    }

    protected static boolean isCircleMostlyEmbeddedInStone(WorldGenLevel level, BlockPos pos, int p_159642_) {
        if (isEmptyOrWaterOrLava(level, pos)) {
            return false;
        } else {
            float $$3 = 6.0F;
            float $$4 = 6.0F / (float)p_159642_;

            for(float $$5 = 0.0F; $$5 < 6.2831855F; $$5 += $$4) {
                int $$6 = (int)(Mth.cos($$5) * (float)p_159642_);
                int $$7 = (int)(Mth.sin($$5) * (float)p_159642_);
                if (isEmptyOrWaterOrLava(level, pos.offset($$6, 0, $$7))) {
                    return false;
                }
            }

            return true;
        }
    }

    protected static boolean isEmptyOrWater(LevelAccessor accessor, BlockPos pos) {
        return accessor.isStateAtPosition(pos, CustomizableDripstoneUtils::isEmptyOrWater);
    }

    protected static boolean isEmptyOrWaterOrLava(LevelAccessor p_159660_, BlockPos p_159661_) {
        return p_159660_.isStateAtPosition(p_159661_, CustomizableDripstoneUtils::isEmptyOrWaterOrLava);
    }

    protected static void buildBaseToTipColumn(Direction p_159652_, int p_159653_, boolean p_159654_, Consumer<BlockState> p_159655_) {
        if (p_159653_ >= 3) {
            p_159655_.accept(createPointedDripstone(p_159652_, DripstoneThickness.BASE));

            for(int $$4 = 0; $$4 < p_159653_ - 3; ++$$4) {
                p_159655_.accept(createPointedDripstone(p_159652_, DripstoneThickness.MIDDLE));
            }
        }

        if (p_159653_ >= 2) {
            p_159655_.accept(createPointedDripstone(p_159652_, DripstoneThickness.FRUSTUM));
        }

        if (p_159653_ >= 1) {
            p_159655_.accept(createPointedDripstone(p_159652_, p_159654_ ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
        }

    }

    protected static void growPointedDripstone(LevelAccessor accessor, BlockPos pos, Direction direction, int int1, boolean bool) {
        if (isDripstoneBase(accessor.getBlockState(pos.relative(direction.getOpposite())))) {
            BlockPos.MutableBlockPos mutablePos = pos.mutable();
            buildBaseToTipColumn(direction, int1, bool, (blockState) -> {
                if (blockState.is(Blocks.POINTED_DRIPSTONE)) {
                    blockState = (BlockState)blockState.setValue(PointedDripstoneBlock.WATERLOGGED, accessor.isWaterAt(mutablePos));
                }

                accessor.setBlock(mutablePos, blockState, 2);
                mutablePos.move(direction);
            });
        }
    }

    protected static boolean placeDripstoneBlockIfPossible(LevelAccessor p_190854_, BlockPos p_190855_) {
        BlockState $$2 = p_190854_.getBlockState(p_190855_);
        if ($$2.is(BlockTags.DRIPSTONE_REPLACEABLE)) {
            p_190854_.setBlock(p_190855_, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), 2);
            return true;
        } else {
            return false;
        }
    }

    private static BlockState createPointedDripstone(Direction p_159657_, DripstoneThickness p_159658_) {
        return (BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, p_159657_)).setValue(PointedDripstoneBlock.THICKNESS, p_159658_);
    }

    public static boolean isDripstoneBaseOrLava(BlockState state) {
        return isDripstoneBase(state) || state.is(Blocks.LAVA);
    }

    public static boolean isDripstoneBase(BlockState p_159663_) {
        return p_159663_.is(Blocks.DRIPSTONE_BLOCK) || p_159663_.is(BlockTags.DRIPSTONE_REPLACEABLE);
    }

    public static boolean isEmptyOrWater(BlockState p_159665_) {
        return p_159665_.isAir() || p_159665_.is(Blocks.WATER);
    }

    public static boolean isNeitherEmptyNorWater(BlockState state) {
        return !state.isAir() && !state.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(BlockState p_159667_) {
        return p_159667_.isAir() || p_159667_.is(Blocks.WATER) || p_159667_.is(Blocks.LAVA);
    }
}
