package io.github.qwbarch.dagger.component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Subcomponent;
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

    @Component.Factory
    interface Factory {
        ScreenComponent create(
            ClientComponent clientComponent,
            @BindsInstance @Named("secondsPerTick") float secondsPerTick
        );
    }
}
