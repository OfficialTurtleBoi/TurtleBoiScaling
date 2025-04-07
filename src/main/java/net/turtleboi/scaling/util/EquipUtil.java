package net.turtleboi.scaling.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EquipUtil {
    public static void equipMobWithGear(Mob mob, double multiplier, RandomSource random) {
        equipArmorForSlot(mob, EquipmentSlot.HEAD, multiplier, random);
        equipArmorForSlot(mob, EquipmentSlot.CHEST, multiplier, random);
        equipArmorForSlot(mob, EquipmentSlot.LEGS, multiplier, random);
        equipArmorForSlot(mob, EquipmentSlot.FEET, multiplier, random);

        if (!(mob instanceof Skeleton) && mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            equipWeapon(mob, multiplier, random);
        }

        if (multiplier >= 1.5) {
            enchantGear(mob, multiplier, random);
        }
    }

    private static void equipArmorForSlot(Mob mob, EquipmentSlot slot, double multiplier, RandomSource random) {
        if (!mob.getItemBySlot(slot).isEmpty()) {
            return;
        }
        ItemStack armorPiece = getArmorForSlot(slot, multiplier, random);
        if (armorPiece != null) {
            mob.setItemSlot(slot, armorPiece);
        }
    }

    private static void equipWeapon(Mob mob, double multiplier, RandomSource random) {
        if (!mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) return;

        if (random.nextDouble() >= Math.min(1.0, 0.33 * multiplier)) {
            return;
        }

        ItemStack weapon = getWeaponForMultiplier(multiplier, random);
        if (!weapon.isEmpty()) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        }
    }

    private static ItemStack getArmorForSlot(EquipmentSlot slot, double multiplier, RandomSource random) {
        if (multiplier < 1.1) return ItemStack.EMPTY;

        double leatherWeight = 0.0;
        double chainmailWeight = 0.0;
        double ironWeight = 0.0;
        double diamondWeight = 0.0;
        double netheriteWeight = 0.0;

        if (multiplier >= 1.1 && multiplier < 1.3) {
            leatherWeight = 1.0;
        } else if (multiplier >= 1.3 && multiplier < 1.5) {
            leatherWeight = 1.0 - (multiplier - 1.3) / 0.2;
        }

        if (multiplier >= 1.3 && multiplier < 1.5) {
            chainmailWeight = 1.0;
        } else if (multiplier >= 1.5 && multiplier < 1.7) {
            chainmailWeight = 1.0 - (multiplier - 1.5) / 0.2;
        }

        if (multiplier >= 1.5 && multiplier < 1.7) {
            ironWeight = 1.0;
        } else if (multiplier >= 1.7 && multiplier < 1.9) {
            ironWeight = 1.0 - (multiplier - 1.7) / 0.2;
        }

        if (multiplier >= 1.7 && multiplier < 1.9) {
            diamondWeight = (multiplier - 1.7) / 0.2;
        } else if (multiplier >= 1.9 && multiplier < 5) {
            diamondWeight = 1.0 - (multiplier - 1.9) / (5 - 1.9);
            if (diamondWeight < 0) diamondWeight = 0;
        }

        if (multiplier >= 1.9 && multiplier < 5) {
            netheriteWeight = (multiplier - 1.9) / (5 - 1.9);
        } else if (multiplier >= 5) {
            netheriteWeight = 1.0;
        }

        double totalWeight = leatherWeight + chainmailWeight + ironWeight + diamondWeight + netheriteWeight;
        double choice = random.nextDouble() * totalWeight;

        String tier = "";
        if (choice < leatherWeight) {
            tier = "leather";
        } else {
            choice -= leatherWeight;
            if (choice < chainmailWeight) {
                tier = "chainmail";
            } else {
                choice -= chainmailWeight;
                if (choice < ironWeight) {
                    tier = "iron";
                } else {
                    choice -= ironWeight;
                    if (choice < diamondWeight) {
                        tier = "diamond";
                    } else {
                        choice -= diamondWeight;
                        if (choice < netheriteWeight) {
                            tier = "netherite";
                        }
                    }
                }
            }
        }

        return switch (tier) {
            case "leather" -> getLeatherArmor(slot);
            case "chainmail" ->
                    random.nextBoolean() ? getChainmailArmor(slot) : getGoldenArmor(slot);
            case "iron" -> getIronArmor(slot);
            case "diamond" -> getDiamondArmor(slot);
            case "netherite" -> getNetheriteArmor(slot);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getWeaponForMultiplier(double multiplier, RandomSource random) {
        if (multiplier < 1.1) return ItemStack.EMPTY;

        double woodenWeight = 0.0;
        double stoneWeight = 0.0;
        double ironWeight = 0.0;
        double diamondWeight = 0.0;
        double netheriteWeight = 0.0;

        if (multiplier >= 1.1 && multiplier < 1.3) {
            woodenWeight = 1.0;
        } else if (multiplier >= 1.3 && multiplier < 1.5) {
            woodenWeight = 1.0 - (multiplier - 1.3) / 0.2;
        }

        if (multiplier >= 1.3 && multiplier < 1.5) {
            stoneWeight = 1.0;
        } else if (multiplier >= 1.5 && multiplier < 1.7) {
            stoneWeight = 1.0 - (multiplier - 1.5) / 0.2;
        }

        if (multiplier >= 1.5 && multiplier < 1.7) {
            ironWeight = 1.0;
        } else if (multiplier >= 1.7 && multiplier < 1.9) {
            ironWeight = 1.0 - (multiplier - 1.7) / 0.2;
        }

        if (multiplier >= 1.7 && multiplier < 1.9) {
            diamondWeight = (multiplier - 1.7) / 0.2;
        } else if (multiplier >= 1.9 && multiplier < 5) {
            diamondWeight = 1.0 - (multiplier - 1.9) / (5 - 1.9);
            if (diamondWeight < 0) diamondWeight = 0;
        }

        if (multiplier >= 1.9 && multiplier < 5) {
            netheriteWeight = (multiplier - 1.9) / (5 - 1.9);
        } else if (multiplier >= 5) {
            netheriteWeight = 1.0;
        }

        double totalWeight = woodenWeight + stoneWeight + ironWeight + diamondWeight + netheriteWeight;
        double choice = random.nextDouble() * totalWeight;

        String tier = "";
        if (choice < woodenWeight) {
            tier = "wooden";
        } else {
            choice -= woodenWeight;
            if (choice < stoneWeight) {
                tier = "stone";
            } else {
                choice -= stoneWeight;
                if (choice < ironWeight) {
                    tier = "iron";
                } else {
                    choice -= ironWeight;
                    if (choice < diamondWeight) {
                        tier = "diamond";
                    } else {
                        choice -= diamondWeight;
                        if (choice < netheriteWeight) {
                            tier = "netherite";
                        }
                    }
                }
            }
        }

        boolean useAxe = random.nextBoolean();
        return switch (tier) {
            case "wooden" -> new ItemStack(useAxe ? Items.WOODEN_AXE : Items.WOODEN_SWORD);
            case "stone" -> new ItemStack(useAxe ? Items.STONE_AXE : Items.STONE_SWORD);
            case "iron" -> new ItemStack(useAxe ? Items.IRON_AXE : Items.IRON_SWORD);
            case "diamond" -> new ItemStack(useAxe ? Items.DIAMOND_AXE : Items.DIAMOND_SWORD);
            case "netherite" -> new ItemStack(useAxe ? Items.NETHERITE_AXE : Items.NETHERITE_SWORD);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getLeatherArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD    -> new ItemStack(Items.LEATHER_HELMET);
            case CHEST   -> new ItemStack(Items.LEATHER_CHESTPLATE);
            case LEGS    -> new ItemStack(Items.LEATHER_LEGGINGS);
            case FEET    -> new ItemStack(Items.LEATHER_BOOTS);
            default      -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getChainmailArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD    -> new ItemStack(Items.CHAINMAIL_HELMET);
            case CHEST   -> new ItemStack(Items.CHAINMAIL_CHESTPLATE);
            case LEGS    -> new ItemStack(Items.CHAINMAIL_LEGGINGS);
            case FEET    -> new ItemStack(Items.CHAINMAIL_BOOTS);
            default      -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getGoldenArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD    -> new ItemStack(Items.GOLDEN_HELMET);
            case CHEST   -> new ItemStack(Items.GOLDEN_CHESTPLATE);
            case LEGS    -> new ItemStack(Items.GOLDEN_LEGGINGS);
            case FEET    -> new ItemStack(Items.GOLDEN_BOOTS);
            default      -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getIronArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD    -> new ItemStack(Items.IRON_HELMET);
            case CHEST   -> new ItemStack(Items.IRON_CHESTPLATE);
            case LEGS    -> new ItemStack(Items.IRON_LEGGINGS);
            case FEET    -> new ItemStack(Items.IRON_BOOTS);
            default      -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getDiamondArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD    -> new ItemStack(Items.DIAMOND_HELMET);
            case CHEST   -> new ItemStack(Items.DIAMOND_CHESTPLATE);
            case LEGS    -> new ItemStack(Items.DIAMOND_LEGGINGS);
            case FEET    -> new ItemStack(Items.DIAMOND_BOOTS);
            default      -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getNetheriteArmor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD    -> new ItemStack(Items.NETHERITE_HELMET);
            case CHEST   -> new ItemStack(Items.NETHERITE_CHESTPLATE);
            case LEGS    -> new ItemStack(Items.NETHERITE_LEGGINGS);
            case FEET    -> new ItemStack(Items.NETHERITE_BOOTS);
            default      -> ItemStack.EMPTY;
        };
    }

    private static void enchantGear(Mob mob, double multiplier, RandomSource random) {
        //System.out.println("Enchanting gear for mob: " + mob.getName().getString());
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET,
                EquipmentSlot.MAINHAND
        };

        for (EquipmentSlot slot : slots) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                for (int i = 0; i < multiplier - 1; i++) {
                    int enchantmentLevel = (int) (1 + random.nextInt(5) * (multiplier * 10));
                    EnchantmentHelper.enchantItem(random, stack, enchantmentLevel, true);
                    mob.setItemSlot(slot, stack);
                }
            }
        }
    }
}
