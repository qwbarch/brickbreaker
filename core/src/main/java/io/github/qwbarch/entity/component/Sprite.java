package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.graphics.Texture;

/**
 * A component with a drawable sprite. The held texture can be null.
 */
@PooledWeaver
public final class Sprite extends Component {
    public Texture texture;
}
