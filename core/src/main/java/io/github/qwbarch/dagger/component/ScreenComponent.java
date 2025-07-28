package io.github.qwbarch.dagger.component;

import dagger.BindsInstance;
import dagger.Component;
import io.github.qwbarch.dagger.module.EntityModule;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.screen.LoadingScreen;
import io.github.qwbarch.screen.ScreenHandler;

import javax.inject.Named;

@Component(modules = { EntityModule.class })
@ScreenScope
public interface ScreenComponent {
    ScreenHandler getScreenHandler();

    LoadingScreen getLoadingScreen();

    @Component.Factory
    interface Factory {
        ScreenComponent create(@BindsInstance @Named("secondsPerTick") float secondsPerTick);
    }
}
