package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * An entity that can be collided into.
 * If a "Collider" entity collides into a "Collidable" entity, its position will be
 * fixed and moved back outside of the "Collidable" entity.
 * <br />
 * For example if a bullet collides into a wall, it will be moved back to the outside of the wall.
 */
@PooledWeaver
public final class Collidable extends Component { }
