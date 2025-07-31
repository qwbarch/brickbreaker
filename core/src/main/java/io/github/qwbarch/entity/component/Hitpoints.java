package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * An entity with its own hit points. If it reaches 0, it is destroyed.
 */
@PooledWeaver
public final class Hitpoints extends Component {
    /**
     * The amount of hit points the entity has.
     */
    public int value;

    /**
     * The timestamp of when the entity was last hit.
     */
    public float lastHitTime;
}
