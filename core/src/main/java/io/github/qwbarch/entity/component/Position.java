package io.github.qwbarch.entity.component;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * A component representing an entity's position amongst the x and y axis.
 */
public final class Position extends PooledComponent {
    public final Vector2 current = new Vector2();
    public final Vector2 previous = new Vector2();

    @Override
    public void reset() {
        current.setZero();
        previous.setZero();
    }
}
