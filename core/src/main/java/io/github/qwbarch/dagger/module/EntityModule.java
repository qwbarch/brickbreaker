package io.github.qwbarch.dagger.module;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import dagger.Module;
import dagger.Provides;
import io.github.qwbarch.entity.system.logic.MovementSystem;

@Module
public final class EntityModule {
    /**
     * Provider method for the entity component system world with all its systems enabled.
     * A new instance is created for every injection site (e.g. new world per screen).
     * @param movementSystem The movement system to use with the world.
     */
    @Provides
    public World provideWorld(MovementSystem movementSystem) {
        System.out.println("provideWorld");
        return new World(
            new WorldConfigurationBuilder()
                .with(movementSystem)
                .build()
        );
    }
}
