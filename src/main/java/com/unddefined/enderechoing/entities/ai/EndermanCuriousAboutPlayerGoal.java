package com.unddefined.enderechoing.entities.ai;

import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.EnumSet;

public class EndermanCuriousAboutPlayerGoal extends Goal {
    private static final double MAX_DISTANCE = 32.0;
    private static final double MIN_RADIUS = 3.0;
    private static final double MAX_RADIUS = 9.0;
    private static final int MIN_IDLE = 20;   // 1 秒
    private static final int MAX_IDLE = 100;   // 3 秒
    private int attractionCooldown = 60;
    private final EnderMan enderman;
    private int idleTicks = 0;
    private int teleportCooldown = 0;
    private Player target;

    public EndermanCuriousAboutPlayerGoal(EnderMan enderman) {
        this.enderman = enderman;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse()  {
        if (attractionCooldown > 0) {
            attractionCooldown--;
            return false;
        }
        var player = enderman.level().getNearestPlayer(enderman, MAX_DISTANCE);
        CuriosApi.getCuriosInventory(player).flatMap(handler -> handler.findCurios(ItemRegistry.ENDER_ECHOING_EYE.get())
                .stream().findFirst()).ifPresentOrElse(slot -> target = player, () -> target = null);
        return target != null && !enderman.isAggressive();
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && !enderman.isAggressive() && enderman.distanceTo(target) <= MAX_DISTANCE / 2;
    }

    @Override
    public void start() {
        idleTicks = 0;
        if (teleportTowards(target)) teleportCooldown = 0;
        moveAroundPlayer(1);
    }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) return;
        enderman.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (isLookingAtMe(target)) {
            if (teleportAwayFromPlayer()) teleportCooldown = 80;
            target = null;
            attractionCooldown = 300;
            return;
        }

        if (teleportCooldown > 0) teleportCooldown--;

        if (idleTicks < 15 && enderman.getRandom().nextFloat() < 0.03F) {
            if (teleportAwayFromPlayer()) teleportCooldown = 30;
            attractionCooldown = 100;
            return;
        }

        if (idleTicks == 0 && enderman.distanceTo(target) < 7 && enderman.getRandom().nextFloat() < 0.5F) enderman.getNavigation().stop();

        if (enderman.distanceTo(target) < 2.5) {
            if (teleportAwayFromPlayer()) teleportCooldown = 40; // 2 秒冷却
            return;
        }

        // 正在“观察”
        if (idleTicks > 0) {
            idleTicks--;
            return;
        }
        if (idleTicks == 0 && enderman.getRandom().nextFloat() < 0.3F) {// 30% 概率直接结束本 Goal
            if (teleportAwayFromPlayer()) teleportCooldown = 60;
            attractionCooldown = 200;
            target = null;
            return;
        }
        // 刚走完一段路，进入 idle
        if (enderman.getNavigation().isDone()) idleTicks = Mth.nextInt(enderman.getRandom(), MIN_IDLE, MAX_IDLE);
    }

    private boolean isLookingAtMe(Player player) {
        Vec3 vec3 = player.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(enderman.getX() - player.getX(), enderman.getEyeY() - player.getEyeY(), enderman.getZ() - player.getZ());
        return vec3.dot(vec31.normalize()) > 1.0 - 0.025 / vec31.length() && player.hasLineOfSight(enderman);
    }

    private void moveAroundPlayer(double speed) {
        double angle = enderman.getRandom().nextDouble() * Math.PI * 2;
        double radius = Mth.lerp(enderman.getRandom().nextDouble(), MIN_RADIUS, MAX_RADIUS);

        double x = target.getX() + Math.cos(angle) * radius;
        double z = target.getZ() + Math.sin(angle) * radius;

        enderman.getNavigation().moveTo(x, target.getY(), z, speed);
    }

    private boolean teleportAwayFromPlayer() {
        enderman.getNavigation().stop();
        idleTicks = 60;
        return teleport();
    }

    private boolean teleport() {
        if (!enderman.level().isClientSide() && enderman.isAlive()) {
            double d0 = enderman.getX() + (enderman.getRandom().nextDouble() - 0.5) * 16.0;
            double d1 = enderman.getY() + (double) (enderman.getRandom().nextInt(16) - 32);
            double d2 = enderman.getZ() + (enderman.getRandom().nextDouble() - 0.5) * 16.0;
            return teleport(d0, d1, d2);
        } else return false;
    }

    boolean teleportTowards(Entity target) {
        Vec3 vec3 = new Vec3(enderman.getX() - target.getX(), enderman.getY(0.5) - target.getEyeY(), enderman.getZ() - target.getZ());
        vec3 = vec3.normalize();
        double d0 = 16.0;
        double d1 = enderman.getX() + (enderman.getRandom().nextDouble() - 0.5) * 8.0 - vec3.x * d0;
        double d2 = enderman.getY() + (double) (enderman.getRandom().nextInt(16) - 8) - vec3.y * d0;
        double d3 = enderman.getZ() + (enderman.getRandom().nextDouble() - 0.5) * 8.0 - vec3.z * d0;
        return teleport(d1, d2, d3);
    }

    private boolean teleport(double x, double y, double z) {
        if (teleportCooldown != 0) return false;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while (blockpos$mutableblockpos.getY() > enderman.level().getMinBuildHeight() && !enderman.level().getBlockState(blockpos$mutableblockpos).blocksMotion())
            blockpos$mutableblockpos.move(Direction.DOWN);

        BlockState blockstate = enderman.level().getBlockState(blockpos$mutableblockpos);
        if (blockstate.blocksMotion() && !blockstate.getFluidState().is(FluidTags.WATER)) {
            var event = net.neoforged.neoforge.event.EventHooks.onEnderTeleport(enderman, x, y, z);
            if (event.isCanceled()) return false;
            boolean flag2 = enderman.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
                enderman.level().gameEvent(GameEvent.TELEPORT, enderman.position(), GameEvent.Context.of(enderman));
                if (!enderman.isSilent()) {
                    enderman.level().playSound(null, enderman.xo, enderman.yo, enderman.zo, SoundEvents.ENDERMAN_TELEPORT, enderman.getSoundSource(), 1.0F, 1.0F);
                    enderman.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else return false;
    }

}
