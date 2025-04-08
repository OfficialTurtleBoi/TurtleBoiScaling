package net.turtleboi.scaling.event;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.scaling.entity.EggSackEntity;
import net.turtleboi.scaling.entity.ModEntities;
import net.turtleboi.scaling.entity.client.models.EggSackModel;
import net.turtleboi.scaling.entity.client.renderer.EggSackRenderer;
import net.turtleboi.turtlecore.util.CoreKeyBinding;

@Mod.EventBusSubscriber(modid = Scaling.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModBusClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.EGGSACK.get(), EggSackRenderer::new);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.EGGSACK.get(), EggSackEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EggSackModel.EGGSACK_LAYER, EggSackModel::createBodyLayer);
    }

    @SubscribeEvent
    public  static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(CoreKeyBinding.LOCK_ON);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {

    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event){

    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event){

    }
}

