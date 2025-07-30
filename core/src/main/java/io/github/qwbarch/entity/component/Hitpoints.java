package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public final class Hitpoints extends Component {
    public int value;
    public float lastHitTime;
}
