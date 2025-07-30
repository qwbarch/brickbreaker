package io.github.qwbarch.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import io.github.qwbarch.dagger.scope.ScreenScope;

import javax.inject.Inject;
import javax.inject.Named;

@ScreenScope
public final class AssetMap {
    private static final String BALL_TEXTURE_PATH = "ball.png";
    private static final String PADDLE_TEXTURE_PATH = "paddle.jpg";

    private static final String GREY_BRICK_TEXTURE_PATH = "grey-brick.jpg";
    private static final String RED_BRICK_TEXTURE_PATH = "red-brick.jpg";
    private static final String YELLOW_BRICK_TEXTURE_PATH = "yellow-brick.jpg";
    private static final String GREEN_BRICK_TEXTURE_PATH = "green-brick.jpg";

    private static final String BACKGROUND_MUSIC_PATH = "background-music.mp3";

    private static final String BALL_SPAWN_SOUND_PATH = "ball-spawn.wav";
    private static final String HARD_BOUNCE_SOUND_PATH = "hard-bounce.mp3";

    private static final String BUTTON_HOVER_SOUND_PATH = "button-hover.mp3";
    private static final String BUTTON_CLICK_SOUND_PATH = "button-click.mp3";

    private static final String MAIN_FONT_PATH = "Blanka-Regular.otf";

    private final AssetManager assetManager = new AssetManager();
    private final int logoFontSize;
    private boolean finishedLoading = false;

    @Inject
    AssetMap(@Named("logoFontSize") int logoFontSize) {
        System.out.println("AssetMap constructor");

        this.logoFontSize = logoFontSize;

        // Enable AssetManager to load fonts.
        var resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".otf", new FreetypeFontLoader(resolver));
    }

    /**
     * Begin loading all the game's assets asynchronously.
     */
    public void loadAssets() {
        if (!finishedLoading) {
            assetManager.load(BALL_TEXTURE_PATH, Texture.class);
            assetManager.load(PADDLE_TEXTURE_PATH, Texture.class);

            assetManager.load(GREY_BRICK_TEXTURE_PATH, Texture.class);
            assetManager.load(RED_BRICK_TEXTURE_PATH, Texture.class);
            assetManager.load(YELLOW_BRICK_TEXTURE_PATH, Texture.class);
            assetManager.load(GREEN_BRICK_TEXTURE_PATH, Texture.class);

            assetManager.load(BACKGROUND_MUSIC_PATH, Sound.class);
            assetManager.load(BALL_SPAWN_SOUND_PATH, Sound.class);
            assetManager.load(HARD_BOUNCE_SOUND_PATH, Sound.class);
            assetManager.load(BUTTON_HOVER_SOUND_PATH, Sound.class);
            assetManager.load(BUTTON_CLICK_SOUND_PATH, Sound.class);
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
     * Load the main font used for the logo and menu buttons. This blocks the thread and waits for the file to load.
     */
    public BitmapFont loadMainFont() {
        var params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = MAIN_FONT_PATH;
        params.fontParameters.size = logoFontSize;
        params.fontParameters.borderWidth = 5f;
        params.fontParameters.borderColor = Color.BLACK;
        assetManager.load(MAIN_FONT_PATH, BitmapFont.class, params);
        assetManager.finishLoadingAsset(MAIN_FONT_PATH);
        return getMainFont();
    }

    public BitmapFont getMainFont() {
        return assetManager.get(MAIN_FONT_PATH, BitmapFont.class);
    }

    public boolean isFinishedLoading() {
        return finishedLoading;
    }

    public Texture getBallTexture() {
        return assetManager.get(BALL_TEXTURE_PATH, Texture.class);
    }

    public Texture getPaddleTexture() {
       return assetManager.get(PADDLE_TEXTURE_PATH, Texture.class);
    }

    public Texture getGreyBrickTexture() {
        return assetManager.get(GREY_BRICK_TEXTURE_PATH, Texture.class);
    }

    public Texture getGreenBrickTexture() {
        return assetManager.get(GREEN_BRICK_TEXTURE_PATH, Texture.class);
    }

    public Texture getRedBrickTexture() {
        return assetManager.get(RED_BRICK_TEXTURE_PATH, Texture.class);
    }

    public Texture getYellowBrickTexture() {
        return assetManager.get(YELLOW_BRICK_TEXTURE_PATH, Texture.class);
    }

    public Sound getBackgroundMusic() {
        return assetManager.get(BACKGROUND_MUSIC_PATH, Sound.class);
    }

    public Sound getBallSpawnSound() {
       return assetManager.get(BALL_SPAWN_SOUND_PATH, Sound.class);
    }

    public Sound getHardBounceSound() {
        return assetManager.get(HARD_BOUNCE_SOUND_PATH, Sound.class);
    }

    public Sound getButtonHoverSound() {
        return assetManager.get(BUTTON_HOVER_SOUND_PATH, Sound.class);
    }

    public Sound getButtonClickSound() {
        return assetManager.get(BUTTON_CLICK_SOUND_PATH, Sound.class);
    }
}
