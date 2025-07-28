package io.github.qwbarch.entity.system;

import com.artemis.systems.IteratingSystem;

/**
 * System that takes in an alpha value for interpolating between steps, meant for rendering graphics.
 * Defaults to acting as an IteratingSystem if TimestepInvocationStrategy is not registered to the world.
 */
public abstract class RenderSystem extends IteratingSystem {
    public float alpha = 1f;
}
