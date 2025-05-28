package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
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
    private boolean reEnableAquiferOnLava(BlockState blockState, Block block, DensityFunction.FunctionContext context) {
        int yLevel = context.blockY();
        final int vanillaAquiferDisabledBelow = -54;        // Vanilla always fills eligible blocks with lava below y = -54.

//        computeSubstance() decision flow:
//        if (p_208187_ > 0.0) {
//            this.shouldScheduleFluidUpdate = false;
//            return null;
//        } else {
//            Aquifer.FluidStatus $$5 = this.globalFluidPicker.computeFluid($$2, $$3, $$4);
//            if ($$5.at($$3).is(Blocks.LAVA)) {                <------ This if-statement is what is being mixed into.
//                this.shouldScheduleFluidUpdate = false;               If true, aquifers are effectively "disabled" for lava.
//                return Blocks.LAVA.defaultBlockState();               If false, continue to aquifer calculations.
//            } else {...continue to aquifer calculations }             Therefore, we must trick the logic for lava to work.
//    }

        if (block == Blocks.LAVA && yLevel >= vanillaAquiferDisabledBelow){
            return false;                               // If the block is lava and is above Vanilla's threshold for lava fill, "skip" the lava check, effectively re-enabling aquifer logic.
        } else return blockState.is(block);             // Otherwise, check the fluid as usual.
    }
}