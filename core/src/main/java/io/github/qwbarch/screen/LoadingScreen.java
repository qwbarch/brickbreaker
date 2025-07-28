package io.github.qwbarch.screen;

import com.artemis.World;
import io.github.qwbarch.dagger.scope.ScreenScope;

import javax.inject.Inject;

@ScreenScope
public final class LoadingScreen implements Screen {
    private final ScreenHandler screenHandler;
    private final LevelScreen levelScreen;

    @Inject
    LoadingScreen(ScreenHandler screenHandler, LevelScreen levelScreen, World world) {
        this.screenHandler = screenHandler;
        this.levelScreen = levelScreen;
    }

    @Override
    public void show() {
        System.out.println("loading screen");
        screenHandler.setScreen(levelScreen);
    }
}
