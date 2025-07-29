package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.math.Vector2;

/**
 * A component representing an entity's position amongst the x and y axis.
 */
@PooledWeaver
public final class Position extends Component {
    public final Vector2 current = new Vector2();
    public final Vector2 previous = new Vector2();
}
