package io.github.qwbarch.screen;

import io.github.qwbarch.dagger.scope.ScreenScope;

import javax.inject.Inject;

@ScreenScope
public final class LoadingScreen implements Screen {
    private ScreenHandler screenHandler;

    @Inject
    LoadingScreen(ScreenHandler screenHandler) {
        this.screenHandler = screenHandler;
    }

    @Override
    public void show() {
        System.out.println("loading screen");
    }
}
