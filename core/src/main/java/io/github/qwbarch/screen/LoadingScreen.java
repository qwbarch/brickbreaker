package io.github.qwbarch.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.holidaystudios.tools.GifDecoder;
import io.github.qwbarch.asset.AssetMap;

import javax.inject.Inject;
import javax.inject.Named;

public final class LoadingScreen implements Screen {
    private final Animation<TextureRegion> loadingAnimation;

    private final SpriteBatch batch;
    private final ScreenHandler screenHandler;
    private final Screen menuScreen;
    private final AssetMap assets;
    private final BitmapFont font;
    private final String leftLogo;
    private final String rightLogo;

    private final float rightLogoWidth;
    private final float logoWidth;
    private final float logoHeight;

    private float elapsedTime;
    private boolean firstFrame = true;

    @Inject
    LoadingScreen(
        ScreenHandler screenHandler,
        GlyphLayout glyphLayout,
        MenuScreen menuScreen,
        AssetMap assets,
        SpriteBatch batch,
        @Named("leftLogo") String leftLogo,
        @Named("rightLogo") String rightLogo,
        @Named("logo") String logo
    ) {
        this.batch = batch;
        this.screenHandler = screenHandler;
        this.menuScreen = menuScreen;
        this.assets = assets;
        this.leftLogo = leftLogo;
        this.rightLogo = rightLogo;

        // Load the loading animation blocking the thread, since we need it immediately.
        loadingAnimation = GifDecoder.loadGIFAnimation(
            Animation.PlayMode.LOOP,
            Gdx.files.internal("loading.gif").read()
        );

        font = assets.loadMainFont();

        // Calculate dimensions of the right side of the logo.
        glyphLayout.setText(font, rightLogo);
        rightLogoWidth = glyphLayout.width;

        // Calculate dimensions of the combined logo.
        glyphLayout.setText(font, logo);
        logoWidth = glyphLayout.width;
        logoHeight = glyphLayout.height;
    }

    @Override
    public void hide() {
        screenHandler.remove(this);
    }

    @Override
    public void render() {
        var screenWidth = Gdx.graphics.getWidth();
        var screenHeight = Gdx.graphics.getHeight();

        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.begin();

        // Draw the current loading animation frame.
        var loadingFrame = loadingAnimation.getKeyFrame(elapsedTime);
        batch.draw(
            loadingFrame,
            Gdx.graphics.getWidth() / 2f - loadingFrame.getRegionWidth() / 1.9f,
            Gdx.graphics.getHeight() / 2f - loadingFrame.getRegionHeight() / 2f,
            loadingFrame.getRegionWidth(),
            loadingFrame.getRegionHeight()
        );

        // Draw the left logo.
        font.setColor(212f / 255f, 83f / 255f, 83f / 255f, 1f);
        var leftLogoX = screenWidth / 2f - logoWidth / 2f;
        var logoY = screenHeight / 3f * 2f + logoHeight / 2f;
        font.draw(batch, leftLogo, leftLogoX, logoY);

        // Draw the right logo.
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(
            batch,
            rightLogo,
            leftLogoX + logoWidth - rightLogoWidth,
            logoY
        );

        batch.end();

        if (firstFrame) {
            firstFrame = false;
            assets.loadAssets();
        } else {
            assets.update();
            if (assets.isFinishedLoading()) {
                screenHandler.setScreen(menuScreen);
            }
        }
    }
}
