package net.thebrewingminer.atmosphericnether.custom.feature;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

public class CustomizableDripstoneUtils {
    public CustomizableDripstoneUtils() {}

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

    protected static boolean isEmptyOrWaterOrLava(LevelAccessor accessor, BlockPos pos) {
        return accessor.isStateAtPosition(pos, CustomizableDripstoneUtils::isEmptyOrWaterOrLava);
    }

    protected static void buildBaseToTipColumn(Direction direction, int height, boolean merge_ends, Consumer<BlockState> stateConsumer) {
        if (height >= 3) {
            stateConsumer.accept(createPointedDripstone(direction, DripstoneThickness.BASE));

            for(int i = 0; i < height - 3; ++i) {
                stateConsumer.accept(createPointedDripstone(direction, DripstoneThickness.MIDDLE));
            }
        }

        if (height >= 2) {
            stateConsumer.accept(createPointedDripstone(direction, DripstoneThickness.FRUSTUM));
        }

        if (height >= 1) {
            stateConsumer.accept(createPointedDripstone(direction, merge_ends ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
        }

    }

    protected static void growPointedDripstone(LevelAccessor accessor, BlockPos pos, Direction direction, int height, boolean merge_ends, TagKey<Block> blockTag) {
        if (isDripstoneBase(accessor.getBlockState(pos.relative(direction.getOpposite())), blockTag)) {
            BlockPos.MutableBlockPos mutablePos = pos.mutable();
            buildBaseToTipColumn(direction, height, merge_ends, (blockState) -> {
                if (blockState.is(Blocks.POINTED_DRIPSTONE)) {
                    blockState = blockState.setValue(PointedDripstoneBlock.WATERLOGGED, accessor.isWaterAt(mutablePos));
                }

                accessor.setBlock(mutablePos, blockState, 2);
                mutablePos.move(direction);
            });
        }
    }

    protected static boolean placeDripstoneBlockIfPossible(LevelAccessor accessor, BlockPos pos, BlockState blockToPlace, TagKey<Block> blockTag) {
        BlockState current = accessor.getBlockState(pos);
        if (current.is(blockTag)) {
            accessor.setBlock(pos, blockToPlace, 2);
            return true;
        } else {
            return false;
        }
    }

    private static BlockState createPointedDripstone(Direction direction, DripstoneThickness thickness) {
        return (BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)).setValue(PointedDripstoneBlock.THICKNESS, thickness);
    }

    public static boolean isDripstoneBaseOrLava(BlockState state, TagKey<Block> blockTag) {
        return isDripstoneBase(state, blockTag) || state.is(Blocks.LAVA);
    }

    public static boolean isDripstoneBase(BlockState state, TagKey<Block> blockTag) {
        return state.is(Blocks.DRIPSTONE_BLOCK) || state.is(blockTag);
    }

    public static boolean isEmptyOrWater(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER);
    }

    public static boolean isNeitherEmptyNorWater(BlockState state) {
        return !state.isAir() && !state.is(Blocks.WATER);
    }

    public static boolean isEmptyOrWaterOrLava(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
    }
}
