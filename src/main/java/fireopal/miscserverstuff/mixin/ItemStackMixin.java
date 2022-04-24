package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getRepairCost", at = @At("HEAD"), cancellable = true)
    private void getRepairCost(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    @Inject(method = "setRepairCost", at = @At("HEAD"), cancellable = true)
    private void setRepairCost(int repairCost, CallbackInfo ci) {
        ci.cancel();
    }
}
