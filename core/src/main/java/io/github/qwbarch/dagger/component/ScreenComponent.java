package io.github.qwbarch.dagger.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Subcomponent;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.dagger.module.EntityModule;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.screen.LoadingScreen;
import io.github.qwbarch.screen.ScreenHandler;

import javax.inject.Named;

@Component(
    dependencies = {ClientComponent.class},
    modules = {EntityModule.class}
)
@ScreenScope
public interface ScreenComponent {
    ScreenHandler getScreenHandler();

    LoadingScreen getLoadingScreen();

    AssetMap getAssets();

    @Component.Factory
    interface Factory {
        ScreenComponent create(
            ClientComponent clientComponent,
            @BindsInstance @Named("secondsPerTick") float secondsPerTick,
            @BindsInstance @Named("worldWidth") float worldWidth,
            @BindsInstance @Named("worldHeight") float worldHeight,
            @BindsInstance @Named("worldBackground") Color worldBackground,
            @BindsInstance @Named("brickSize") float brickSize,
            @BindsInstance @Named("ballSize") float ballSize,
            @BindsInstance @Named("ballVelocity") float ballVelocity,
            @BindsInstance @Named("startingBallSpawnX") float startingBallSpawnX,
            @BindsInstance @Named("startingBallSpawnY") float startingBallSpawnY,
            @BindsInstance @Named("paddleWidth") float paddleWidth,
            @BindsInstance @Named("paddleHeight") float paddleHeight,
            @BindsInstance @Named("paddleVelocity") float paddleVelocity,
            @BindsInstance @Named("paddleSpawnX") float paddleSpawnX,
            @BindsInstance @Named("paddleSpawnY") float paddleSpawnY,
            @BindsInstance @Named("leftLogo") String leftLogo,
            @BindsInstance @Named("rightLogo") String rightLogo,
            @BindsInstance @Named("logo") String logo
        );
    }
}
