package net.turtleboi.scaling.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.scaling.entity.EggSackEntity;
import net.turtleboi.scaling.entity.client.models.EggSackModel;
import net.turtleboi.turtlecore.entity.abilities.SanctuaryDomeEntity;
import net.turtleboi.turtlecore.entity.client.SanctuaryDomeModel;

public class EggSackRenderer extends EntityRenderer<EggSackEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Scaling.MOD_ID, "textures/entity/eggsack.png");
    private final EggSackModel<EggSackEntity> model;
    protected EggSackRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new EggSackModel<>(pContext.bakeLayer(EggSackModel.EGGSACK_LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(EggSackEntity pEntity) {
        return TEXTURE;
    }
}
