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

@ScreenScope
@All(Player.class)
public final class PlayerSystem extends IteratingSystem {
    private static final float PADDLE_VELOCITY = 500;

    private ComponentMapper<LinearVelocity> velocities;

    @Inject
    PlayerSystem() {
    }

    @Override
    protected void process(int entityId) {
        if (entityId < 0) return;

        var velocity = velocities.get(entityId);
        var isLeftHeld = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        var isRightHeld = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (isLeftHeld == isRightHeld) {
            // If both left/right are held or neither are held, stop moving the paddle.
            velocity.x = 0;
        } else {
            velocity.x = isLeftHeld ? -PADDLE_VELOCITY : PADDLE_VELOCITY;
        }
    }
}
