package net.turtleboi.scaling.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.scaling.client.renderer.NecroticBurstRenderer;
import net.turtleboi.scaling.enchantment.ModEnchantments;
import net.turtleboi.turtlecore.client.util.ParticleSpawnQueue;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScalingAbilities {
    public double getGlobalMultiplier(Level level){
        long gameTime = level.getGameTime();
        double multiplier = (gameTime / 24000.0) * 0.7;

        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        String formatted = df.format(multiplier);
        multiplier = Double.parseDouble(formatted);

        return multiplier;
    }

    public static double getStaticGlobalMultiplier(Level level){
        long gameTime = level.getGameTime();
        double multiplier = (gameTime / 24000.0) * 0.7;

        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        String formatted = df.format(multiplier);
        multiplier = Double.parseDouble(formatted);

        return 0.75;
    }

    public static void handleZombieCooldowns(Zombie zombie) {
        CompoundTag nbt = zombie.getPersistentData();

        if (!nbt.contains("cooldown")) {
            nbt.putInt("cooldown", 0);
        } else {
            int cooldown = nbt.getInt("cooldown");
            if (cooldown > 0) {
                nbt.putInt("cooldown", cooldown - 1);
            }
        }
    }

    public static void zombieRageAbility(Zombie zombie, double multiplier) {
        CompoundTag nbt = zombie.getPersistentData();
        if (nbt.contains("cooldown") && nbt.getInt("cooldown") <= 0) {
            Level level = zombie.level();
            RandomSource random = level.getRandom();

            List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, zombie.getBoundingBox().inflate(8 + (2 * multiplier)));
            boolean canSeePlayer = false;
            for (Player player : nearbyPlayers) {
                if (zombie.hasLineOfSight(player)) {
                    canSeePlayer = true;
                    break;
                }
            }

            if (canSeePlayer) {
                level.playSound(
                        zombie,
                        zombie.getOnPos(),
                        SoundEvents.POLAR_BEAR_DEATH,
                        SoundSource.HOSTILE,
                        1.0F,
                        1.0F + (random.nextFloat() - 0.5F) * 0.2F);

                zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, (int) (100 + (100 * multiplier)), (int) (2 + (1 * multiplier))));
                zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, (int) (100 + (100 * multiplier)), (int) (2 + (1 * multiplier))));
                nbt.putInt("cooldown", 600);
            }
        }
    }

    public static void zombieRegenerationAbility(Zombie zombie, double multiplier) {
        if (zombie.tickCount % Math.max(10, (20 - (10 * multiplier))) == 0) {
            zombie.heal((float) (4 * multiplier));
        }
    }

    public static void zombieNecroticBurstAbility(Zombie zombie, double multiplier) {
        double radius = 5.0 + (2 * multiplier);
        Level level = zombie.level();
        RandomSource random = level.getRandom();
        List<Player> players = level.getEntitiesOfClass(Player.class, zombie.getBoundingBox().inflate(radius));
        for (Player player : players) {
            player.hurt(level.damageSources().mobAttack(zombie), zombie.getMaxHealth() / 8);
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, (int) (200 * multiplier), (int) ((1 * multiplier) - 1)));
        }

        float pitch = 1.0F + (random.nextFloat() - 0.5F) * 0.2F;
        level.playSound(
                zombie,
                zombie.getOnPos(),
                SoundEvents.DRAGON_FIREBALL_EXPLODE,
                SoundSource.HOSTILE,
                0.5f,
                pitch);

        NecroticBurstRenderer.addAuraForEntity(zombie, System.currentTimeMillis(), 15, radius);

        for (int i = 0; i < 80; i += 1) {
            int delay = (int) (i * (20 * random.nextDouble()));
            double theta = random.nextDouble() * Math.PI;
            double phi = random.nextDouble() * 2 * Math.PI;
            double speed = (radius / 20) + random.nextDouble() * (radius / 20);
            double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
            double ySpeed = speed * Math.cos(theta);
            double zSpeed = speed * Math.sin(theta) * Math.sin(phi);
            double offX = (random.nextDouble() - 0.5) * 0.2;
            double offY = zombie.getBbHeight() / 2;
            double offZ = (random.nextDouble() - 0.5) * 0.2;
            double soundChance = random.nextDouble();

            ParticleSpawnQueue.schedule(delay, () -> {
                    CoreNetworking.sendToNear(new SendParticlesS2C(
                            new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ROTTEN_FLESH)),
                            zombie.getX() + offX,
                            zombie.getY() + offY,
                            zombie.getZ() + offZ,
                            xSpeed, ySpeed, zSpeed), zombie);

                    if (soundChance >= 0.65) {
                        level.playSound(
                                zombie,
                                zombie.getOnPos(),
                                SoundEvents.SLIME_HURT,
                                SoundSource.HOSTILE,
                                0.35f,
                                pitch);
                    }
            });
        }
    }

    public static void skeletonRapidFire(Skeleton skeleton, double multiplier) {
        if (skeleton.tickCount < 5) {
            //System.out.println("Skeleton can spawn with Rapid Fire");
            if (skeleton.getItemBySlot(EquipmentSlot.MAINHAND).getItem() == Items.BOW) {
                ItemStack bowInHand = skeleton.getItemBySlot(EquipmentSlot.MAINHAND);
                int levelOnBow = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.RAPID_FIRE.get(), bowInHand);

                int rapidFireLevel = 1;

                if (multiplier >= 1.5) {
                    rapidFireLevel = 2;
                }

                if (levelOnBow < rapidFireLevel) {
                    Map<Enchantment, Integer> enchants = new HashMap<>();
                    enchants.put(ModEnchantments.RAPID_FIRE.get(), rapidFireLevel);
                    EnchantmentHelper.setEnchantments(enchants, bowInHand);
                    //System.out.println("Enchanted skeleton's bow with RapidFire level " + rapidFireLevel);
                }
            } else {
                //System.out.println("Skeleton is holding something else: " + skeleton.getItemBySlot(EquipmentSlot.MAINHAND));
            }
        }
    }

    public static void skeletonBoneSplinterAbility(Skeleton skeleton, double multiplier, float damage) {
        double radius = 1.0 + (2 * multiplier);
        Level level = skeleton.level();
        RandomSource random = level.getRandom();
        List<Player> players = level.getEntitiesOfClass(Player.class, skeleton.getBoundingBox().inflate(radius));
        for (Player player : players) {
            player.hurt(level.damageSources().mobAttack(skeleton), damage / 4);
        }

        float pitch = 1.0F + (random.nextFloat() - 0.5F) * 0.2F;
        level.playSound(
                skeleton,
                skeleton.getOnPos(),
                SoundEvents.TURTLE_EGG_BREAK,
                SoundSource.HOSTILE,
                1.0f,
                pitch);

        for (int i = 0; i < 120; i += 1) {
            double theta = random.nextDouble() * Math.PI;
            double phi = random.nextDouble() * 2 * Math.PI;
            double speed = (radius / 10) + random.nextDouble() * (radius / 10);
            double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
            double ySpeed = speed * Math.cos(theta);
            double zSpeed = speed * Math.sin(theta) * Math.sin(phi);
            double offX = (random.nextDouble() - 0.5) * 0.2;
            double offY = skeleton.getBbHeight() / 2;
            double offZ = (random.nextDouble() - 0.5) * 0.2;

            CoreNetworking.sendToNear(new SendParticlesS2C(
                    new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BONE)),
                    skeleton.getX() + offX,
                    skeleton.getY() + offY,
                    skeleton.getZ() + offZ,
                    xSpeed, ySpeed, zSpeed), skeleton);
        }
    }

    public static void skeletonDecoyAbility(Skeleton skeleton, float damage) {
        Level level = skeleton.level();
        RandomSource random = level.getRandom();
        final String DECOY_TRIGGERED_KEY = "decoyTriggered";

        CompoundTag nbt = skeleton.getPersistentData();
        if (nbt.getBoolean(DECOY_TRIGGERED_KEY)) return;

        float maxHealth = skeleton.getMaxHealth();
        float postDamageHealth = skeleton.getHealth() - damage;
        if (postDamageHealth <= maxHealth / 2) {
            skeleton.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60));
            skeleton.hurtDuration = 60;
            skeleton.hurtTime = 60;
            skeleton.invulnerableTime = 60;

            float pitch = 1.0F + (random.nextFloat() - 0.5F) * 0.2F;
            level.playSound(
                    skeleton,
                    skeleton.getOnPos(),
                    SoundEvents.ENDER_EYE_LAUNCH,
                    SoundSource.HOSTILE,
                    1.0f,
                    pitch);

            for (int i = 0; i < 20; i += 1) {
                double theta = random.nextDouble() * Math.PI;
                double phi = random.nextDouble() * 2 * Math.PI;
                double speed = 0.05 + random.nextDouble() * 0.05;
                double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
                double zSpeed = speed * Math.sin(theta) * Math.sin(phi);
                double offX = (random.nextDouble() - 0.5) * 0.2;
                double offY = skeleton.getBbHeight() / 2;
                double offZ = (random.nextDouble() - 0.5) * 0.2;

                CoreNetworking.sendToNear(new SendParticlesS2C(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        skeleton.getX() + offX,
                        skeleton.getY() + offY,
                        skeleton.getZ() + offZ,
                        xSpeed, 0, zSpeed), skeleton);
            }

            Skeleton decoy = EntityType.SKELETON.create(level);
            if (decoy != null) {
                decoy.moveTo(skeleton.getX(), skeleton.getY(), skeleton.getZ(), skeleton.getYRot(), skeleton.getXRot());
                decoy.setItemSlot(EquipmentSlot.MAINHAND, skeleton.getItemBySlot(EquipmentSlot.MAINHAND).copy());
                CompoundTag decoyNbt = decoy.getPersistentData();
                decoyNbt.putBoolean(DECOY_TRIGGERED_KEY, true);
                decoy.setNoAi(true);
                decoy.setHealth(2);
                level.addFreshEntity(decoy);
                decoy.hurtDuration = 200;
                decoy.hurtTime = 200;
                decoy.invulnerableTime = 200;
            }

            nbt.putBoolean(DECOY_TRIGGERED_KEY, true);
            List<Player> players = level.getEntitiesOfClass(Player.class, skeleton.getBoundingBox().inflate(16));
            if (players.isEmpty()) return;
            Player nearest = players.get(0);
            for (Player p : players) {
                if (p.distanceToSqr(skeleton) < nearest.distanceToSqr(skeleton)) {
                    nearest = p;
                }
            }

            double dx = skeleton.getX() - nearest.getX();
            double dz = skeleton.getZ() - nearest.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance == 0) {
                dx = 1;
                dz = 1;
                distance = Math.sqrt(2);
            }

            double fleeDistance = 16.0;
            double targetX = skeleton.getX() + (dx / distance) * fleeDistance;
            double targetZ = skeleton.getZ() + (dz / distance) * fleeDistance;
            double targetY = skeleton.getY();

            BlockPos targetPos = new BlockPos((int) targetX, (int) targetY, (int) targetZ);
            if (isSafeSpot(level, targetPos)) {
                skeleton.teleportTo(targetX + 0.5, targetY, targetZ + 0.5);
                return;
            }

            for (int i = 0; i < 16; i++) {
                double offsetX = targetX + level.getRandom().nextDouble() * 2 - 1;
                double offsetZ = targetZ + level.getRandom().nextDouble() * 2 - 1;
                BlockPos tryPos = new BlockPos((int) offsetX, (int) targetY, (int) offsetZ);
                if (isSafeSpot(level, tryPos)) {
                    skeleton.teleportTo(offsetX + 0.5, targetY, offsetZ + 0.5);
                    return;
                }
            }
        }
    }

    public static void handleSpiderCooldowns(Spider spider) {
        CompoundTag nbt = spider.getPersistentData();

        if (!nbt.contains("cooldown")) {
            nbt.putInt("cooldown", 0);
        } else {
            int cooldown = nbt.getInt("cooldown");
            if (cooldown > 0) {
                nbt.putInt("cooldown", cooldown - 1);
            }
        }
    }

    public static void spiderSwarmAbility(Spider spider, double multiplier) {
        Level level = spider.level();
        RandomSource random = level.getRandom();
        if (spider.getTarget() != null && random.nextFloat() < 0.02f) {
            System.out.println("Spawning egg sack!");
            //SwarmEggEntity eggSack = new SwarmEggEntity(level);
            //eggSack.moveTo(spider.getX(), spider.getY(), spider.getZ(), spider.getYRot(), spider.getXRot());
            //level.addFreshEntity(eggSack);
        }
    }

    public static void spiderCloakAbility(Spider spider) {
        CompoundTag nbt = spider.getPersistentData();
        if (nbt.contains("cooldown") && nbt.getInt("cooldown") <= 0) {
            Level level = spider.level();
            RandomSource random = level.getRandom();
            List<Player> players = level.getEntitiesOfClass(Player.class, spider.getBoundingBox().inflate(16));
            boolean anyPlayerLooking = false;
            for (Player player : players) {
                if (isPlayerLookingAt(spider, player)) {
                    anyPlayerLooking = true;
                    break;
                }
            }

            if (!anyPlayerLooking) {
                if (!spider.hasEffect(MobEffects.INVISIBILITY) || spider.getEffect(MobEffects.INVISIBILITY).getDuration() <= 20) {
                    spider.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 72000, 0, false, false));

                    for (int i = 0; i < 10; i += 1) {
                        double theta = random.nextDouble() * Math.PI;
                        double phi = random.nextDouble() * 2 * Math.PI;
                        double speed = 0.01 + random.nextDouble() * 0.01;
                        double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
                        double zSpeed = speed * Math.sin(theta) * Math.sin(phi);
                        double offX = (random.nextDouble() - 0.5) * 0.2;
                        double offY = spider.getBbHeight() / 2;
                        double offZ = (random.nextDouble() - 0.5) * 0.2;

                        CoreNetworking.sendToNear(new SendParticlesS2C(
                                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                spider.getX() + offX,
                                spider.getY() + offY,
                                spider.getZ() + offZ,
                                xSpeed, 0, zSpeed), spider);
                    }
                }
                nbt.putInt("cooldown", 300);
            }
        }
    }

    public static float spiderCloakDamage(double multiplier, float damage) {
        return (float) (damage * (1.5 + (multiplier)));
    }

    private static boolean isSafeSpot(Level level, BlockPos pos) {
        return level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above());
    }

    private static boolean isPlayerLookingAt(LivingEntity livingEntity, Player player) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 toMob = new Vec3(livingEntity.getX() - player.getX(), livingEntity.getEyeY() - player.getEyeY(), livingEntity.getZ() - player.getZ());
        toMob = toMob.normalize();
        double dot = lookVec.dot(toMob);
        return dot > 0.45;
    }
}
