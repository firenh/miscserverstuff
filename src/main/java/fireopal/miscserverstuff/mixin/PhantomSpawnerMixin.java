package fireopal.miscserverstuff.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import fireopal.miscserverstuff.MixinHelpers;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.spawner.PhantomSpawner;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
    // @ModifyVariable(method = "spawn", at = @At("STORE"), ordinal = 0)
    @Redirect(
        method = "spawn", 
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;getPlayers()Ljava/util/List;"
        )
    )
    private List<ServerPlayerEntity> modifyWorldGetPlayers(ServerWorld world) {
        List<ServerPlayerEntity> players = world.getPlayers();
        ArrayList<ServerPlayerEntity> playersNew = new ArrayList<>();

        for (ServerPlayerEntity p : players) {
            if (MixinHelpers.canSpawnPhantomsFor(p)) {
                playersNew.add(p);
            }
        }

        return playersNew;
    }
}
