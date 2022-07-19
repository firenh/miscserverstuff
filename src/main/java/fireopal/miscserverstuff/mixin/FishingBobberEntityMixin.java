package fireopal.miscserverstuff.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {
    @Shadow @Final
    private int lureLevel;

    @Inject(at = @At("RETURN"), method = "pullHookedEntity")
    private void pullHookedEntity(Entity entity, CallbackInfo ci) {
        if (true /*lureLevel > 0*/) {
            Entity entity2 = ((FishingBobberEntity)(Object)this).getOwner();
            if (entity2 == null) {
                return;
            }
            Vec3d vec3d = new Vec3d(entity2.getX() - ((FishingBobberEntity)(Object)this).getX(), entity2.getY() - ((FishingBobberEntity)(Object)this).getY(), entity2.getZ() - ((FishingBobberEntity)(Object)this).getZ()).multiply(0.1);
            entity.setVelocity(entity.getVelocity().add(vec3d.multiply(30)));
        }
    }
}
