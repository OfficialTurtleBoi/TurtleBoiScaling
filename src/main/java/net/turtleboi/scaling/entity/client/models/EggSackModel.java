package net.turtleboi.scaling.entity.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.scaling.entity.EggSackEntity;
import net.turtleboi.scaling.entity.client.animations.ModAnimationDefinitions;

public class EggSackModel<T extends Entity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation EGGSACK_LAYER = new ModelLayerLocation(new ResourceLocation(Scaling.MOD_ID, "eggsack"), "main");
    private final ModelPart eggsack;
    private final ModelPart egg;
    private final ModelPart strings;

    public EggSackModel(ModelPart root) {
        this.eggsack = root.getChild("eggsack");
        this.egg = this.eggsack.getChild("egg");
        this.strings = this.eggsack.getChild("strings");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition eggsack = partdefinition.addOrReplaceChild("eggsack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition egg = eggsack.addOrReplaceChild("egg", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition strings = eggsack.addOrReplaceChild("strings", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition string9_r1 = strings.addOrReplaceChild("string9_r1", CubeListBuilder.create().texOffs(8, 18).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -2.5F, 3.25F, 1.1345F, 1.1345F, 1.5708F));

        PartDefinition string8_r1 = strings.addOrReplaceChild("string8_r1", CubeListBuilder.create().texOffs(8, 21).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.75F, -5.75F, -3.5F, 0.0F, -1.1345F, 1.5708F));

        PartDefinition string7_r1 = strings.addOrReplaceChild("string7_r1", CubeListBuilder.create().texOffs(0, 24).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.75F, -2.5F, -3.5F, 1.8653F, -0.8118F, -0.7387F));

        PartDefinition string6_r1 = strings.addOrReplaceChild("string6_r1", CubeListBuilder.create().texOffs(16, 24).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, -9.45F, 0.0F, 3.0963F, 0.478F, 1.0361F));

        PartDefinition string5_r1 = strings.addOrReplaceChild("string5_r1", CubeListBuilder.create().texOffs(8, 24).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.75F, -3.25F, 1.5F, -1.5541F, -0.0403F, -2.4002F));

        PartDefinition string4_r1 = strings.addOrReplaceChild("string4_r1", CubeListBuilder.create().texOffs(16, 21).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.0F, -7.0F, -2.0F, -2.7415F, -0.1828F, 2.5897F));

        PartDefinition string3_r1 = strings.addOrReplaceChild("string3_r1", CubeListBuilder.create().texOffs(16, 18).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.25F, -5.5F, 2.0F, 0.48F, 0.0F, 0.4363F));

        PartDefinition string2_r1 = strings.addOrReplaceChild("string2_r1", CubeListBuilder.create().texOffs(0, 21).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.25F, -2.5F, -1.75F, -0.4363F, 0.0F, 0.4363F));

        PartDefinition string1_r1 = strings.addOrReplaceChild("string1_r1", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(-4.0F, -1.5F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -6.5F, 3.75F, 2.0508F, 1.1345F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        EggSackEntity eggSack = (EggSackEntity) entity;
        if (eggSack.getAge() < eggSack.getMaxAge() - 160) {
            this.animate(eggSack.idleAnimationState, ModAnimationDefinitions.EGGSACK_IDLE, ageInTicks, 1f);
        } else {
            this.animate(eggSack.idleAnimationState, ModAnimationDefinitions.EGGSACK_SHAKING_1, ageInTicks, 1f);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        eggsack.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return eggsack;
    }
}
