package io.github.qwbarch.screen;

import com.artemis.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.qwbarch.asset.AssetMap;
import io.github.qwbarch.dagger.scope.ScreenScope;

import javax.inject.Inject;

@ScreenScope
public final class LoadingScreen implements Screen {
    private final ScreenHandler screenHandler;
    private final LevelScreen levelScreen;
    private final AssetMap assets;

    @Inject
    LoadingScreen(ScreenHandler screenHandler, LevelScreen levelScreen, AssetMap assets, World world, SpriteBatch batch) {
        this.screenHandler = screenHandler;
        this.levelScreen = levelScreen;
        this.assets = assets;
    }

    @Override
    public void show() {
        System.out.println("loading screen");
        assets.loadAssets();
    }

    @Override
    public void render() {
        assets.update();

        if (assets.isFinishedLoading()) {
            var music = assets.getBackgroundMusic();
            music.setLooping(0L, true);
            music.play();
            screenHandler.setScreen(levelScreen);
        }
    }
}
