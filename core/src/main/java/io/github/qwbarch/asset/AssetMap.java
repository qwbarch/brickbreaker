package io.github.qwbarch.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.qwbarch.dagger.scope.ScreenScope;

import javax.inject.Inject;

@ScreenScope
public final class AssetMap {
    private static final String BALL_TEXTURE_PATH = "ball.png";

    private final AssetManager assetManager = new AssetManager();
    private boolean finishedLoading = false;

    @Inject
    AssetMap() {
        System.out.println("AssetMap constructor");
    }

    /**
     * Begin loading all the game's assets.
     */
    public void loadAssets() {
        if (!finishedLoading) {
            assetManager.load(BALL_TEXTURE_PATH, Texture.class);
        }
    }

    /**
     * Update the current progress of the asset loading process.
     */
    public void update() {
        if (!finishedLoading) {
            // Blocks for at least 17 milliseconds to load assets faster.
            // This is recommended by the LibGDX wiki:
            // https://libgdx.com/wiki/managing-your-assets#optimize-loading
            finishedLoading = assetManager.update(17);
        }
    }

    /**
     * The progress of the asset loading.
     * @return A number from 0f to 1f.
     */
    public float getProgress() {
        return assetManager.getProgress();
    }

    public boolean isFinishedLoading() {
        return finishedLoading;
    }

    public Texture getBallTexture() {
        return assetManager.get(BALL_TEXTURE_PATH, Texture.class);
    }
}
