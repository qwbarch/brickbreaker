package io.github.qwbarch.entity.system.logic;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.One;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.*;
import io.github.qwbarch.entity.system.LogicSystem;

import javax.inject.Inject;
import javax.inject.Named;

@ScreenScope
@All({Position.class, LinearVelocity.class, Size.class})
@One({WorldBounded.class, Player.class, BounceCollider.class})
public final class CollisionSystem extends LogicSystem {
    private final float worldWidth;
    private final float worldHeight;

    private ComponentMapper<Position> positions;
    private ComponentMapper<Size> sizes;
    private ComponentMapper<LinearVelocity> velocities;
    private ComponentMapper<WorldBounded> worldBounded;
    private ComponentMapper<Player> isPlayer;
    private ComponentMapper<BounceCollider> bounceCollider;

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
        processWorldBounded(entityId);
    }

    private void processWorldBounded(int entityId) {
        if (worldBounded.has(entityId)) {
            var position = positions.get(entityId);
            var size = sizes.get(entityId);
            var velocity = velocities.get(entityId);
            var bounce = bounceCollider.has(entityId);

            var x = position.current.x;
            var y = position.current.y;

            // If past the left world bounds.
            if (x < 0f) {
                position.current.x = 0f;
                if (bounce) velocity.reverseX();
            }
            // If past the right world bounds.
            else if (x + size.width > worldWidth) {
                position.current.x = worldWidth - size.width;
                if (bounce) velocity.reverseX();
            }

            // If past the bottom world bounds.
            if (y < 0f) {
                position.current.y = 0f;
                if (bounce) velocity.reverseY();
            }
            // If past the top world bounds.
            else if (y + size.height > worldHeight) {
                position.current.y = worldHeight - size.height;
                if (bounce) velocity.reverseY();
            }
        }
    }
}
