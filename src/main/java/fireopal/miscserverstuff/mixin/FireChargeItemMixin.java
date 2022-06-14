package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fireopal.miscserverstuff.MixinHelpers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin extends Item {
	public FireChargeItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		Vec3d pos = user.getEyePos();

		SmallFireballEntity fireball = new SmallFireballEntity(world, user, 0, 0, 0);
		fireball.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 0.01f, 0f);

		double velocityFactorAboveNormal = Math.sqrt(
			Math.pow(fireball.getVelocity().x, 2) +
			Math.pow(fireball.getVelocity().y, 2) + 
			Math.pow(fireball.getVelocity().z, 2)
		) / MixinHelpers.getFireballVelocityFactor();

		fireball.powerX = fireball.getVelocity().x / velocityFactorAboveNormal;
		fireball.powerY = fireball.getVelocity().y / velocityFactorAboveNormal;
		fireball.powerZ = fireball.getVelocity().z / velocityFactorAboveNormal;
		fireball.setPos(pos.getX(), pos.getY(), pos.getZ());

		world.syncWorldEvent(WorldEvents.BLAZE_SHOOTS, new BlockPos(pos), 0);
		world.spawnEntity(fireball);

		if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

		user.incrementStat(Stats.USED.getOrCreateStat((FireChargeItem)(Object)this));

        return TypedActionResult.success(itemStack, world.isClient());
    }

	@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
	private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (!context.getPlayer().isSneaking()) {
			TypedActionResult<ItemStack> typedActionResult = ((FireChargeItem)(Object)this).use(
				context.getWorld(), 
				context.getPlayer(), 
				context.getHand()
			);

			cir.setReturnValue(typedActionResult.getResult());
		}
	}
}
