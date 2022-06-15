package fireopal.miscserverstuff.item;

import java.util.List;
import java.util.Objects;

import fireopal.miscserverstuff.MiscServerStuff;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NewEchoShardLogic {
    public static void init() {
        DispenserBlock.registerBehavior(Items.ECHO_SHARD, new DispenserBehavior() {
            @Override
            public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
                Direction dir = pointer.getWorld().getBlockState(pointer.getPos()).get(DispenserBlock.FACING);
                BlockPos pos = pointer.getPos();
                Vec3d posVec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                runSonicBoom(pointer.getWorld(), null, posVec3d, getDispenserDirectionAsVec3d(dir), 25, 15f, 5f);
                stack.decrement(1);
                return stack;
            }
           
        });
    }

    private static Vec3d getDispenserDirectionAsVec3d(Direction dir) {
        BlockPos pos = new BlockPos(0, 0, 0).offset(dir);
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void playChargeSound(Vec3d pos, World world) {
        for (PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new PlaySoundIdS2CPacket(SoundEvents.ENTITY_WARDEN_SONIC_CHARGE.getId(), SoundCategory.HOSTILE, pos, 1f, world.getRandom().nextFloat() + 0.5f, world.getRandom().nextLong()));
            }
        }
    }

    public static void createChargeParticles(PlayerEntity user, World world, int count) {
        Vec3d pos = user.getPos();

        MiscServerStuff.LOGGER.info("itemUseTime: " + user.getItemUseTime());

        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnParticles(
                ParticleTypes.SCULK_CHARGE_POP,
                pos.x, 
                (pos.y + user.getEyeY()) / 2, 
                pos.z, 
                count, 
                1.0, 
                1.0, 
                1.0, 
                0.0
            );
        }
    }

    public static TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Vec3d look = getLookVector(user);

        runSonicBoom(world, user, user.getEyePos(), look, 15, 10f, 5f);

		ItemStack itemStack = user.getStackInHand(hand);
        itemStack.decrement(1);
    
        return TypedActionResult.success(itemStack, world.isClient());
    }

    public static Vec3d runSonicBoom(World world, Entity user, Vec3d pos, Vec3d look, int range, float initalDamage, float endDamage) {
        world.playSound(pos.x, pos.y, pos.z, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.HOSTILE, 1, 1, false);

        for (PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new PlaySoundIdS2CPacket(SoundEvents.ENTITY_WARDEN_SONIC_BOOM.getId(), SoundCategory.HOSTILE, pos, 1f, world.getRandom().nextFloat() + 0.5f, world.getRandom().nextLong()));
            }
        }

        if ((!Objects.isNull(user)) && user instanceof LivingEntity) {
            ((LivingEntity) user).takeKnockback(1, look.getX(), look.getZ());
        }
        
        int i = 0;
        float damageLost = (endDamage - initalDamage) / ((float) range);

        MiscServerStuff.LOGGER.info("damageLost = " + damageLost);

        while (i < range) {
            i += 1;

            Vec3d vec3d4 = pos.add(look.multiply(i));

            if (world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            
            List<Entity> entitiesAffected = world.getEntitiesByType(
                TypeFilter.instanceOf(Entity.class), 
                new Box(vec3d4.add(-2, -2, -2), vec3d4.add(2, 2, 2)),
                entity -> (!entity.equals(user)) && (entity.getPos().isInRange(vec3d4, 1) || entity.getEyePos().isInRange(vec3d4, 1) || new Vec3d(entity.getX(), (entity.getEyeY() + entity.getY()) / 2, entity.getZ()).isInRange(vec3d4, 1))
            );

            if (!entitiesAffected.isEmpty()) {
                float damage = initalDamage + (i * damageLost);

                MiscServerStuff.LOGGER.info("damage = " + damage);

                DamageSource damageSource = DamageSource.sonicBoom(user);
                DamageSource damageSourceDispenser = DamageSource.MAGIC;

                for (Entity entity : entitiesAffected) {
                    entity.damage(Objects.isNull(user) ? damageSourceDispenser : damageSource, damage);

                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).takeKnockback(0.1 * damage, -look.getX(), -look.getZ());
                    }
                }

                MiscServerStuff.LOGGER.info(entitiesAffected.toString());

                break;
            }

            BlockPos blockPos = new BlockPos(vec3d4);
            BlockState state = world.getBlockState(blockPos);

            if (state.getBlock() == Blocks.SCULK_SHRIEKER) {
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState());

                ItemEntity itemEntity = new ItemEntity(world, vec3d4.x, vec3d4.y, vec3d4.z, new ItemStack(Items.ECHO_SHARD, world.getRandom().nextBetween(1, 2)));
                world.spawnEntity(itemEntity);

                if (world instanceof ServerWorld) {
                    ((ServerWorld) world).spawnParticles(ParticleTypes.EXPLOSION, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
                }

                break;

                // dropLoot(world, user, vec3d4);
            }

            if (state.isOpaque() && state.isSolidBlock(world, blockPos)) {
                MiscServerStuff.LOGGER.info(world.getBlockState(new BlockPos(vec3d4)).toString());
                break;
            }
        }

        return pos.add(look.multiply(i));
    }

    private static Vec3d getLookVector(PlayerEntity user) {
        float yaw = user.getYaw();
        float pitch = user.getPitch();
        float roll = 0.0f;

        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float g = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

        return new Vec3d(f, g, h);
    }
}
