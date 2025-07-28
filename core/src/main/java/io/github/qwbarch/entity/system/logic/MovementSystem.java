package io.github.qwbarch.entity.system.logic;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.One;
import io.github.qwbarch.entity.component.LinearVelocity;
import io.github.qwbarch.entity.component.Position;
import io.github.qwbarch.entity.system.LogicSystem;

import javax.inject.Inject;
import javax.inject.Named;

@All(Position.class)
@One(LinearVelocity.class)
public class MovementSystem extends LogicSystem {
    private ComponentMapper<Position> positions;
    private ComponentMapper<LinearVelocity> velocities;

    @Inject
    MovementSystem(@Named("secondsPerTick") float secondsPerTick) {
        super(secondsPerTick);
    }

    @Override
    protected void process(int entityId) {
        var position = positions.get(entityId);
        position.previous.set(position.current);

        if (velocities.has(entityId)) {
            var linearVelocity = velocities.get(entityId);
            position.current.x += linearVelocity.x * getSecondsPerTick();
            position.current.y += linearVelocity.y * getSecondsPerTick();
        }
    }
}
