package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.entity.BeaconBlockEntity;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @ModifyVariable(ordinal = 0, at = @At("STORE"), method = "applyPlayerEffects")
    private static double modifyRange(double d) {
        return Math.abs(((d - 10) * 2) + 10);
    }
}
