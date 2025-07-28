package io.github.qwbarch.entity.system.logic;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.system.LogicSystem;

import javax.inject.Inject;
import javax.inject.Named;

@ScreenScope
@All({Position.class, LinearVelocity.class})
public final class MovementSystem extends LogicSystem {
    private final float secondsPerTick;

    private ComponentMapper<Position> positions;
    private ComponentMapper<LinearVelocity> velocities;

    @Inject
    MovementSystem(@Named("secondsPerTick") float secondsPerTick) {
        this.secondsPerTick = secondsPerTick;
    }

    @Override
    protected void process(int entityId) {
        var position = positions.get(entityId);
        var velocity = velocities.get(entityId);
        position.previous.set(position.current);
        position.current.x += velocity.x * secondsPerTick;
        position.current.y += velocity.y * secondsPerTick;
    }
}
