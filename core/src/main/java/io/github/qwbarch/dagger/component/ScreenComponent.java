package io.github.qwbarch.dagger.component;

import dagger.Component;
import io.github.qwbarch.dagger.scope.ScreenScope;
import io.github.qwbarch.screen.LoadingScreen;
import io.github.qwbarch.screen.ScreenHandler;

@Component
@ScreenScope
public interface ScreenComponent {
    ScreenHandler getScreenHandler();

    LoadingScreen getLoadingScreen();

    @Component.Factory
    interface Factory {
        ScreenComponent create();
    }
}
