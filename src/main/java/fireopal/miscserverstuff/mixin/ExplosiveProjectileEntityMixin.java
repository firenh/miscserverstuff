package fireopal.miscserverstuff.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fireopal.miscserverstuff.MiscServerStuff;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldEvents;

@Mixin(ExplosiveProjectileEntity.class)
public class ExplosiveProjectileEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (((ExplosiveProjectileEntity)(Object)this).isTouchingWater()) {
            MiscServerStuff.LOGGER.info("lmao");

            ((ExplosiveProjectileEntity)(Object)this).world.syncWorldEvent(WorldEvents.FIRE_EXTINGUISHED, new BlockPos(((ExplosiveProjectileEntity)(Object)this).getPos()), 0);
            ((ExplosiveProjectileEntity)(Object)this).world.syncWorldEvent(WorldEvents.WET_SPONGE_DRIES_OUT, new BlockPos(((ExplosiveProjectileEntity)(Object)this).getPos()), 0);
            

            Identifier identifier = MiscServerStuff.SMALL_FIREBALL_LOOT_TABLE_ID;
            MiscServerStuff.LOGGER.info(identifier.toString());
            LootTable lootTable = ((ExplosiveProjectileEntity)(Object)this).world.getServer().getLootManager().getTable(identifier);

            LootContext.Builder builder = new LootContext.Builder((ServerWorld) ((ExplosiveProjectileEntity)(Object)this).world)
                .random(Random.create())
                .luck(0f)
                .parameter(LootContextParameters.DAMAGE_SOURCE, DamageSource.DROWN)
                .parameter(LootContextParameters.ORIGIN, ((ExplosiveProjectileEntity)(Object)this).getPos())
                .parameter(LootContextParameters.THIS_ENTITY, ((ExplosiveProjectileEntity)(Object)this));

            if (!Objects.isNull(((ExplosiveProjectileEntity)(Object)this).getOwner())) {
                builder.optionalParameter(LootContextParameters.KILLER_ENTITY, ((ExplosiveProjectileEntity)(Object)this).getOwner());
            }
            
            lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), ((ExplosiveProjectileEntity)(Object)this)::dropStack);

            ((ExplosiveProjectileEntity)(Object)this).kill();
        }
    }
}
