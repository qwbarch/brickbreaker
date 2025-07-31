package io.github.qwbarch.entity.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.component.Size;
import io.github.qwbarch.entity.component.Sprite;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * System that takes in an alpha value for interpolating between steps, meant for rendering graphics.
 * Defaults to acting as an IteratingSystem if TimestepInvocationStrategy is not registered to the world.
 * <p />
 * This is a singleton, so one instance is used for the entire game.
 */
@All({Position.class, Size.class, Sprite.class})
@Singleton
public final class RenderSystem extends IteratingSystem {
    /**
     * The current time in seconds in-between the previous and the current state.
     * This is used for interpolating between the two states, since logical updates runs
     * at a fixed number of updates per second, smoothing out the animation for users
     * with higher frame rates.
     */
    public float alpha = 1f;

    // Dependencies injected via dagger.
    private final SpriteBatch batch;

    // Component mappers are automatically injected via artemis-odb,
    // which gives access to the entity's components.
    private ComponentMapper<Sprite> sprites;
    private ComponentMapper<Size> sizes;
    private ComponentMapper<Position> positions;

    // Package-private constructor since dagger injects the dependencies.
    @Inject
    RenderSystem(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    protected void process(int entityId) {
        var texture = sprites.get(entityId).texture;
        var size = sizes.get(entityId);
        var position = positions.get(entityId);

        // This system runs on every available render frame.
        if (texture != null) {
            // Interpolate between the previous and the current state.
            // This requires the previous position to be set by another system.
            var x = position.previous.x + (position.current.x - position.previous.x) * alpha;
            var y = position.previous.y + (position.current.y - position.previous.y) * alpha;
            batch.draw(texture, x, y, size.width, size.height);
        }
    }
}
