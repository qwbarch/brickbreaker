package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * An entity that can be despawned.
 */
@PooledWeaver
public final class Despawnable extends Component {
    /**
     * A function that runs when the entity is attempting to despawn.
     */
    public Runnable onDespawn;
}
