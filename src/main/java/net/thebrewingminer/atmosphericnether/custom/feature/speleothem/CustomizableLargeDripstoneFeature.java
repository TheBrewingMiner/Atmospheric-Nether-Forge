package net.thebrewingminer.atmosphericnether.custom.feature.speleothem;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class CustomizableLargeDripstoneFeature extends Feature<CustomizableLargeDripstoneConfiguration> {

    public CustomizableLargeDripstoneFeature(Codec<CustomizableLargeDripstoneConfiguration> codec) {
        super(codec);
    }

    private static CustomizableLargeDripstoneFeature.ExtendedLargeDripstone makeDripstone(BlockPos origin, boolean pointingUp, RandomSource source, int radius, FloatProvider bluntness, FloatProvider scale) {
        return new CustomizableLargeDripstoneFeature.ExtendedLargeDripstone(origin, pointingUp, radius, bluntness.sample(source), scale.sample(source));
    }


    public boolean place(FeaturePlaceContext<CustomizableLargeDripstoneConfiguration> context) {

        CustomizableLargeDripstoneConfiguration config = context.config();
        BlockStateProvider blockProvided = config.block;
        TagKey<Block> suitableBaseTag = config.baseTag;

        WorldGenLevel worldgenLevel = context.level();
        BlockPos blockPos = context.origin();
        RandomSource randomSource = context.random();
        Block blockToPlace = blockProvided.getState(randomSource, blockPos).getBlock();
        if (!CustomizableDripstoneUtils.isEmptyOrWater(worldgenLevel, blockPos)) {
            return false;
        } else {
            Optional<Column> column = Column.scan(worldgenLevel, blockPos, config.floorToCeilingSearchRange, CustomizableDripstoneUtils::isEmptyOrWater, (level) -> CustomizableDripstoneUtils.isDripstoneBaseOrLava(level, suitableBaseTag));
            if (column.isPresent() && column.get() instanceof Column.Range) {
                Column.Range columnRange = (Column.Range)column.get();
                if (columnRange.height() < 4) {
                    return false;
                } else {
                    int columnPos = (int)((float)columnRange.height() * config.maxColumnRadiusToCaveHeightRatio);
                    int maxRadius = Mth.clamp(columnPos, config.columnRadius.getMinValue(), config.columnRadius.getMaxValue());
                    int randomRadius = Mth.randomBetweenInclusive(randomSource, config.columnRadius.getMinValue(), maxRadius);
                    CustomizableLargeDripstoneFeature.ExtendedLargeDripstone worldgenLevel0 = makeDripstone(blockPos.atY(columnRange.ceiling() - 1), false, randomSource, randomRadius, config.stalactiteBluntness, config.heightScale);
                    CustomizableLargeDripstoneFeature.ExtendedLargeDripstone worldgenLevel1 = makeDripstone(blockPos.atY(columnRange.floor() + 1), true, randomSource, randomRadius, config.stalagmiteBluntness, config.heightScale);
                    WindOffsetter worldgenLevel3;
                    if (worldgenLevel0.isSuitableForWind(config) && worldgenLevel1.isSuitableForWind(config)) {
                        worldgenLevel3 = new WindOffsetter(blockPos.getY(), randomSource, config.windSpeed);
                    } else {
                        worldgenLevel3 = WindOffsetter.noWind();
                    }

                    boolean worldgenLevel4 = worldgenLevel0.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(worldgenLevel, worldgenLevel3);
                    boolean worldgenLevel5 = worldgenLevel1.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(worldgenLevel, worldgenLevel3);
                    if (worldgenLevel4) {
                        worldgenLevel0.placeBlocks(worldgenLevel, randomSource, worldgenLevel3, blockToPlace, suitableBaseTag);
                    }

                    if (worldgenLevel5) {
                        worldgenLevel1.placeBlocks(worldgenLevel, randomSource, worldgenLevel3, blockToPlace, suitableBaseTag);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    static final class ExtendedLargeDripstone {
        private BlockPos root;
        private final boolean pointingUp;
        private int radius;
        private final double bluntness;
        private final double scale;

        ExtendedLargeDripstone(BlockPos p_197116_, boolean p_197117_, int p_197118_, double p_197119_, double p_197120_) {
            this.root = p_197116_;
            this.pointingUp = p_197117_;
            this.radius = p_197118_;
            this.bluntness = p_197119_;
            this.scale = p_197120_;
        }

        private int getHeight() {
            return this.getHeightAtRadius(0.0F);
        }

        private int getMinY() {
            return this.pointingUp ? this.root.getY() : this.root.getY() - this.getHeight();
        }

        private int getMaxY() {
            return !this.pointingUp ? this.root.getY() : this.root.getY() + this.getHeight();
        }

        boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(WorldGenLevel worldGenLevel, CustomizableLargeDripstoneFeature.WindOffsetter offsetter) {
            while(this.radius > 1) {
                BlockPos.MutableBlockPos blockPos = this.root.mutable();
                int dx = Math.min(10, this.getHeight());

                for(int randomSource = 0; randomSource < dx; ++randomSource) {
                    if (worldGenLevel.getBlockState(blockPos).is(Blocks.LAVA)) {
                        return false;
                    }

                    if (CustomizableDripstoneUtils.isCircleMostlyEmbeddedInStone(worldGenLevel, offsetter.offset(blockPos), this.radius)) {
                        this.root = blockPos;
                        return true;
                    }

                    blockPos.move(this.pointingUp ? Direction.DOWN : Direction.UP);
                }

                this.radius /= 2;
            }

            return false;
        }

        private int getHeightAtRadius(float p_159988_) {
            return (int)CustomizableDripstoneUtils.getDripstoneHeight(p_159988_, this.radius, this.scale, this.bluntness);
        }

        void placeBlocks(WorldGenLevel level, RandomSource source, WindOffsetter offsetter, Block blockToPlace, TagKey<Block> suitableBaseTag) {
            for(int dx = -this.radius; dx <= this.radius; ++dx) {
                for(int randomSource = -this.radius; randomSource <= this.radius; ++randomSource) {
                    float column = Mth.sqrt((float)(dx * dx + randomSource * randomSource));
                    if (!(column > (float)this.radius)) {
                        int columnRange = this.getHeightAtRadius(column);
                        if (columnRange > 0) {
                            if ((double)source.nextFloat() < 0.2) {
                                columnRange = (int)((float)columnRange * Mth.randomBetween(source, 0.8F, 1.0F));
                            }

                            BlockPos.MutableBlockPos columnPos = this.root.offset(dx, 0, randomSource).mutable();
                            boolean placedBlocks = false;
                            int maxHeight = this.pointingUp ? level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, columnPos.getX(), columnPos.getZ()) : Integer.MAX_VALUE;

                            for(int dy = 0; dy < columnRange && columnPos.getY() < maxHeight; ++dy) {
                                BlockPos offsetPos = offsetter.offset(columnPos);
                                if (CustomizableDripstoneUtils.isEmptyOrWaterOrLava(level, offsetPos)) {
                                    placedBlocks = true;
                                    Block currentBlock = blockToPlace;
                                    level.setBlock(offsetPos, currentBlock.defaultBlockState(), 2);
                                } else if (placedBlocks && level.getBlockState(offsetPos).is(suitableBaseTag)) {
                                    break;
                                }

                                columnPos.move(this.pointingUp ? Direction.UP : Direction.DOWN);
                            }
                        }
                    }
                }
            }

        }

        boolean isSuitableForWind(CustomizableLargeDripstoneConfiguration config) {
            return this.radius >= config.minRadiusForWind && this.bluntness >= (double)config.minBluntnessForWind;
        }
    }

    private static final class WindOffsetter {
        private final int originY;
        @Nullable
        private final Vec3 windSpeed;

        WindOffsetter(int originY, RandomSource p_225151_, FloatProvider p_225152_) {
            this.originY = originY;
            float windMagnitude = p_225152_.sample(p_225151_);
            float windDirection = Mth.randomBetween(p_225151_, 0.0F, 3.1415927F);
            this.windSpeed = new Vec3(Mth.cos(windDirection) * windMagnitude, 0.0, Mth.sin(windDirection) * windMagnitude);
        }

        private WindOffsetter() {
            this.originY = 0;
            this.windSpeed = null;
        }

        static CustomizableLargeDripstoneFeature.WindOffsetter noWind() {
            return new CustomizableLargeDripstoneFeature.WindOffsetter();
        }

        BlockPos offset(BlockPos pos) {
            if (this.windSpeed == null) {
                return pos;
            } else {
                int verticalOffset = this.originY - pos.getY();
                Vec3 windOffset = this.windSpeed.scale(verticalOffset);
                return pos.offset(Mth.floor(windOffset.x), 0, Mth.floor(windOffset.z));
            }
        }
    }
}
