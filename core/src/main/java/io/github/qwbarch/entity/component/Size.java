package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * An entity with a width and a height.
 */
@PooledWeaver
public final class Size extends Component {
    public float width = 0f;
    public float height = 0f;

    public Size set(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }
}
