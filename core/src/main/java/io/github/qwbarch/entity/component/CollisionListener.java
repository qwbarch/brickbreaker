package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * Allows an entity to react to arbitrary collisions.
 */
@PooledWeaver
public final class CollisionListener extends Component {
    @FunctionalInterface
    public interface CollisionListenerFunction {
        /**
         * Runs when a collider entity collides into this entity.
         * @param colliderId The entity that collided into this entity.
         * @param entityId This entity.
         */
        void collide(int colliderId, int entityId);
    }

    /**
     * The function to run when the entity collides with another entity.
     */
    public CollisionListenerFunction listener;

    /**
     * Timestamp of when the entity last collided with another entity.
     */
    public float lastCollisionTime;
}
