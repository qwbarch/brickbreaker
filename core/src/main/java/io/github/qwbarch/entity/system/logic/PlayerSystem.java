package io.github.qwbarch.entity.system.logic;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Player;

import javax.inject.Inject;

import static io.github.qwbarch.entity.system.logic.MovementCollisionSystem.PASSES;

/**
 * Handles the player input of entities. This is currently only for the player's paddle.
 */
@ScreenScope
@All({Player.class, LinearVelocity.class})
public final class PlayerSystem extends IteratingSystem {
    /**
     * The speed of the paddle.
     *
     * TODO: This shouldn't need to rely on PASSES, but right now MovementCollisionSystem
     * is slightly bugged where I believe movement is only applied on the first pass.
     * This causes the paddle to move slower, so as a workaround for now I simply multiply
     * it by the # of passes.
     */
    private static final float PADDLE_VELOCITY = 120 * PASSES;

    // Component mappers are automatically injected via artemis-odb,
    // which gives access to the entity's components.
    private ComponentMapper<LinearVelocity> velocities;

    @Inject
    PlayerSystem() {
    }

    /**
     * Called once per game tick on every entity that matches the specified aspects above
     * (from the @All, @One, etc. annotations).
     */
    @Override
    protected void process(int entityId) {
        var velocity = velocities.get(entityId);
        var isLeftHeld = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        var isRightHeld = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        // If both left/right are held or neither are held, stop moving the paddle.
        if (isLeftHeld == isRightHeld) {
            velocity.x = 0;
        }
        // Otherwise move the paddle left/right.
        else {
            velocity.x = isLeftHeld ? -PADDLE_VELOCITY : PADDLE_VELOCITY;
        }
    }
}
