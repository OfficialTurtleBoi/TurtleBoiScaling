package net.turtleboi.scaling.event;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.scaling.enchantment.BarrageEnchantment;
import net.turtleboi.scaling.util.EquipUtil;
import net.turtleboi.scaling.abilities.ScalingAbilities;
import net.turtleboi.turtlecore.init.CoreAttributeModifiers;

import java.util.*;

@Mod.EventBusSubscriber(modid = Scaling.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModBusEvents {
    private static final RandomSource random = RandomSource.createNewThreadLocalInstance();

    @SubscribeEvent
    public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if (livingEntity instanceof Monster || livingEntity instanceof FlyingMob || livingEntity instanceof EnderDragon ||
                livingEntity instanceof Shulker || livingEntity instanceof Slime || livingEntity instanceof ZombieHorse ||
                livingEntity instanceof SkeletonHorse || livingEntity instanceof Hoglin) {
            double multiplier = ScalingAbilities.getStaticGlobalMultiplier(level);

            //System.out.println("Applying multiplier: " + multiplier + " to mob: " + livingEntity.getName().getString());

            CoreAttributeModifiers.applyPermanentModifier(
                    livingEntity,
                    Attributes.MAX_HEALTH,
                    "ScalingMaxHealth",
                    multiplier,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            livingEntity.setHealth(livingEntity.getMaxHealth());

            CoreAttributeModifiers.applyPermanentModifier(
                    livingEntity,
                    Attributes.ATTACK_DAMAGE,
                    "ScalingAttackDamage",
                    multiplier,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            CoreAttributeModifiers.applyPermanentModifier(
                    livingEntity,
                    Attributes.ARMOR,
                    "ScalingArmor",
                    multiplier,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            if (livingEntity instanceof Zombie || livingEntity instanceof AbstractSkeleton || livingEntity instanceof AbstractPiglin) {
                if (multiplier >= 1.1) {
                    EquipUtil.equipMobWithGear((Mob) livingEntity, multiplier, random);
                }
            }

            if (livingEntity instanceof Skeleton skeleton) {

                if (multiplier >= 1.3 && skeleton.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {

                    List<MobEffect> harmfulEffects = new ArrayList<>();
                    for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
                        if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                            harmfulEffects.add(effect);
                        }
                    }

                    if (!harmfulEffects.isEmpty()) {
                        MobEffect selectedEffect = harmfulEffects.get(random.nextInt(harmfulEffects.size()));

                        //System.out.println("Skeleton " + skeleton.getName().getString()
                        //        + " is assigned negative effect: " + selectedEffect.getDescriptionId());

                        MobEffectInstance effectInstance = new MobEffectInstance(selectedEffect, 200, 0);
                        List<MobEffectInstance> effectList = Collections.singletonList(effectInstance);
                        ItemStack tippedArrow = new ItemStack(Items.TIPPED_ARROW);
                        PotionUtils.setCustomEffects(tippedArrow, effectList);

                        int color = PotionUtils.getColor(effectList);
                        tippedArrow.getOrCreateTag().putInt("CustomPotionColor", color);
                        skeleton.setItemSlot(EquipmentSlot.OFFHAND, tippedArrow);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        BarrageEnchantment.onProjectileLaunch(event);
    }
}
