package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * An entity that can collide into "Collidable" entities.
 * <br />
 * If a "Collider" entity collides into a "Collidable" entity, its position will be
 * fixed and moved back outside of the "Collidable" entity.
 * <br />
 * For example if a bullet collides into a wall, it will be moved back to the outside of the wall.
 */
@PooledWeaver
public final class Collider extends Component {
    /**
     * If true, the entity is ghosted and can move through entities. Collision listeners will still trigger.
     * If false, the entity cannot move through entities.
     */
    public boolean ghosted;

    /**
     * If true, the entity will bounce (reverse its velocity) if it collides into a "Collidable" entity.
     * If false, the entity's velocity will be set to 0.
     */
    public boolean bounce;

    /**
     * If true, will player the collidable entity's impact sound.
     * If false, will not play any sounds on impact.
     */
    public boolean playImpactSound;
}
