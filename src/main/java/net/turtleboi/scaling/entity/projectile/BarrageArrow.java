package net.turtleboi.scaling.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class BarrageArrow extends Arrow {
    public BarrageArrow(Level level, LivingEntity shooter) {
        super(level, shooter);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 200) {
            discard();
        }
    }
}
