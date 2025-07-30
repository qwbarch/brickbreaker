package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.InputProcessor;

@PooledWeaver
public final class InputListener extends Component {
    public InputProcessor processor;
}
