package io.github.qwbarch.entity.system.logic;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.component.Size;
import io.github.qwbarch.entity.component.flag.IsPlayer;

import javax.inject.Inject;

import static io.github.qwbarch.entity.EntitySpawner.PADDLE_TAG;

@ScreenScope
@All(IsPlayer.class)
public final class PlayerSystem extends IteratingSystem  {
    private static final float PADDLE_VELOCITY = 120f;

    private ComponentMapper<LinearVelocity> velocities;

    @Inject
    PlayerSystem() {}

    @Override
    protected void process(int entityId) {
        if (entityId < 0)  return;

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
