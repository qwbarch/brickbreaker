package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.InputProcessor;

@PooledWeaver
public final class InputListener extends Component {
    public InputProcessor processor;

    /**
     * If true, the input processor will be unregistered from the system, and this component is removed from the entity.
     * If false, the input processor will continue running as normal.
     */
    public boolean unregister;
}
