package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.audio.Sound;

@PooledWeaver
public final class ImpactSound extends Component {
    public Sound sound;
    public float lastPlayedTime;
}
