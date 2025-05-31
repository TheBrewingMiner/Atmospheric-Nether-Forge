package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(Zoglin.class)
public abstract class ZoglinSpawnCheckingMixin extends Monster implements Enemy, HoglinBase {

    protected ZoglinSpawnCheckingMixin(EntityType<? extends Monster> type, Level level){
        super(type, level);
    }

    @Unique
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        if (levelAccessor.getRandom().nextFloat() < 0.35F) {
            this.setBaby(true);
        }

        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, groupData);
    }
    
}
