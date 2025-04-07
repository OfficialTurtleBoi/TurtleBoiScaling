package net.turtleboi.scaling;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.turtleboi.scaling.commands.InviteCommand;
import net.turtleboi.scaling.effect.ModEffects;
import net.turtleboi.scaling.enchantment.ModEnchantments;
import net.turtleboi.scaling.entity.ModEntities;
import net.turtleboi.scaling.item.ModItems;
import net.turtleboi.scaling.network.ModNetworking;
import net.turtleboi.scaling.particle.ModParticles;
import net.turtleboi.scaling.potion.ModPotions;
import org.slf4j.Logger;

@Mod(Scaling.MOD_ID)
public class Scaling {
    public static final String MOD_ID = "scaling";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Scaling() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);

        ModEntities.register(eventBus);
        ModEffects.register(eventBus);
        ModPotions.register(eventBus);

        ModEnchantments.register(eventBus);
        ModParticles.register(eventBus);

        eventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworking.register();

        //BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(Potions.THICK,
        //        Items.SUGAR_CANE, ModPotions.LESSER_ENERGY_POTION.get()));
        //BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(ModPotions.LESSER_ENERGY_POTION.get(),
        //        Items.GLOWSTONE_DUST, ModPotions.ENERGY_POTION.get()));
        //BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(ModPotions.ENERGY_POTION.get(),
        //        Items.GLOWSTONE_DUST, ModPotions.GREATER_ENERGY_POTION.get()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        InviteCommand.register(event.getServer().getCommands().getDispatcher());
    }
}