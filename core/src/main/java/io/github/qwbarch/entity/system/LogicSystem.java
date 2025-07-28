package io.github.qwbarch.entity.system;

import com.artemis.systems.IteratingSystem;

/**
 * System that runs at a fixed interval of secondsPerTick, meant for game "physics."
 * Defaults to acting as an IteratingSystem if TimestepInvocationStrategy is not registered to the world.
 */
public abstract class LogicSystem extends IteratingSystem {
    private final float secondsPerTick;

    protected LogicSystem(float secondsPerTick) {
        this.secondsPerTick = secondsPerTick;
    }

    protected float getSecondsPerTick() {
        return secondsPerTick;
    }
}
