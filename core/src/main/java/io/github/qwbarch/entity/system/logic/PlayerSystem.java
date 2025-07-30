package io.github.qwbarch.entity.system.logic;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.Collider;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Player;

import javax.inject.Inject;
import javax.inject.Named;

import static io.github.qwbarch.entity.system.logic.MovementCollisionSystem.PASSES;

/**
 * Handles the player input of entities.
 */
@ScreenScope
@All({Player.class, LinearVelocity.class})
public final class PlayerSystem extends IteratingSystem {
    private final float paddleVelocity;

    // Component mappers are automatically injected via artemis-odb,
    // which gives access to the entity's components.
    private ComponentMapper<LinearVelocity> velocities;
    private ComponentMapper<Collider> colliders;

    @Inject
    PlayerSystem(@Named("paddleVelocity") float paddleVelocity) {
        this.paddleVelocity = paddleVelocity;
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
            // Make it faster by "PASSES" if the entity is a collider.
            // This is due to a bug in MovementCollisionSystem that makes collider entities
            // move slower by a factor of "PASSES".
            var multiplier = colliders.has(entityId) ? (float) PASSES : 1f;
            velocity.x = (isLeftHeld ? -paddleVelocity : paddleVelocity) * multiplier;
        }
    }
}
