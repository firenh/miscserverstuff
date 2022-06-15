package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fireopal.miscserverstuff.MiscServerStuff;
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
            if (user.getItemUseTime() == 0) {
                NewEchoShardLogic.playChargeSound(user.getPos(), world);
            }

            NewEchoShardLogic.createChargeParticles(user, world, 20);

            if (user.getItemUseTime() >= 34.0) {
                TypedActionResult<ItemStack> result = NewEchoShardLogic.use(world, user, hand);
                user.getItemCooldownManager().set(Items.ECHO_SHARD, user.isCreative() ? 10 : 300);
                cir.setReturnValue(result);
            }
        }
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    public void getMaxUseTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.isOf(Items.ECHO_SHARD)) {
            MiscServerStuff.LOGGER.info("Max Use Time for Echo Shard");

            cir.setReturnValue(35);
        }
    }
}
