package fireopal.miscserverstuff;

import java.util.Objects;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class MixinHelpers {
    public static Vec3d vec3iAsVec3dCentered(Vec3i pos) {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static boolean canSpawnPhantomsFor(ServerPlayerEntity player) {
        if (
            player.world.getRegistryKey() != player.getSpawnPointDimension()
        ) {
            // MiscServerStuff.LOGGER.info("Tried to spawn phantoms for " + player.toString() + "; they're not in their spawn dimension so it worked!");
            return true;
        }

        if (
            Objects.isNull(player.getSpawnPointPosition())
        ) {
            // MiscServerStuff.LOGGER.info("Spawned phantoms for " + player.toString() + "; they don't have a spawn point!");
            return true;
        }

        if (
            player.getPos().distanceTo(
                MixinHelpers.vec3iAsVec3dCentered(player.getSpawnPointPosition())
            ) > MiscServerStuff.DISTANCE_FROM_SPAWN_FOR_PHANTOMS
        ) {
            // MiscServerStuff.LOGGER.info("Spawned phantoms for " + player.toString() + "; they're far away from their spawn point!");
            return true;
        }

        // MiscServerStuff.LOGGER.info("Tried to spawn phantoms for " + player.toString() + "; failed! Too close to home. ");
        return false;
    } 
}
