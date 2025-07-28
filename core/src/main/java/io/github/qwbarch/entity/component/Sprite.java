package io.github.qwbarch.entity.component;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.Texture;

/**
 * A component with a drawable sprite. The held texture can be null.
 */
public final class Sprite extends PooledComponent {
    public Texture texture;

    @Override
    protected void reset() {
        if (texture != null) {
            texture = null;
        }
    }
}
