package ovh.astarivi.mobs.entity.generic;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;


public abstract class GenericAnimal extends Animal implements NeutralMob, GeoEntity, EntityResourceProvider {
    protected GenericAnimal(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }
}
