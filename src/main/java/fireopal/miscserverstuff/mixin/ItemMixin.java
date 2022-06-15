package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fireopal.miscserverstuff.item.NewEchoShardLogic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At("TAIL"), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (user.getStackInHand(hand).getItem() == Items.ECHO_SHARD) {
            cir.setReturnValue(NewEchoShardLogic.use(world, user, user.getStackInHand(hand)));
        }
    }
}
