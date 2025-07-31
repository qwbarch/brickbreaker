package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.InputProcessor;

/**
 * An entity that is able to react to user inputs.
 */
@PooledWeaver
public final class InputListener extends Component {
    public InputProcessor processor;
}
