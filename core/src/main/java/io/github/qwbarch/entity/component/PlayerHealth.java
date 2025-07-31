package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * An entity that counts towards the player's alive status.
 * When all entities with this component is destroyed, the player loses the game.
 */
@PooledWeaver
public final class PlayerHealth extends Component { }
