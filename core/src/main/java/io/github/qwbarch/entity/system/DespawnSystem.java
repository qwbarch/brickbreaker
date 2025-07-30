package io.github.qwbarch.entity.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import io.github.qwbarch.entity.component.Despawnable;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.component.Size;

import javax.inject.Inject;

@All({Position.class, Size.class, Despawnable.class})
public class DespawnSystem extends LogicSystem {
    private ComponentMapper<Position> positions;
    private ComponentMapper<Size> sizes;
    private ComponentMapper<Despawnable> despawnables;

    @Inject
    DespawnSystem() {
        System.out.println("DespawnSystem constructor");
    }

    @Override
    protected void process(int entityId) {
        var position = positions.get(entityId);
        var size = sizes.get(entityId);
        var despawnable = despawnables.get(entityId);

        // If the entity is below the world (y = 0).
        if (position.current.y < -size.height) {
            if (despawnable.onDespawn != null) {
                despawnable.onDespawn.run();
                despawnable.onDespawn = null;
            }
            System.out.println("despanwed");
            world.delete(entityId);
        }
    }
}
