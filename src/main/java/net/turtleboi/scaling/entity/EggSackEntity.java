package net.turtleboi.scaling.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EggSackEntity extends Mob {
    private static final EntityDataAccessor<Integer> MAX_AGE = SynchedEntityData.defineId(EggSackEntity.class, EntityDataSerializers.INT);

    public EggSackEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MAX_AGE,0);
    }

    public AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public AnimationState shakingAnimationState = new AnimationState();
    private int shakingAnimationTimeout = 0;

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide){
            setupAnimationStates();
        }
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <= 0){
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if(this.shakingAnimationTimeout <= 0){
            this.shakingAnimationTimeout = 40;
            this.shakingAnimationState.start(this.tickCount);
        } else {
            --this.shakingAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > getMaxAge()) {
            discard();
        }
    }

    public int getMaxAge() {
        return this.entityData.get(MAX_AGE);
    }

    public void setMaxAge(int value) {
        this.entityData.set(MAX_AGE,value);
    }

    public int getAge() {
        return this.tickCount;
    }
}
