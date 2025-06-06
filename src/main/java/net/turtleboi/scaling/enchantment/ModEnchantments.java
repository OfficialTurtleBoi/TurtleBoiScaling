package net.turtleboi.scaling.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.scaling.Scaling;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Scaling.MOD_ID);

    public static RegistryObject<Enchantment> RAPID_FIRE =
            ENCHANTMENTS.register("rapidfire",
                    () -> new BarrageEnchantment(Enchantment.Rarity.VERY_RARE,
                            EnchantmentCategory.BOW, EquipmentSlot.MAINHAND));

    public static void register(IEventBus eventBus){
        ENCHANTMENTS.register(eventBus);
    }
}
