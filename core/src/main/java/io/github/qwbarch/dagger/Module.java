package io.github.qwbarch.dagger;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Provides;
import io.github.qwbarch.entity.strategy.FixedTimestepInvocationStrategy;
import io.github.qwbarch.entity.system.*;

import javax.inject.Singleton;

@dagger.Module
public final class Module {
    /**
     * Provider method for the entity component system world with all its systems enabled.
     * A new instance is created for every injection site (e.g. new world per screen).
     */
    @Provides
    @Singleton
    public World provideWorld(
        FixedTimestepInvocationStrategy strategy,
        RenderSystem renderSystem,
        MovementCollisionSystem movementCollisionSystem,
        InputSystem inputSystem,
        DespawnSystem despawnSystem,
        GameOverSystem gameOverSystem
    ) {
        System.out.println("provideWorld");
        return new World(
            new WorldConfigurationBuilder()
                .with(inputSystem)
                .with(movementCollisionSystem)
                .with(despawnSystem)
                .with(gameOverSystem)
                .with(renderSystem)
                .register(strategy)
                .build()
        );
    }

    @Provides
    @Singleton
    public SpriteBatch provideSpriteBatch() {
        System.out.println("provideSpriteBatch");
        return new SpriteBatch();
    }

    @Provides
    @Singleton
    public GlyphLayout provideGlyphLayout() {
        System.out.println("provideGlyphLayout");
        return new GlyphLayout();
    }

    @Provides
    @Singleton
    public InputMultiplexer provideInputMultiplexer() {
        return new InputMultiplexer();
    }
}
