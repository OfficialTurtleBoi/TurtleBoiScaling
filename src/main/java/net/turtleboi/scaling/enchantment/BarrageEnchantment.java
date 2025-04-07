package net.turtleboi.scaling.enchantment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.turtleboi.scaling.entity.projectile.BarrageArrow;
import net.turtleboi.turtlecore.client.util.ParticleSpawnQueue;
import net.turtleboi.turtlecore.spells.SpellScheduler;
import org.jetbrains.annotations.NotNull;

public class BarrageEnchantment extends Enchantment{
    private static final SpellScheduler spellScheduler = new SpellScheduler();
    public BarrageEnchantment(Enchantment.Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(rarity, category, slots);
    }

    @Override
    public int getMinCost(int level) {
        return 10 + level * 5;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack itemStack) {
        return itemStack.is(Items.BOW) || itemStack.is(Items.CROSSBOW)|| super.canApplyAtEnchantingTable(itemStack);
    }

    public static void onProjectileLaunch(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Arrow arrow) || entity instanceof BarrageArrow || entity.tickCount > 5) {
            return;
        }

        Entity shooterEntity = arrow.getOwner();
        if (shooterEntity instanceof LivingEntity livingShooter) {
            ItemStack bow = livingShooter.getMainHandItem();
            int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.RAPID_FIRE.get(), bow);
            if (level <= 0) {
                //System.out.println("Enchantment not present!");
                return;
            }
            //System.out.println("Spawning more arrows!");

            int extraArrowCount = level + 1;
            Level skeletonLevel = livingShooter.level();
            float entityXRot = livingShooter.getXRot();
            float entityYRot = livingShooter.getYRot();
            Vec3 originalVelocity = arrow.getDeltaMovement();
            double vx = originalVelocity.x;
            double vy = originalVelocity.y;
            double vz = originalVelocity.z;

            for (int i = 0; i < extraArrowCount; i++) {
                int delayTicks = 100 * (i + 1);
                scheduleExtraArrow(livingShooter, skeletonLevel, entityXRot, entityYRot, vx, vy, vz, delayTicks);
            }
        } else {
            System.out.println("Arrow has no living owner!");
        }
    }

    private static void scheduleExtraArrow(LivingEntity livingEntity, Level level, float entityXRot, float entityYRot,
                                           double vx, double vy, double vz, int delayTicks) {
        ParticleSpawnQueue.schedule(delayTicks, () -> {
            BarrageArrow extraArrow = new BarrageArrow(level, livingEntity);
            extraArrow.shootFromRotation(livingEntity, entityXRot, entityYRot, 0.0F, (float) 0, (float) 1);
            extraArrow.setDeltaMovement(vx, vy, vz);
            extraArrow.pickup = AbstractArrow.Pickup.DISALLOWED;

            level.addFreshEntity(extraArrow);
        });
    }
}
