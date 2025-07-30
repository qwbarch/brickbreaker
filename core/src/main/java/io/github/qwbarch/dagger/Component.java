package io.github.qwbarch.dagger;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import dagger.BindsInstance;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.screen.LoadingScreen;
import io.github.qwbarch.screen.ScreenHandler;

import javax.inject.Named;
import javax.inject.Singleton;

@dagger.Component(modules = {Module.class})
@Singleton
public interface Component {
    ScreenHandler getScreenHandler();

    LoadingScreen getLoadingScreen();

    AssetMap getAssets();

    InputMultiplexer getInputMultiplexer();

    @dagger.Component.Factory
    interface Factory {
        Component create(
            @BindsInstance @Named("secondsPerTick") float secondsPerTick,
            @BindsInstance @Named("worldWidth") float worldWidth,
            @BindsInstance @Named("worldHeight") float worldHeight,
            @BindsInstance @Named("worldBackground") Color worldBackground,
            @BindsInstance @Named("brickSize") float brickSize,
            @BindsInstance @Named("ballSize") float ballSize,
            @BindsInstance @Named("ballVelocity") float ballVelocity,
            @BindsInstance @Named("ballSpawnVelocity") float ballSpawnVelocity,
            @BindsInstance @Named("startingBallSpawnX") float startingBallSpawnX,
            @BindsInstance @Named("startingBallSpawnY") float startingBallSpawnY,
            @BindsInstance @Named("paddleWidth") float paddleWidth,
            @BindsInstance @Named("paddleHeight") float paddleHeight,
            @BindsInstance @Named("paddleVelocity") float paddleVelocity,
            @BindsInstance @Named("paddleSpawnX") float paddleSpawnX,
            @BindsInstance @Named("paddleSpawnY") float paddleSpawnY,
            @BindsInstance @Named("leftLogo") String leftLogo,
            @BindsInstance @Named("rightLogo") String rightLogo,
            @BindsInstance @Named("logo") String logo,
            @BindsInstance @Named("spawnBallChance") float spawnBallChance
        );
    }
}
