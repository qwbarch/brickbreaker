package io.github.qwbarch.dagger.module;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import dagger.Module;
import dagger.Provides;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.strategy.FixedTimestepInvocationStrategy;
import io.github.qwbarch.entity.system.RenderSystem;
import io.github.qwbarch.entity.system.logic.CollisionSystem;
import io.github.qwbarch.entity.system.logic.MovementSystem;

@Module
public final class EntityModule {
    /**
     * Provider method for the entity component system world with all its systems enabled.
     * A new instance is created for every injection site (e.g. new world per screen).
     *
     * @param strategy       The fixed timestep invocation strategy to use with the world.
     * @param movementSystem The movement system to use with the world.
     */
    @Provides
    @ScreenScope
    public World provideWorld(
        FixedTimestepInvocationStrategy strategy,
        MovementSystem movementSystem,
        RenderSystem renderSystem,
        CollisionSystem collisionSystem
    ) {
        System.out.println("provideWorld");
        return new World(
            new WorldConfigurationBuilder()
                .with(movementSystem)
                .with(collisionSystem)
                .with(renderSystem)
                .register(strategy)
                .build()
        );
    }
}
