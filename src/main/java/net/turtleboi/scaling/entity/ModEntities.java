package net.turtleboi.scaling.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.scaling.Scaling;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Scaling.MOD_ID);

    public static final RegistryObject<EntityType<EggSackEntity>> EGGSACK =
            ENTITY_TYPES.register("eggsack",()-> EntityType.Builder.of(EggSackEntity::new, MobCategory.MISC)
                    .sized(0.5F, .6F).build("eggsack"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
