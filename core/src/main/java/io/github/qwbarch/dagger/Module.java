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

/**
 * Provides class instances that dagger does not
 * know how to construct (classes that don't have the @Inject annotation).
 */
@dagger.Module
public final class Module {
    /**
     * Provider method for the entity component system world with all its systems enabled.
     * This is a singleton, so one instance is used for the entire game.
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

    /**
     * This is a singleton, so one instance is used for the entire game.
     */
    @Provides
    @Singleton
    public SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }

    /**
     * This is a singleton, so one instance is used for the entire game.
     */
    @Provides
    @Singleton
    public GlyphLayout provideGlyphLayout() {
        return new GlyphLayout();
    }

    /**
     * This is a singleton, so one instance is used for the entire game.
     */
    @Provides
    @Singleton
    public InputMultiplexer provideInputMultiplexer() {
        return new InputMultiplexer();
    }
}
