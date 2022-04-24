package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {
    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof HoeItem) {
            cir.setReturnValue(true);
        }
    }
}
