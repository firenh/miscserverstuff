package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(MendingEnchantment.class)
public class MendingEnchantmentMixin extends Enchantment {
    private MendingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    public boolean isAcceptableItem(ItemStack stack) {
        Item item = stack.getItem();
        
        if (item == Items.ELYTRA) {
            return false;
        }

        return super.isAcceptableItem(stack);
    }
}
