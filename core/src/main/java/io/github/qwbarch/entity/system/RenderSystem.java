package io.github.qwbarch.entity.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.component.Sprite;

import javax.inject.Inject;

/**
 * System that takes in an alpha value for interpolating between steps, meant for rendering graphics.
 * Defaults to acting as an IteratingSystem if TimestepInvocationStrategy is not registered to the world.
 */
@ScreenScope
@All({Position.class, Sprite.class})
public final class RenderSystem extends IteratingSystem {
    public float alpha = 1f;

    private final SpriteBatch batch;

    private ComponentMapper<Sprite> sprites;
    private ComponentMapper<Position> positions;

    @Inject
    RenderSystem(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    protected void process(int entityId) {
        var texture = sprites.get(entityId).texture;
        var position = positions.get(entityId);

        // TODO: INTERPOLATE USING ALPHA.
        if (texture != null) {
            batch.draw(texture, position.current.x, position.current.y, 1f, 1f);
        }
    }
}
