package io.github.qwbarch.entity.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.badlogic.gdx.audio.Sound;

/**
 * An entity that plays a sound on collision impact, currently for "Collidable" entities only.
 */
@PooledWeaver
public final class ImpactSound extends Component {
    /**
     * The sound to play when the
     */
    public Sound sound;

    /**
     * A timestamp of when the sound was last played.
     */
    public float lastPlayedTime;
}
