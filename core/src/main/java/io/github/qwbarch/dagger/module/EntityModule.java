package io.github.qwbarch.dagger.module;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import dagger.Module;
import dagger.Provides;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.entity.strategy.FixedTimestepInvocationStrategy;
import io.github.qwbarch.entity.system.*;

@Module
public final class EntityModule {
    /**
     * Provider method for the entity component system world with all its systems enabled.
     * A new instance is created for every injection site (e.g. new world per screen).
     */
    @Provides
    @ScreenScope
    public World provideWorld(
        FixedTimestepInvocationStrategy strategy,
        RenderSystem renderSystem,
        MovementCollisionSystem movementCollisionSystem,
        InputSystem inputSystem,
        DespawnSystem despawnSystem,
        PlayerHealthSystem playerHealthSystem
    ) {
        System.out.println("provideWorld");
        return new World(
            new WorldConfigurationBuilder()
                .with(inputSystem)
                .with(movementCollisionSystem)
                .with(despawnSystem)
                .with(playerHealthSystem)
                .with(renderSystem)
                .register(strategy)
                .build()
        );
    }
}
