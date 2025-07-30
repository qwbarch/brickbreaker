package io.github.qwbarch.screen;

import io.github.qwbarch.asset.AssetMap;

import javax.inject.Inject;

public final class MenuScreen implements Screen {
    private final AssetMap assets;

    // Only play the BGM the first time the menu screen is shown.
    private boolean firstRun = true;

    @Inject
    MenuScreen(
        AssetMap assets
    ) {
        System.out.println("MenuScreen constructor");
        this.assets = assets;
    }

    @Override
    public void show() {
        if (firstRun) {
            firstRun = false;
            var music = assets.getBackgroundMusic();
            music.setLooping(0L, true);
            music.play();
        }
    }
}
