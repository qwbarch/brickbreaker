package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * An entity with this component is marked as an objective target.
 * All "target" entities need to be destroyed for the player to win the game.
 */
@PooledWeaver
public final class Target extends Component { }
