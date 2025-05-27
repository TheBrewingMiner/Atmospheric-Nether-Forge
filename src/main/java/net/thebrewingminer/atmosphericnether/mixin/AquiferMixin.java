package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public class AquiferMixin {
    @Redirect(
            method = "Lnet/minecraft/world/level/levelgen/Aquifer$NoiseBasedAquifer;computeSubstance(Lnet/minecraft/world/level/levelgen/DensityFunction$FunctionContext;D)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
                    ordinal = 0
            )
    )
    private boolean reEnableAquiferOnLava(BlockState blockState, Block block){
        if (block == Blocks.LAVA){
            return false;       // Always skip check if default fluid is lava.
        }
        return blockState.is(block);  // If logic falls through, act as normal.
    }
}
