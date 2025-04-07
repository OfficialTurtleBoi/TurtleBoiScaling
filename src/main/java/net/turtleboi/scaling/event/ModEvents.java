package net.turtleboi.scaling.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.scaling.abilities.ScalingAbilities;
import net.turtleboi.scaling.capabilities.party.PlayerPartyProvider;
import net.turtleboi.scaling.capabilities.targeting.PlayerTargetingProvider;

@Mod.EventBusSubscriber(modid = Scaling.MOD_ID)
public class ModEvents {
    private static final RandomSource random = RandomSource.createNewThreadLocalInstance();

    @SubscribeEvent
    public static void onAttachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!event.getObject().getCapability(PlayerPartyProvider.PLAYER_PARTY).isPresent()) {
                event.addCapability(new ResourceLocation(Scaling.MOD_ID, "player_party"), new PlayerPartyProvider());
            }
            if (!event.getObject().getCapability(PlayerTargetingProvider.PLAYER_TARGET).isPresent()) {
                event.addCapability(new ResourceLocation(Scaling.MOD_ID, "player_target"), new PlayerTargetingProvider(player));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(PlayerPartyProvider.PLAYER_PARTY).ifPresent(oldStore ->
                    event.getEntity().getCapability(PlayerPartyProvider.PLAYER_PARTY).ifPresent(newStore ->
                            newStore.copyFrom(oldStore)));
            event.getOriginal().getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(oldStore ->
                    event.getEntity().getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(newStore ->
                            newStore.copyFrom(oldStore)));
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event){
        if(!event.getLevel().isClientSide){
            if(event.getEntity() instanceof ServerPlayer player){

            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingHurtEvent event) {
        LivingEntity targetEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();
        Level level = targetEntity.level();
        double multiplier = ScalingAbilities.getStaticGlobalMultiplier(level);

        if (targetEntity instanceof Player player) {
            //Logic for if player gets hurt by something
        }


        if (sourceEntity instanceof Player player) {
             //Logic for if player hurts something
            double health = targetEntity.getHealth();
            double maxHealth = targetEntity.getMaxHealth();
            System.out.println("This mob has " + health + "/" + maxHealth + " health!");
        }

        if (sourceEntity instanceof Spider spider) {
            if (multiplier >= 0.75) {
                if (spider.hasEffect(MobEffects.INVISIBILITY)) {
                    event.setAmount(ScalingAbilities.spiderCloakDamage(multiplier, event.getAmount()));
                    spider.removeEffect(MobEffects.INVISIBILITY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingDamageEvent event){
        Entity hurtEntity = event.getEntity();
        if (hurtEntity instanceof LivingEntity livingEntity) {
            Level level = livingEntity.level();
            double multiplier = ScalingAbilities.getStaticGlobalMultiplier(level);

            if (livingEntity instanceof Zombie zombie) {

            }

            if (livingEntity instanceof Skeleton skeleton) {
                if (multiplier >= 0.25) {
                    ScalingAbilities.skeletonBoneSplinterAbility(skeleton, multiplier, event.getAmount());
                }

                if (multiplier >= 0.5) {
                    ScalingAbilities.skeletonDecoyAbility(skeleton, event.getAmount());
                }
            }

            if (event.getSource().getDirectEntity() instanceof Arrow) {
                livingEntity.hurtDuration = 1;
                livingEntity.hurtTime = 1;
                livingEntity.invulnerableTime = 1;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {

        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        double multiplier = ScalingAbilities.getStaticGlobalMultiplier(level);
        if (livingEntity instanceof Zombie zombie) {
            ScalingAbilities.handleZombieCooldowns(zombie);

            if (multiplier >= 0.25) {
                if (level.getRandom().nextDouble() <= multiplier) {
                    ScalingAbilities.zombieRageAbility(zombie, multiplier);
                }
            }

            if (multiplier >= 0.5) {
                if (level.getRandom().nextDouble() <= multiplier) {
                    ScalingAbilities.zombieRegenerationAbility(zombie, multiplier);
                }
            }
        }

        if (livingEntity instanceof Skeleton skeleton) {
            if (multiplier >= 0.75) {
                ScalingAbilities.skeletonRapidFire(skeleton, multiplier);
            }
        }

        if (livingEntity instanceof Spider spider) {
            ScalingAbilities.handleSpiderCooldowns(spider);

            if (multiplier >= 0.75) {
                ScalingAbilities.spiderCloakAbility(spider);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        double multiplier = ScalingAbilities.getStaticGlobalMultiplier(level);

        if (livingEntity instanceof Zombie zombie) {
            if (multiplier >= 0.75) {
                ScalingAbilities.zombieNecroticBurstAbility(zombie, multiplier);
            }
        }
    }
}
