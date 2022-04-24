package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.KnockbackEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(KnockbackEnchantment.class)
public class KnockbackEnchantmentMixin extends Enchantment {
    private KnockbackEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    public boolean isAcceptableItem(ItemStack stack) {
        Item item = stack.getItem();
        
        if (item == Items.STICK || item == Items.BLAZE_ROD) {
            return true;
        }

        return super.isAcceptableItem(stack);
    }
}
