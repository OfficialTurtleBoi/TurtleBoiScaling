package net.turtleboi.scaling.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.scaling.entity.EggSackEntity;
import net.turtleboi.scaling.entity.client.models.EggSackModel;
import net.turtleboi.turtlecore.entity.abilities.SanctuaryDomeEntity;
import net.turtleboi.turtlecore.entity.client.SanctuaryDomeModel;

public class EggSackRenderer extends MobRenderer<EggSackEntity, EggSackModel<EggSackEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Scaling.MOD_ID, "textures/entity/eggsack.png");
    public EggSackRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new EggSackModel<>(pContext.bakeLayer(EggSackModel.EGGSACK_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(EggSackEntity pEntity) {
        return TEXTURE;
    }
}
