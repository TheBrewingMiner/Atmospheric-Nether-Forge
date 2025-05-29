package net.thebrewingminer.atmosphericnether.custom.feature.speleothem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class CustomizableLargeDripstoneConfiguration extends LargeDripstoneConfiguration {
    public static final Codec<CustomizableLargeDripstoneConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockStateProvider.CODEC.fieldOf("block").forGetter(cfg -> cfg.block),
                    ResourceLocation.CODEC.xmap(id -> TagKey.create(Registry.BLOCK_REGISTRY, id), TagKey::location).fieldOf("base_tag").forGetter(cfg -> cfg.baseTag),
                    IntProvider.codec(1, 60).fieldOf("column_radius").forGetter(cfg -> cfg.columnRadius),
                    FloatProvider.codec(0.1F, 10.0F).fieldOf("stalactite_bluntness").forGetter(cfg -> cfg.stalactiteBluntness),
                    FloatProvider.codec(0.1F, 10.0F).fieldOf("stalagmite_bluntness").forGetter(cfg -> cfg.stalagmiteBluntness),
                    FloatProvider.codec(0F, 20.0F).fieldOf("height_scale").forGetter(cfg -> cfg.heightScale),
                    FloatProvider.codec(0.0F, 2.0F).fieldOf("wind_speed").forGetter(cfg -> cfg.windSpeed),
                    Codec.intRange(1, 100).fieldOf("min_radius_for_wind").forGetter(cfg -> cfg.minRadiusForWind),
                    Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter(cfg -> cfg.minBluntnessForWind),
                    Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").orElse(30).forGetter(cfg -> cfg.floorToCeilingSearchRange),
                    Codec.floatRange(0.0F, 5.0F).fieldOf("max_column_radius_to_cave_height_ratio").forGetter(cfg -> cfg.maxColumnRadiusToCaveHeightRatio)
            ).apply(instance, CustomizableLargeDripstoneConfiguration::new)
    );

    public final BlockStateProvider block;
    public final TagKey<Block> baseTag;

    public CustomizableLargeDripstoneConfiguration(BlockStateProvider block, TagKey<Block> baseTag, IntProvider columnRadius, FloatProvider stalactiteBluntness, FloatProvider stalagmiteBluntness, FloatProvider heightScale, FloatProvider windSpeed, int minRadiusForWind, float minBluntnessForWind, int floorToCeilingSearchRange, float maxColumnRadiusToCaveHeightRatio) {
        super(floorToCeilingSearchRange, columnRadius, heightScale, maxColumnRadiusToCaveHeightRatio, stalactiteBluntness, stalagmiteBluntness, windSpeed, minRadiusForWind, minBluntnessForWind);
        this.block = block;
        this.baseTag = baseTag;
    }
}
