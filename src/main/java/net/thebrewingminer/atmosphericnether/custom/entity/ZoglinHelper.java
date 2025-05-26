package net.thebrewingminer.atmosphericnether.custom.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class ZoglinHelper {
    public static boolean checkZoglinSpawnRules(EntityType<Zoglin> type, LevelAccessor accessor, MobSpawnType spawnType, BlockPos pos, RandomSource source) {
        return !accessor.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK);
    }
}
