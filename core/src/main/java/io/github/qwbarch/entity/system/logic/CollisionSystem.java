package io.github.qwbarch.entity.system.logic;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.component.Size;
import io.github.qwbarch.entity.system.LogicSystem;

import javax.inject.Inject;
import javax.inject.Named;

@ScreenScope
@All({Position.class, LinearVelocity.class, Size.class})
public final class CollisionSystem extends LogicSystem {
    private final float worldWidth;
    private final float worldHeight;

    private ComponentMapper<Position> positions;
    private ComponentMapper<Size> sizes;
    private ComponentMapper<LinearVelocity> velocities;

    @Inject
    CollisionSystem(
        @Named("worldWidth") float worldWidth,
        @Named("worldHeight") float worldHeight
    ) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    protected void process(int entityId) {
        var position = positions.get(entityId);
        var size = sizes.get(entityId);
        var velocity = velocities.get(entityId);

        var x = position.current.x;
        var y = position.current.y;

        // If past the left world bounds.
        if (x < 0f) {
            position.current.x = 0f;
            System.out.println("reverseX 1");
            velocity.reverseX();
        }
        // If past the right world bounds.
        else if (x + size.width > worldWidth) {
            position.current.x = worldWidth - size.width;
            velocity.reverseX();
            System.out.println("reverseX 2");
        }

        // If past the bottom world bounds.
        if (y < 0f) {
            position.current.y = 0f;
            velocity.reverseY();
        }
        // If past the top world bounds.
        else if (y + size.height > worldHeight) {
            position.current.y = worldHeight - size.height;
            velocity.reverseY();
        }
    }
}
