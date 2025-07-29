package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

/**
 * A component representing an entity's linear velocity amongst the x and y axis.
 */
@PooledWeaver
public final class LinearVelocity extends Component {
    public float x = 0f;
    public float y = 0f;

    /**
     * Set the linear velocity amongst the x and y axis. Returns itself to allowed chained operations.
     */
    public LinearVelocity set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public LinearVelocity setZero() {
        return set(0f, 0f);
    }

    public LinearVelocity reverseX() {
        x = -x;
        return this;
    }

    public LinearVelocity reverseY() {
        y = -y;
        return this;
    }
}
