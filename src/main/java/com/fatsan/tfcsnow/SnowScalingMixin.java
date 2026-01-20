package com.fatsan.tfcsnow;

import net.dries007.tfc.world.climate.Climate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowLayerBlock.class)
public class SnowScalingMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void tfcSnowScaling(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {

        float temp = Climate.getTemperature(level, pos);

        int totalLayers =
                temp <= -16 ? 30 :
                temp <= -14 ? 28 :
                temp <= -12 ? 24 :
                temp <= -10 ? 20 :
                temp <= -8  ? 16 :
                temp <= -6  ? 12 :
                temp <= -4  ? 8  :
                temp <= -2  ? 4  : 0;

        int blocks = totalLayers / 8;
        int layers = totalLayers % 8;

        for (int i = 0; i < blocks; i++) {
            level.setBlock(pos.above(i), SnowLayerBlock.defaultBlockState(), 3);
        }

        if (layers > 0) {
            level.setBlock(
                pos.above(blocks),
                SnowLayerBlock.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layers),
                3
            );
        }

        ci.cancel();
    }
}
